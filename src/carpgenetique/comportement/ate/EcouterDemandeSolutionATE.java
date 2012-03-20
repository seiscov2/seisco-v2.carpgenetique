package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.util.MessageHelper;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import seisco.util.MessageCodec;

/**
 * <p>Ce comportement écoute les demandes de solutions en provenance d'autre ATE.
 * <p>ACL Message Id utilisés :
 * <p>ID_SOL_DEMANDE_ATE, ID_SOL_ENVOI_ATE
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterDemandeSolutionATE extends CyclicBehaviour {
    
    private ATEPop ate;
    
    public EcouterDemandeSolutionATE(ATEPop a) {
        super(a);
        this.ate = a;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_SOL_DEMANDE_ATE);
        
        ACLMessage msgRecu = myAgent.receive(mt);
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.QUERY_IF) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    
                    
                    if(ate.getCacheDemandeurSolution() != null) {
                        this.sendErrorMessage(msgRecu.getSender(), "busy");
                    } else {
                        float fitnessDem = Float.parseFloat(msgRecu.getContent());
                        
                        if(ate.getCacheFitness()>fitnessDem) {
                            // Vérification du cache
                            if(ate.getCacheSolution()==null) {
                                // Récupération de l'individu + mise en cache
                                ate.setEtat("demandeSolution", true);
                            }

                            ate.setEtat("envoiSolution", true);
                            ate.setCacheDemandeurSolution(msgRecu.getSender());
                        } else {
                            this.sendErrorMessage(msgRecu.getSender(), "no_best");
                        }
                        
                        ate.println("Réception d'une demande : <<" + msgRecu.getSender().getName() + ">> " + fitnessDem);
                    }
                }
            }
        } else 
            block(1000);
    }
    
    /**
     * <p>Permet d'envoyer un message d'erreur à l'ATE demandeur.
     * 
     * @param rec
     *      L'AID de l'ATE auquel envoyer le message
     * @param msg 
     *      Le message d'erreur
     * @since 2012
     */
    private void sendErrorMessage(AID rec, String msg) {
        MessageHelper rep = new MessageHelper();
        rep.create(ACLMessage.INFORM, MessageHelper.ID_SOL_ENVOI_ATE);
        rep.addReceiver(rec);
        try {
            ate.send(rep.get(MessageCodec.encode(msg)));
        } catch(Exception ex) {
            ate.println("Erreur: Impossible de sérialiser le message.\n\t(Raison: "+ex.getMessage()+")");
        }
    }
}
