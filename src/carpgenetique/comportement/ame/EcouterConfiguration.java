package carpgenetique.comportement.ame;

import carpgenetique.agent.AMEPop;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import seisco.util.MessageCodec;

/**
 * <p>Ce comportement écoute la configuration envoyée par l'ATE.
 * <p>ACL Message ID utilisé :
 * <p>ID_CONT_AME
 * @author Jerome
 */
public class EcouterConfiguration extends Behaviour {

    private AMEPop ame;
    private boolean done;
    public EcouterConfiguration(AMEPop a) {
        super(a);
        this.ame = a;
        this.done = false;
    }
    
    @Override
    public void action() {
        
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_CONT_AME);
        
        ACLMessage msgRecu = myAgent.receive(mt);

        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.INFORM) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    try {
                        ArrayList cont;
                        cont = MessageCodec.decode(msgRecu.getContent(), ArrayList.class);
                        
                        ame.setNextMachines(cont);
                        
                        ame.setATE(msgRecu.getSender());
                        
                        this.done = true;
                    } catch(Exception ex) {
                        ame.println("Erreur: Impossible de lire la liste des containeurs.");
                    }
                }
            }
        } else
            block(1000);
    }

    @Override
    public boolean done() {
        return this.done;
    }

}
