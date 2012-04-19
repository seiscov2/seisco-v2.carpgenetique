package carpgenetique.comportement.ame;

import carpgenetique.agent.AMEPop;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;
import seisco.util.ObjectCodec;

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

    private AMEPop ame;
    
    public PresenterSolutionsAMC(AMEPop a) {
        super(a);
        this.ame = a;
    }
    
    @Override
    public void action() {
        if((Boolean)ame.getEtat("solutionsRecues").getValeur()) {
            ArrayList<Individu> inds = (ArrayList<Individu>)ame.getRecSolutions();
            
            MessageHelper mh = new MessageHelper();
            try {
                if(inds.isEmpty()) { // SI LISTE VIDE : FAIRE UNE DEMANDE A L'ATE
                    mh.create(ACLMessage.QUERY_IF, MessageHelper.ID_SOL_DEMANDE);
                    mh.addReceiver(ame.getATE());
                    ame.send(mh.get(String.valueOf(ame.getCacheFitness())));
                } else {
                    mh.create(ACLMessage.INFORM, MessageHelper.ID_SOL_ENVOI_AMC);
                    mh.addReceiver(ame.getAMC());
                    ame.send(mh.get(ObjectCodec.encode(inds)));
                }
            } catch(Exception ex) {
                ame.println("Erreur: Impossible d'envoyer les solutions.\n\t(Raison: "+ex.getMessage()+")");
            }
            
            ame.setRecSolutions(new ArrayList<Individu>());
            ame.setEtat("solutionsRecues", false);
        }
    }

}
