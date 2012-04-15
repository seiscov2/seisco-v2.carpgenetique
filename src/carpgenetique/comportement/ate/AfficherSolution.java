package carpgenetique.comportement.ate;

import carp.SolutionCARP;
import carpgenetique.agent.ATEPop;
import carpgenetique.algo.Individu;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import seisco.probleme.Solution;

/**
 * <p>Affiche la solution finale lorsque tous les agents sont détruits.
 * 
 * @author Jerome
 * @version 2012
 */
public class AfficherSolution extends Behaviour {

    private ATEPop ate;
    private boolean done;
    
    public AfficherSolution(ATEPop a) {
        super(a);
        this.ate = a;
        this.done = false;
    }

    @Override
    public void action() {
        if(ate.getNbAgentsArretes() >= ate.getNbAgents()) {
            Solution ind = ate.getCacheSolution();
            if(ind != null) {
                FileOutputStream fos = null;
                PrintStream out = null;
                try {
                    fos = new FileOutputStream("resultats.txt");
                    out = new PrintStream(fos);
                    ate.println(((SolutionCARP)ind).getResultat().toString(), out);
                    
                    this.done = true;
                } catch(FileNotFoundException ex) {
                    Logger.getLogger(AfficherSolution.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        fos.close();
                        out.close();
                    } catch(IOException ex) {
                        Logger.getLogger(AfficherSolution.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                ate.println("Aucune solution à afficher");
            }
        } else
            block(1000);
    }

    @Override
    public boolean done() {
        return this.done;
    }
}
