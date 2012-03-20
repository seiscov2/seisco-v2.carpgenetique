package carpgenetique.comportement.ate;

import carpgenetique.agent.ATEPop;
import carpgenetique.util.MessageHelper;
import jade.core.ContainerID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.StaleProxyException;

/**
 * <p>Ce comportement déplace les AMEs après avoir recu la confirmation que l'AMC associé est initialisé.
 * <p>ACL Message ID utilisé : 
 * <p>ID_INIT_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class DeplacementAME extends Behaviour {

    private boolean done;
    private ATEPop ate;
    
    public DeplacementAME(ATEPop a) {
        super(a);
        this.done = false;
        this.ate = a;
    }

    @Override
    public void action() {
        // Récupération de message de fin d'initialisation
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_INIT_AMC);
        ACLMessage msgRecu = myAgent.receive(mt);
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.INFORM) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent().equals("init_done")) {
                        String nom = msgRecu.getSender().getLocalName();
                        
                        // Flag pret pour arreter le comportement
                        ate.setAMCState(nom, true);
                        
                        // Déplacement de l'AME
                        int fin_id = Integer.valueOf(nom.substring(4)) - 1; // -1 car les numéros des AID commencent à 1 et pas 0
                        ContainerID loc = ate.getLocationAme(fin_id);
                        
                        try {
                            ate.getControllerAme(fin_id).move(loc);
                        } catch(StaleProxyException ex) {
                            ate.println("Erreur: Impossible de déplacer ame_" + fin_id + ".");
                        }
                    }
                }
            }
        }

        // Vérifie que tous les agents sont prets
        int nbAgents = ate.getNbAgents();
        boolean allReady = true;
        for(int i=0; allReady && i < nbAgents; i++) {
            allReady &= ate.getAMCState("amc_" + (i+1));
        }
        
        this.done = allReady;
    }

    @Override
    public boolean done() {
        return this.done;
    }
}
