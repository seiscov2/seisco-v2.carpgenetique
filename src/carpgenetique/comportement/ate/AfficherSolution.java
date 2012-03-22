package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.algo.Individu;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import seisco.probleme.Solution;

/**
 * <p>Affiche la solution finale lorsque tous les agents sont détruits.
 * 
 * @author Jerome
 * @version 2012
 */
public class AfficherSolution extends Behaviour {

    private ATEPop ate;
    
    public AfficherSolution(ATEPop a) {
        super(a);
        this.ate = a;
    }

    @Override
    public void action() {
        if(ate.getNbAgentsArretes() >= ate.getNbAgents()) {
            Solution ind = ate.getCacheSolution();
            if(ind != null) {
                ind.afficher();
            } else {
                ate.println("Aucune solution à afficher");
            }
        } else
            block(1000);
    }

    @Override
    public boolean done() {
        return ate.getNbAgentsArretes() >= ate.getNbAgents();
    }

}