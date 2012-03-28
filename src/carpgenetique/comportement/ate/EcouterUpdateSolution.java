package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * <p>Ce comportement écoute les mises à jour du fitness de l'AMC et remplace le cache.
 * <p>ACL Message ID utilisé :
 * <p>ID_UPDATESOL_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterUpdateSolution extends CyclicBehaviour {
    private ATEPop ate;
    
    public EcouterUpdateSolution(ATEPop a) {
        super(a);
        this.ate = a;
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
                    
                    if(fitness < ate.getCacheFitness()) {
                        ate.setCacheFitness(fitness);
                        ate.setCacheSolution(null);
                        ate.setCacheAMC(msgRecu.getSender());
                        ate.setEtat("solutionPresente", false);
                        ate.println("Nouveau fitness !");
                        ate.println("\t" + fitness + " @ " + msgRecu.getSender().getLocalName());
                    } 
                }
            }
        } else
            block(2000);
    }

}
