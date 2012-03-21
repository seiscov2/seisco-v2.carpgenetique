package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import seisco.util.MessageCodec;

/**
 * <p>Ce comportement envoi la liste des solutions, recues des autres AMEs, à l'AMC.
 * <p>Ce omportement est également chargé de faire une demande à l'ATE en cas 
 * de réponse négative de la part de tous les AMEs.
 * <p>ACL Message ID :
 * <p>ID_SOL_ENVOI_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class PresenterSolutionsAMC extends CyclicBehaviour {

    private ATEPop ate;
    
    public PresenterSolutionsAMC(ATEPop a) {
        super(a);
        this.ate = a;
    }
    
    @Override
    public void action() {
        if((Boolean)ate.getEtat("solutionsRecues").getValeur()) {
            ArrayList<Individu> inds = (ArrayList<Individu>)ate.getRecSolutions();
            
            MessageHelper mh = new MessageHelper();
            mh.create(ACLMessage.INFORM, MessageHelper.ID_SOL_ENVOI_AMC);
            mh.addReceiver(ate.getAmcSol());
            try {
                if(inds.isEmpty()) {
                    ate.send(mh.get(MessageCodec.encode("no_best")));
                } else {
                    ate.send(mh.get(MessageCodec.encode(inds)));
                }
            } catch(Exception ex) {
                ate.println("Erreur: Impossible d'envoyer les solutions.\n\t(Raison: "+ex.getMessage()+")");
            }
            
            ate.setRecSolutions(new ArrayList<Individu>());
            ate.setEtat("solutionsRecues", false);
        } else 
            block(1000);
    }

}
