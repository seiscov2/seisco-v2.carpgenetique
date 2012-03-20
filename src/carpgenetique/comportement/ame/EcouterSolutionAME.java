package carpgenetique.comportement.ame;

import carpgenetique.agent.AMEPop;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import seisco.util.MessageCodec;

/**
 * <p>Ce comportement permet de récupérer les solutions demandées aux autres AME.
 * <p>ACL Message ID utilisé :
 * <p>ID_SOL_ENVOI_AME
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterSolutionAME extends CyclicBehaviour {
    
    private AMEPop ame;
    
    public EcouterSolutionAME(AMEPop a) {
        super(a);
        this.ame = a;
    }
    
    @Override
    public void action() {
        if(!ame.getAMEs().isEmpty()) {
            MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_SOL_ENVOI_AME);
            ACLMessage msgRecu = ame.receive(mt);
            if(msgRecu != null) {
                if(ame.getAMEs().contains(msgRecu.getSender())) { // Si présent dans la liste des demandes
                    Object response=null;
                    try {
                        response = MessageCodec.decode(msgRecu.getContent());
                    } catch(Exception ex) {
                        ame.println("Erreur: Impossible de désérialiser le contenu.\n\t(Raison: "+ex.getMessage()+")");
                    }
                    
                    if(response instanceof Individu) { // Réception solution
                        Individu ind = (Individu)response;
                        
                        ame.getRecSolutions().add(ind);
                        //ame.println("\tRéception sol: " + (1/ind.getFitness()) + " @ " + msgRecu.getSender().getLocalName());
                    } else if(response instanceof String) { // Réception message d'erreur
                    } else { // Contenu inconnu
                        ame.println("Erreur: Réception d'une réponse de type inconnue [" + response.getClass().getSimpleName() + "].");
                    }
                    
                    // Retire l'AME de la liste pour ne plus rien recevoir de lui pour cette demande
                    ame.getAMEs().remove(msgRecu.getSender());
                    
                    if(ame.getAMEs().isEmpty()) {
                        ame.setEtat("solutionsRecues", true);
                    }
                }
            } else
                block(1000);
        }
    }
}
