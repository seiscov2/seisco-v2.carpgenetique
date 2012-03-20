package carpgenetique.agent;

import carpgenetique.comportement.amc.*;
import seisco.agent.AgentMobileCalcul;
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
}