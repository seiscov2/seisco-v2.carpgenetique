package carpgenetique.comportement.ame;

import carpgenetique.agent.AMEPop;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Ce comportement écoute son AMC pour savoir s'il demande de nouvelles solutions.
 * <p>Il envoi également une demande de solutions aux autres AME.
 * <p>ACL Message ID utilisés :
 * <p>ID_SOL_DEMANDE, ID_SOL_DEMANDE_AME
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterDemandeSolutionAMC extends CyclicBehaviour {

    private AMEPop ame;
    
    public EcouterDemandeSolutionAMC(AMEPop a) {
        super(a);
        this.ame = a;
    }
    
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId(MessageHelper.ID_SOL_DEMANDE),
                                                 MessageTemplate.MatchSender(ame.getAMC()));
        
        ACLMessage msgRecu = myAgent.receive(mt);
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.QUERY_IF) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    String fitness = msgRecu.getContent();
                    
                    List<AID> ames = ame.getAmesDF();
                    ames.remove(ame.getAID()); // On se retire de la liste pour ne pas s'envoyer de message
                    
                    ame.setAMEs(ames);
                    
                    MessageHelper mh = new MessageHelper();
                    mh.create(ACLMessage.QUERY_IF, MessageHelper.ID_SOL_DEMANDE_AME);
                    mh.setReceiver(ames);
                    ame.send(mh.get(fitness));

                    //ame.println("Fitness demande: " + (1/Float.parseFloat(fitness)));
                    
                    ame.setRecSolutions(new ArrayList<Individu>());
                    ame.setEtat("solutionsRecues", false);
                }
            }
        } else
            block(1000);
                
    }
    
}
