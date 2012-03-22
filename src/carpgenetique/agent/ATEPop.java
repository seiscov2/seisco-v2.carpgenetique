package carpgenetique.agent;

import carp.ProblemeCARP;
import carpgenetique.algo.Individu;
import carpgenetique.comportement.ate.*;
import jade.core.AID;
import jade.core.ContainerID;
import jade.wrapper.AgentController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import seisco.agent.AgentTransversalEchange;
import seisco.algo.JeuParametres;
import seisco.util.Etat;
import seisco.util.Parametre;

/**
 * <p>L'ATE Population est une extension de l'AgentTransersalEchange qui est propre 
 * à l'algoritme Génétique pour une résolution de problème CARP.
 * <p>Il gère notament l'échange d'{@link Individu} au moyen d'une fitness au niveau inter plateforme.
 * <p>C'est également lui qui a pour rôle l'intialisation des autres agents, leur configuration et leur déploiement.
 * 
 * @author Jerome
 * @version 2012
 */
public class ATEPop extends AgentTransversalEchange {
    protected ProblemeCARP probleme=null;
    protected JeuParametres jp=null;
    
    protected float cacheFitness;
    protected List<Individu> recSolutions;
    protected AID amcSol;
    
    protected List<String> op_croisement;
    protected List<String> op_mutation;
    
    protected HashMap<String, Boolean> amc;
    protected List<AgentController> ameController;
    protected List<ContainerID> ameContainer;
    protected int nbAgents;
    
    /**
     * <p>Initilisation de l'agent.
     * <ul>
     *  <li>Chargement du problème CARP
     *  <li>Chargement des opérateurs
     *  <li>Chargement des parametres
     *  <li>Lancement des agents
     * </ul>
     * 
     * @version 2012
     * @see ProblemeCARP
     * @see Etat
     * @see JeuParametres
     * @see InitialiserAgents
     * @see DeplacementAME
     * @see EcouterUpdateSolution
     * @see EcouterDemandeSolutionAME
     * @see EcouterDemandeSolutionATE
     * @see PresenterSolutionATE
     * @see PresenterSolutionsAMC
     * @see EcouterSolutionATE
     * @see AfficherSolution
     */
    @Override
    public void setup() {
        super.setup();

        // Cache
        this.cacheFitness = Float.POSITIVE_INFINITY;
        this.recSolutions = new ArrayList<Individu>();
        this.amcSol = null;
        
        // Chargement du problème
        try {
            probleme = ProblemeCARP.loadFromFile(this.conf.getString("carp.probleme", "conf" + System.getProperty("file.separator") + "prob.conf"));
        } catch (IOException ex) {
            System.out.println("Erreur: " + ex.getMessage());
        }
        
        // Chargement des opérateurs
        this.op_croisement = new ArrayList<String>();
        this.op_mutation = new ArrayList<String>();
        
        if(this.conf.isList("carpgen.operateur.croisement"))
            this.op_croisement = this.conf.getStringList("carpgen.operateur.croisement");
        
        if(this.conf.isList("carpgen.operateur.mutation"))
            this.op_mutation = this.conf.getStringList("carpgen.operateur.mutation");
        
        // Chargement des parametres 
        JeuParametres jeuparam = new JeuParametres();
        jeuparam.addParametre(new Parametre("se_pourc_min", this.conf.getInt("carpgen.se.pourc_min", 10)));
        jeuparam.addParametre(new Parametre("se_pourc_max", this.conf.getInt("carpgen.se.pourc_max", 90)));
        jeuparam.addParametre(new Parametre("generation", this.conf.getInt("carpgen.parametre.generation", 10000)));
        jeuparam.addParametre(new Parametre("gen_cons", this.conf.getInt("carpgen.parametre.gen_cons", 20)));
        jeuparam.addParametre(new Parametre("stagne_dem", this.conf.getInt("carpgen.parametre.stagne_dem", 10)));
        jeuparam.addParametre(new Parametre("population", this.conf.getInt("carpgen.parametre.population", 400)));
        jeuparam.addParametre(new Parametre("prob_crois", (float)this.conf.getDouble("carpgen.parametre.prob_crois", 0.6)));
        jeuparam.addParametre(new Parametre("prob_muta" , (float)this.conf.getDouble("carpgen.parametre.prob_muta", 0.4)));
        jeuparam.addParametre(new Parametre("survie"    , (float)this.conf.getDouble("carpgen.parametre.survie", 0.5)));
        jeuparam.addParametre(new Parametre("noclone_type", this.conf.getString("carpgen.parametre.noclone_type", "none")));
        this.jp = jeuparam;
        
        // Création des agents
        this.nbAgents = this.conf.getInt("carpgen.nb_agents", 1);
        this.amc = new HashMap<String, Boolean>();
        this.ameController = new ArrayList<AgentController>();
        this.ameContainer = new ArrayList<ContainerID>();
        addBehaviour(new InitialiserAgents(this));
        
        // Déplacement des agents vers leur container
        addBehaviour(new DeplacementAME(this));
        
        // Comportement lié à l'update et l'affichage de Solution
        addBehaviour(new EcouterUpdateSolution(this));
        addBehaviour(new AfficherSolution(this));
        
        // Comportement lié à l'échange inter-plateforme
        addBehaviour(new EcouterDemandeSolutionAME(this));
        addBehaviour(new EcouterDemandeSolutionATE(this));
        addBehaviour(new PresenterSolutionATE(this));
        addBehaviour(new EcouterSolutionATE(this));
        addBehaviour(new PresenterSolutionsAMC(this));
    }

    /**
     * <p>Retourne l'état de l'AMC (initialisé ou non).
     * 
     * @param nom
     *      Nom de l'AMC (correspond à l'AID)
     * @return <code>true</code> si l'agent est prêt, ou <code>false</code> si non prêt ou non existant.
     * @since 2012
     * @see #setAMCState(String, Boolean) 
     */
    public boolean getAMCState(String nom) {
        Boolean val = this.amc.get(nom);
        if(val != null)
            return val;
        
        return false;
    }
    
    /**
     * <p>Remplace l'état de l'AMC.
     * 
     * @param nom
     *      Nom de l'AMC
     * @param valeur 
     *      Nouvelle valeur
     * @since 2012
     * @see #getAMCState(String) 
     */
    public void setAMCState(String nom, Boolean valeur) {
        this.amc.put(nom, valeur);
    }
    
    /**
     * <p>Retourne le Containeur de l'AME.
     * @param i
     *      Le numéro de l'AME.
     * @return Le containeur de l'AME ou le <b>Main-Container</b> en cas d'erreur.
     * @since 2012
     */
    public ContainerID getLocationAme(int i) {
        ContainerID loc;
        
        try {
            loc = this.ameContainer.get(i);
        } catch(IndexOutOfBoundsException e) {
            loc = new ContainerID();
            loc.setName("Main-Container");
        }
       
        return loc;
    }
    
    /**
     * <p>Retourne le controlleur de l'AME.
     * 
     * @param i
     *      Le numéro de l'AME
     * @return Le controlleur de l'AME ou <code>null</code> s'il n'existe pas.
     * @since 2012
     */
    public AgentController getControllerAme(int i) {
        AgentController ac;
        
        try {
            ac = this.ameController.get(i);
        } catch(IndexOutOfBoundsException e) {
            ac = null;
        }
       
        return ac;
    }

    /**
     * <p>Ajoute le containeur d'un AME à la liste.
     * 
     * @param ameContainer
     *      Le nouveau containeur à ajouter
     * @return <code>true</code> si le containeur a été ajouté, sinon <code>false</code>
     * @since 2012
     */
    public boolean addAmeContainer(ContainerID ameContainer) {
        return this.ameContainer.add(ameContainer);
    }

    /**
     * <p>Ajoute le controlleur d'un AME à la liste.
     * 
     * @param ameController
     *      Le nouveau controlleur à ajouter
     * @return <code>true</code> si le controlleur a été ajouté, sinon <code>false</code>
     * @since 2012
     */
    public boolean addAmeController(AgentController ameController) {
        return this.ameController.add(ameController);
    }
    
    /**
     * <p>Retourne le nombre d'agents (couple AMC/AME).
     * 
     * @return Un entier qui représente le nombre d'agent
     * @since 2012
     * @see #setNbAgents(int) 
     */
    public int getNbAgents() {
        return nbAgents;
    }

    /**
     * <p>Remplace le nombre d'agents.
     * 
     * @param nbAgents 
     *      Le nouveau nombre d'agents
     * @since 2012
     * @see #getNbAgents() 
     */
    public void setNbAgents(int nbAgents) {
        this.nbAgents = nbAgents;
    }

    /**
     * <p>Retourne le jeu de parametres.
     * 
     * @return Le jeu de parametres
     * @since 2012
     * @see JeuParametres
     * @see #setJp(JeuParametres)
     */
    public JeuParametres getJp() {
        return jp;
    }

    /**
     * <p>Remplace le jeu de parametres.
     * 
     * @param jp 
     * @since 2012
     * @see JeuParametres
     * @see #setJp(JeuParametres)
     */
    public void setJp(JeuParametres jp) {
        this.jp = jp;
    }

    /**
     * <p>Retourne la liste des opérateurs de croisement.
     * 
     * @return La liste des opérateurs
     * @since 2012
     * @see #setOp_croisement(List) 
     */
    public List<String> getOp_croisement() {
        return op_croisement;
    }

    /**
     * <p>Remplace la liste des opérateurs de croisement.
     * 
     * @param op_croisement 
     *      La nouvelle liste d'opérateurs
     * @since 2012
     * @see #getOp_croisement() 
     */
    public void setOp_croisement(List<String> op_croisement) {
        this.op_croisement = op_croisement;
    }

    /**
     * <p>Retourne la liste des opérateurs de mutation.
     * 
     * @return La liste des opérateurs
     * @since 2012
     * @see #setOp_mutation(List) 
     */
    public List<String> getOp_mutation() {
        return op_mutation;
    }

    /**
     * <p>Remplace la liste des opérateurs de mutation.
     * 
     * @param op_mutation 
     *      La nouvelle liste d'opérateurs
     * @since 2012
     * @see #getOp_mutation() 
     */
    public void setOp_mutation(List<String> op_mutation) {
        this.op_mutation = op_mutation;
    }

    /**
     * <p>Retourne le Probleme CARP.
     * 
     * @return Le problème carp.
     * @since 2012
     * @see #setProbleme(ProblemeCARP) 
     * @see ProblemeCARP
     */
    public ProblemeCARP getProbleme() {
        return probleme;
    }

    /**
     * <p>Remplace le problème CARP.
     * 
     * @param probleme 
     *      Le nouveau problème
     * @since 2012
     * @see #getProbleme()
     * @see ProblemeCARP
     */
    public void setProbleme(ProblemeCARP probleme) {
        this.probleme = probleme;
    }

    /**
     * <p>Retourne la fitness mise en cache.
     * 
     * @return La fitness en cache
     * @since 2012
     * @see #setCacheFitness(float) 
     */
    public float getCacheFitness() {
        return cacheFitness;
    }

    /**
     * <p>Remplace la fitness en cache.
     * 
     * @param cacheFitness 
     *      La nouvelle fitness
     * @since 2012
     * @see #getCacheFitness() 
     */
    public void setCacheFitness(float cacheFitness) {
        this.cacheFitness = cacheFitness;
    }

    /**
     * <p>Retourne la liste d'états de tous les AMCs.
     * 
     * @return Une HashMap avec comme clé le nom de l'amc et comme valeur son état
     * @since 2012
     * @see #setAmc(HashMap) 
     */
    public HashMap<String, Boolean> getAmc() {
        return amc;
    }

    /**
     * <p>Remplace la liste d'états des AMCs.
     * 
     * @param amc 
     *      La nouvelle liste d'états
     * @since 2012
     * @see #getAmc() 
     */
    public void setAmc(HashMap<String, Boolean> amc) {
        this.amc = amc;
    }
    
    /**
     * <p>Retourne les solutions recues des autres ATEs.
     * 
     * @return Une liste de solutions
     * @since 2012
     * @see #setRecSolutions(List)
     */
    public List<Individu> getRecSolutions() {
        return recSolutions;
    }

    /**
     * <p>Remplace les solutions recues des autres ATEs.
     * 
     * @param recSolutions
     *      La nouvelle liste de solutions
     * @since 2012
     * @see #getRecSolutions() 
     */
    public void setRecSolutions(List<Individu> recSolutions) {
        this.recSolutions = recSolutions;
    }

    /**
     * <p>Retourne l'AID de l'AMC faisant une demande de solution.
     * 
     * @return L'AID de l'AMC
     * @since 2012
     * @see #setAmcSol(AID) 
     */
    public AID getAmcSol() {
        return amcSol;
    }

    /**
     * <p>Remplace l'AID de l'AMC faisant une demande.
     * 
     * @param amcSol 
     *      Le nouvel AID
     * @since 2012
     * @see #getAmcSol() 
     */
    public void setAmcSol(AID amcSol) {
        this.amcSol = amcSol;
    }
    
}
