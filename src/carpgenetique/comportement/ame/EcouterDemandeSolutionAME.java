package carpgenetique.comportement.ame;

import carpgenetique.agent.AMEPop;
import carpgenetique.util.MessageHelper;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import seisco.util.ObjectCodec;

/**
 * <p>Ce comportement écoute les demandes de solutions en provenance d'autre AME.
 * <p>ACL Message Id utilisés :
 * <p>ID_SOL_DEMANDE_AME, ID_SOL_ENVOI_AME
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterDemandeSolutionAME extends CyclicBehaviour {
    
    private AMEPop ame;
    
    public EcouterDemandeSolutionAME(AMEPop a) {
        super(a);
        this.ame = a;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_SOL_DEMANDE_AME);
        
        ACLMessage msgRecu = myAgent.receive(mt);
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.QUERY_IF) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    if(ame.getCacheDemandeurSolution() != null) {
                        this.sendErrorMessage(msgRecu.getSender(), "busy");
                    } else {                   
                        float fitnessDem = Float.parseFloat(msgRecu.getContent());

                        if(ame.getCacheFitness()>fitnessDem) {
                            // Vérification du cache
                            if(ame.getCacheSolution()==null) {
                                // Récupération de l'individu + mise en cache
                                ame.setEtat("demandeSolution", true);
                            }
                            
                            ame.setEtat("envoiSolution", true);
                            ame.setCacheDemandeurSolution(msgRecu.getSender());
                        } else {
                            this.sendErrorMessage(msgRecu.getSender(), "no_best");
                        }
                    }
                }
            }
        } else 
            block(1000);
    }
    
    /**
     * <p>Permet d'envoyer un message d'erreur à l'AME demandeur.
     * 
     * @param rec
     *      L'AID de l'AME auquel envoyer le message
     * @param msg 
     *      Le message d'erreur
     * @since 2012
     */
    private void sendErrorMessage(AID rec, String msg) {
        MessageHelper rep = new MessageHelper();
        rep.create(ACLMessage.INFORM, MessageHelper.ID_SOL_ENVOI_AME);
        rep.addReceiver(rec);
        try {
            ame.send(rep.get(ObjectCodec.encode(msg)));
        } catch(Exception ex) {
            ame.println("Erreur: Impossible de sérialiser le message.\n\t(Raison: "+ex.getMessage()+")");
        }
    }
}
