package carpgenetique.agent;

import carp.SolutionCARP;
import carpgenetique.algo.AlgoGenCARP;
import carpgenetique.algo.Individu;
import carpgenetique.comportement.amc.*;
import carpgenetique.comportement.ate.AfficherSolution;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import seisco.agent.AgentMobileCalcul;
import seisco.util.DateHelper;
import seisco.util.Etat;

/**
 * <p>L'AMC Population est une extension de l'AgentMobileCalcul qui est propre 
 * à l'algoritme Génétique pour une résolution de problème CARP.
 * 
 * @author Jerome
 * @version 2012
 */
public class AMCPop extends AgentMobileCalcul {
    protected float lastFitness;
    protected int countFitness;
            
    protected Date debutExecution;
    protected Date finExecution;
    protected long timeExecAlgo;
    
    /**
     * <p>Initilisation de l'agent.
     * <ul>
     *  <li>Initilisation des Etats
     *  <li>Initilisation des comportements
     * </ul>
     * 
     * @since 2012
     * @see Etat
     * @see EcouterProbleme
     * @see EcouterParametres
     * @see EcouterOperateurs
     * @see ConfirmerInitialisation
     * @see DemandeIndividu
     * @see UpdateSolution
     * @see ReceptionIndividus
     */
    @Override
    public void setup() {
        super.setup();
        
        this.lastFitness = 0;
        this.countFitness = 0;
        
        this.debutExecution = new Date();
        this.timeExecAlgo = 0;
        
        this.etats.add(new Etat("demandeSolution", false));
        this.etats.add(new Etat("attenteSolution", false));
        this.etats.add(new Etat("presenteSolution", false));
        
        // Comportements liés à l'initialisation
        addBehaviour(new EcouterProbleme(this));
        addBehaviour(new EcouterParametres(this));
        addBehaviour(new EcouterOperateurs(this));
        addBehaviour(new ConfirmerInitialisation(this));
        
        // Comportement liés au partage de solution
        addBehaviour(new DemandeIndividu(this));
        addBehaviour(new UpdateSolution(this));
        addBehaviour(new ReceptionIndividus(this));
    }
    
    /**
     * <p>Action après le déplacement de l'agent.
     * <p>Déclaration du comportement d'exécution de l'algorithme.
     * 
     * @since 2012
     */
    @Override
    public void afterMove() {
        super.afterMove();
        
        // Lancement de l'algo
        addBehaviour(new ExecuterAlgorithme(this));
    }
    
    /**
     * <p>Retourne le nombre consécutif d'exécution où la fitness est identique.
     * 
     * @return Le nombre d'exécution consécutive
     * @since 2012
     * @see #setCountFitness(int) 
     */
    public int getCountFitness() {
        return countFitness;
    }

    /**
     * <p>Remplace le nombre consécutif d'exécution où la fitness est identique.
     * 
     * @param countFitness
     *      Le nouveau nombre
     * @since 2012
     * @see #getCountFitness() 
     */
    public void setCountFitness(int countFitness) {
        this.countFitness = countFitness;
    }

    /**
     * <p>Retourne la dernière meilleure fitness calculée.
     * 
     * @return La dernière fitness
     * @since 2012
     * @see #setLastFitness(float) 
     */
    public float getLastFitness() {
        return lastFitness;
    }

    /**
     * <p>Remplace la dernière fitness.
     * 
     * @param lastFitness 
     *      La nouvelle fitness
     * @since 2012
     * @see #getLastFitness() 
     */
    public void setLastFitness(float lastFitness) {
        this.lastFitness = lastFitness;
    }
    
    /**
     * <p>Retourne le temps en millisecode du début de l'exécution de l'agent.
     * 
     * @return 
     *      Le temps en millisecondes
     * @since 2012
     */
    public long getDebutExecution() {
        return this.debutExecution.getTime();
    }
    
    /**
     * <p>Exécuté à l'arrêt de l'agent.
     * 
     * @since 2012
     */
    @Override
    public void takeDown() {
        super.takeDown();
        
        FileOutputStream fos = null;
        PrintStream out = null;
        try {
            fos = new FileOutputStream("amc.txt");
            out = new PrintStream(fos);
            Individu sol = (Individu)this.cacheSolution;
            
            println(this.getLocalName() + " - " + sol.getFitness(), out);
            println("Temps total: " + DateHelper.formatMillisecondes(finExecution.getTime()-debutExecution.getTime()), out);
            println("\tAlgo : " + DateHelper.formatMillisecondes(this.timeExecAlgo), out);
            println("\tfonctionObjectif : " + DateHelper.formatMillisecondes(algo.getTimeObjectiveFunction()), out);
        } catch(FileNotFoundException ex) {
            Logger.getLogger(AMCPop.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Exception e) {
            println("Erreur lors de l'arret: \n" + e.getMessage());
        } finally {
            try {
                fos.close();
                out.close();
            } catch(IOException ex) {
                Logger.getLogger(AMCPop.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * <p>Remplace le temps de fin de l'exécution total de l'agent.
     * 
     * @param date
     *      La nouvelle date de fin
     * @since 2012
     * @see #getFinExecution() 
     */
    public void setFinExecution(Date date) {
        this.finExecution = date;
    }

    /**
     * <p>Retourne le temps en millisecode de fin de l'exécution total de l'agent.
     * 
     * @return 
     *      Le temps en millisecondes
     * @since 2012
     * @see #setFinExecution(Date) 
     */
    public long getFinExecution() {
        return this.finExecution.getTime();
    }
    
    /**
     * <p>Remplace le temps d'exécution de l'algo
     * 
     * @param time
     *      La nouvelle durée d'exécution de l'algo
     * @since 2012
     * @see #getAlgoExecTime() 
     */
    public void setAlgoExecTime(long time) {
        this.timeExecAlgo = time;
    }

    /**
     * <p>Retourne le temps en millisecode d'exécution de l'algo
     * 
     * @return 
     *      Le temps en millisecondes
     * @since 2012
     * @see #setAlgoExecTime(long) 
     */
    public long getAlgoExecTime() {
        return this.timeExecAlgo;
    }
}