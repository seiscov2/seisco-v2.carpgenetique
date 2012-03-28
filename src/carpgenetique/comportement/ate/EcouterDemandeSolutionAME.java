package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;

/**
 * <p>Ce comportement écoute ses AME pour savoir s'ils demandent de nouvelles solutions.
 * <p>Il envoi également une demande de solutions aux autres ATE.
 * <p>ACL Message ID utilisés :
 * <p>ID_SOL_DEMANDE, ID_SOL_DEMANDE_ATE
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterDemandeSolutionAME extends CyclicBehaviour {

    private ATEPop ate;
    
    public EcouterDemandeSolutionAME(ATEPop a) {
        super(a);
        this.ate = a;
    }
    
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_SOL_DEMANDE);
        
        ACLMessage msgRecu = myAgent.receive(mt);
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.QUERY_IF) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    String fitness = msgRecu.getContent();
                    
                    List<AID> ates = ate.getAidAte();
                    ates.remove(ate.getAID()); // On se retire de la liste pour ne pas s'envoyer de message
                    
                    ate.setATEs(ates);
                    
                    // Si l'ATE a une meilleure solution en cache, l'ajouter à la liste
                    if(ate.getCacheSolution()!=null && ate.getCacheFitness()<Float.valueOf(fitness)) {
                        ate.getRecSolutions().add((Individu)ate.getCacheSolution());
                        if(ates.isEmpty()) { // On vérifie que l'ATE n'est pas en stand alone
                            ate.setEtat("solutionsRecues", true);
                        }
                    }
                    // Ajout de l'AMC demandant des solutions
                    String idAmc = msgRecu.getSender().getLocalName().substring(4);
                    ate.setAmcSol(new AID("amc_" + idAmc, AID.ISLOCALNAME));
                    
                    MessageHelper mh = new MessageHelper();
                    mh.create(ACLMessage.QUERY_IF, MessageHelper.ID_SOL_DEMANDE_ATE);
                    mh.setReceiver(ates);
                    ate.send(mh.get(fitness));
                }
            }
        } else
            block(1000);
                
    }
}