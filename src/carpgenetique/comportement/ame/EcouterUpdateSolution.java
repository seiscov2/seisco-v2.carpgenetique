package carpgenetique.comportement.ame;

import carpgenetique.agent.AMEPop;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * <p>Ce comportement écoute les mises à jour du fitness de l'AMC et remplace le cache.
 * <p>ACL Message utilisé :
 * <p>ID_UPDATESOL_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterUpdateSolution extends CyclicBehaviour {
    private AMEPop ame;
    
    public EcouterUpdateSolution(AMEPop a) {
        super(a);
        this.ame = a;
    }
    
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_UPDATESOL_AMC);
        ACLMessage msgRecu = myAgent.receive(mt);
        
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.INFORM) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    float fitness = Float.parseFloat(msgRecu.getContent());
                    ame.setCacheFitness(fitness);
                    ame.setCacheSolution(null);
                    ame.setEtat("solutionPresente", false);
                }
            }
        } else
            block(1000);
    }

}
