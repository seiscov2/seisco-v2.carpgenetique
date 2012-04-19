package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import seisco.util.ObjectCodec;

/**
 * <p>Ce comportement envoi la solution demandée par un ATE
 * (une fois la récupération de l'individu en cache réalisé).
 * <p>ACL Message ID utilisé :
 * <p>ID_SOL_ENVOI_ATE
 * 
 * @author Jerome
 * @version 2012
 */
public class PresenterSolutionATE extends CyclicBehaviour {

    private ATEPop ate;
    
    public PresenterSolutionATE(ATEPop a) {
        super(a);
        this.ate = a;
    }
    
    @Override
    public void action() {
        if((Boolean)ate.getEtat("envoiSolution").getValeur() && (Boolean)ate.getEtat("solutionPresente").getValeur()) {      
            if(ate.getCacheDemandeurSolution()!=null) {
                MessageHelper rep = new MessageHelper();
                rep.create(ACLMessage.INFORM, MessageHelper.ID_SOL_ENVOI_ATE);
                rep.addReceiver(ate.getCacheDemandeurSolution());
            
                try {
                    ate.send(rep.get(ObjectCodec.encode(ate.getCacheSolution())));
                } catch(Exception ex) {
                    ate.println("Erreur: Impossible de sérialiser l'individu.\n\t(Raison: "+ex.getMessage()+")");
                }
            }
            
            ate.setEtat("envoiSolution", false);
            ate.setCacheDemandeurSolution(null);
        }
    }

}
