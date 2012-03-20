package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import seisco.util.MessageCodec;

/**
 * <p>Ce comportement permet de récupérer les solutions demandées aux autres ATE.
 * <p>ACL Message ID utilisé :
 * <p>ID_SOL_ENVOI_ATE
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterSolutionATE extends CyclicBehaviour {
    
    private ATEPop ate;
    
    public EcouterSolutionATE(ATEPop a) {
        super(a);
        this.ate = a;
    }
    
    @Override
    public void action() {
        if(!ate.getATEs().isEmpty()) {
            MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_SOL_ENVOI_ATE);
            ACLMessage msgRecu = ate.receive(mt);
            if(msgRecu != null) {
                if(ate.getATEs().contains(msgRecu.getSender())) { // Si présent dans la liste des demandes
                    Object response=null;
                    try {
                        response = MessageCodec.decode(msgRecu.getContent());
                    } catch(Exception ex) {
                        ate.println("Erreur: Impossible de désérialiser le contenu.\n\t(Raison: "+ex.getMessage()+")");
                    }
                    
                    if(response instanceof Individu) { // Réception solution
                        Individu ind = (Individu)response;
                        
                        ate.getRecSolutions().add(ind);
                        ate.println("\tRéception sol: " + ind.getFitness() + " @ " + msgRecu.getSender().getName());
                    } else if(response instanceof String) { // Réception message d'erreur
                        String err = String.valueOf(response);
                        if(err.equalsIgnoreCase("busy"))
                            ate.println("EcouterSolutionATE: <<"+msgRecu.getSender().getName()+">> est occupé et n'envoit pas de solution.");
                        else
                            ate.println("EcouterSolutionATE: <<"+msgRecu.getSender().getName()+" a retourné le message '"+err+"'");
                    } else { // Contenu inconnu
                        ate.println("Erreur: Réception d'une réponse de type inconnue [" + response.getClass().getSimpleName() + "].");
                    }
                    
                    // Retire l'AME de la liste pour ne plus rien recevoir de lui pour cette demande
                    ate.getATEs().remove(msgRecu.getSender());
                    
                    if(ate.getATEs().isEmpty()) {
                        ate.setEtat("solutionsRecues", true);
                    }
                }
            } else
                block(1000);
        }
    }
}

