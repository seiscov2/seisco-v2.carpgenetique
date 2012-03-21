package carpgenetique.comportement.amc;

import carpgenetique.agent.AMCPop;
import carpgenetique.algo.AlgoGenCARP;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.List;
import seisco.util.MessageCodec;

/**
 * <p>Ce comportement permet de récupérer les solutions demandées à l'AME.
 * <p>ACL Message ID utilisé :
 * <p>ID_SOL_ENVOI_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class ReceptionIndividus extends CyclicBehaviour {

    private AMCPop amc;
    
    public ReceptionIndividus(AMCPop a) {
        super(a);
        this.amc = a;
    }
    
    /**
     * BOBIBOB
     */
    
    @Override
    public void action() {
        if((Boolean)amc.getEtat("attenteSolution").getValeur()) {
            
            MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_SOL_ENVOI_AMC);
            ACLMessage msgRecu = amc.receive(mt);
            
            if(msgRecu != null) {
                if(msgRecu.getPerformative() == ACLMessage.INFORM) {
                    if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                        
                        if(msgRecu.getContent() == null)
                            throw new NullPointerException();
                        
                        try {
                            Object obj = MessageCodec.decode(msgRecu.getContent());
                            
                            if(obj instanceof List) {
                                List<Individu> inds = (ArrayList<Individu>)obj;
                                
                                // Intégration des individus
                                if(!inds.isEmpty()) {
                                    AlgoGenCARP algo = (AlgoGenCARP)amc.getAlgo();
                                    int taillePop = algo.getPopulation().getIndividus().size();
                                    for(int i=0, pos=0; i < inds.size(); i++) {
                                        if(!algo.getPopulation().getIndividus().contains(inds.get(i))) { // Empeche les doublons
                                            algo.getPopulation().getIndividus().set(taillePop-(pos+1), inds.get(i));
                                            pos++;
                                        }
                                    }
                                }
                            } else if(obj instanceof String) {
                                amc.println("Aucune solution recue\n\t<<"+msgRecu.getSender().getLocalName()+">>\n\t["+String.valueOf(obj)+"]");
                            }
                        } catch(Exception ex) {
                            amc.println("Erreur: Impossible de décoder les individus.\n\r(Raison: "+ex.getMessage()+")");
                        }
                        
                        amc.setEtat("attenteSolution", false);
                    } else
                        amc.println("Erreur: Langage eronné.");
                } else
                    amc.println("Erreur: Performative INFORM non recu.");
            } else
                block(1000);
        } else
            block(1000);
    }

}
