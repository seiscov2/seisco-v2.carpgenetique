package carpgenetique.agent;

import carpgenetique.algo.Individu;
import carpgenetique.comportement.ame.*;
import java.util.ArrayList;
import java.util.List;
import seisco.agent.AgentMobileEchange;
import seisco.comportement.ame.RecupererSolution;
import seisco.util.Etat;

/**
 * <p>L'AME Population est une extension de l'AgentMobileEchange qui est propre 
 * à l'algoritme Génétique pour une résolution de problème CARP.
 * <p>Il gère notament l'échange d'{@link Individu} au moyen d'une fitness.
 * 
 * @author Jerome
 * @version 2012
 */
public class AMEPop extends AgentMobileEchange {
    
    protected float cacheFitness;
    protected List<Individu> recSolutions;
    
    /**
     * <p>Initilisation de l'agent.
     * <ul>
     *  <li>Initilisation des Etats
     *  <li>Initilisation des comportements
     * </ul>
     * 
     * @since 2012
     * @see Etat
     * @see RecupererSolution
     * @see EcouterDemandeSolutionAMC
     * @see EcouterUpdateSolution
     * @see EcouterDemandeSolutionAME
     * @see PresenterSolutionAME
     * @see EcouterSolutionAME
     * @see PresenterSolutionAME
     * @see PresenterSolutionsAMC
     * @see EcouterConfiguration
     */
    @Override
    public void setup() {
        super.setup();
        
        this.cacheFitness = 0;
        
        this.recSolutions = new ArrayList<Individu>();
        
        // Initialisation des états
        this.etats.add(new Etat("demandeSolution", false));
        this.etats.add(new Etat("solutionPresente", false));
        this.etats.add(new Etat("solutionsRecues", false));
        this.etats.add(new Etat("demandeSolutionATE", false));
        
        // Comportement lié à l'AMC
        addBehaviour(new RecupererSolution(this));
        addBehaviour(new EcouterDemandeSolutionAMC(this));
        addBehaviour(new EcouterUpdateSolution(this));
        
        // Comportement lié aux AME     
        addBehaviour(new EcouterDemandeSolutionAME(this)); 
        addBehaviour(new PresenterSolutionAME(this));
        addBehaviour(new EcouterSolutionAME(this));
        addBehaviour(new PresenterSolutionAME(this));
        addBehaviour(new PresenterSolutionsAMC(this));
        
        // Comportement lié à l'ATE
        addBehaviour(new EcouterConfiguration(this));
    }

    /**
     * <p>Retourne la fitness en cache.
     * 
     * @return La fitness en cache.
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
     * <p>Retourne les solutions recues des autres AMEs.
     * 
     * @return Une liste de solutions
     * @since 2012
     * @see #setRecSolutions(List)
     */
    public List<Individu> getRecSolutions() {
        return recSolutions;
    }

    /**
     * <p>Remplace les solutions recues des autres AMEs.
     * 
     * @param recSolutions
     *      La nouvelle liste de solutions
     * @since 2012
     * @see #getRecSolutions() 
     */
    public void setRecSolutions(List<Individu> recSolutions) {
        this.recSolutions = recSolutions;
    }
}
