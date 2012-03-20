package carpgenetique.comportement.amc;

import carp.ProblemeCARP;
import carpgenetique.agent.AMCPop;
import carpgenetique.algo.AlgoGenCARP;
import carpgenetique.algo.Population;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * <p>Ce comportement permet d'écouter le problème CARP envoyé par l'ATE.
 * <p>Une fois le problème recu, l'AMC envoi une confirmation à l'ATE.
 * <p>ACL Message ID utilisé :
 * <p>ID_PROBL_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterProbleme extends Behaviour {

    private boolean probRecu;
    private AMCPop amc;
    
    public EcouterProbleme(AMCPop a) {
        super(a);
        probRecu = false;
        this.amc = a;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_PROBL_AMC);
        
        ACLMessage msgRecu = myAgent.receive(mt);

        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.INFORM) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    try {
                        ProblemeCARP p = (ProblemeCARP)msgRecu.getContentObject();
                        
                        Population pop = new Population(amc.getLocalName());
                        
                        amc.setAlgo(new AlgoGenCARP(p, pop));
                        
                        // Réponse à l'ATE
                        amc.setATE(msgRecu.getSender());
                        MessageHelper mh = new MessageHelper();
                        mh.create(ACLMessage.CONFIRM, MessageHelper.ID_PROBL_AMC);
                        mh.addReceiver(msgRecu.getSender());
                        myAgent.send(mh.get("prob_ok"));
                        
                        probRecu = true;
                    } catch(UnreadableException ex) {
                        amc.println("Erreur: Impossible de lire le probleme CARP.");
                    }
                }
            }
        } else
            block(1000);
    }

    @Override
    public boolean done() {
        return probRecu;
    }

}
