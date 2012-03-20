package carpgenetique.comportement.ame;

import carpgenetique.agent.AMEPop;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import seisco.util.MessageCodec;

/**
 * <p>Ce comportement envoi la solution demandée par un AME
 * (une fois la récupération de l'individu en cache réalisé).
 * <p>ACL Message ID utilisé :
 * <p>ID_SOL_ENVOI_AME
 * 
 * @author Jerome
 * @version 2012
 */
public class PresenterSolutionAME extends CyclicBehaviour {

    private AMEPop ame;
    
    public PresenterSolutionAME(AMEPop a) {
        super(a);
        this.ame = a;
    }
    
    @Override
    public void action() {
        if((Boolean)ame.getEtat("envoiSolution").getValeur() && (Boolean)ame.getEtat("solutionPresente").getValeur()) {      
            if(ame.getCacheDemandeurSolution()!=null) {
                MessageHelper rep = new MessageHelper();
                rep.create(ACLMessage.INFORM, MessageHelper.ID_SOL_ENVOI_AME);
                rep.addReceiver(ame.getCacheDemandeurSolution());
            
                try {
                    ame.send(rep.get(MessageCodec.encode(ame.getCacheSolution())));
                } catch(Exception ex) {
                    ame.println("Erreur: Impossible de sérialiser l'individu.\n\t(Raison: "+ex.getMessage()+")");
                }
            }
            
            ame.setEtat("envoiSolution", false);
            ame.setCacheDemandeurSolution(null);
        } else 
            block(1000);
    }

}
