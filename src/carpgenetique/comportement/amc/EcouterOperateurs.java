package carpgenetique.comportement.amc;

import carpgenetique.agent.AMCPop;
import carpgenetique.algo.CroisementLOX;
import carpgenetique.algo.MutationMove;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.List;
import seisco.algo.Operateur;

/**
 * <p>Ce comportement permet d'écouter les opérateurs envoyés par l'ATE.
 * <p>ACL Message ID utilisé :
 * <p>ID_OPERA_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterOperateurs extends Behaviour {

    private AMCPop amc;
    public EcouterOperateurs(AMCPop a) {
        super(a);
        this.amc = a;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_OPERA_AMC);
        ACLMessage msgRecu = myAgent.receive(mt);
        
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.INFORM) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    try {
                        amc.getAlgo().setOperateurs((List<Operateur>)msgRecu.getContentObject());
                    } catch(UnreadableException ex) {
                        amc.println("Erreur: Impossible de lire les operateurs.");
                        // Generation des operateurs par défaut
                        List<Operateur> ops = new ArrayList<Operateur>();
                        ops.add(new CroisementLOX());
                        ops.add(new MutationMove());
                        amc.getAlgo().setOperateurs(ops);
                    }
                    
                    amc.setEtat("initOperateurs", true);
                }
            }
        } else
            block();
    }

    @Override
    public boolean done() {
        return amc.isInit("Operateurs");
    }

}
