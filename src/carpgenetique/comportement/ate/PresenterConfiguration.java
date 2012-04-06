package carpgenetique.comportement.ate;

import carp.ProblemeCARP;
import carpgenetique.agent.ATEPop;
import carpgenetique.algo.*;
import carpgenetique.util.MessageHelper;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import seisco.algo.Operateur;
import seisco.util.ObjectCodec;

/**
 * <p>Ce comportement envoi la configuration à l'AMC et l'AME
 * <p>ACL Message ID utilisés :
 * <p>ID_CONT_AME, ID_PROBL_AMC, ID_PARAM_AMC, ID_OPERA_AMC
 * @author Jerome
 */
public class PresenterConfiguration extends Behaviour {

    private AID amc;
    private AID ame;
    private boolean done;
    private ATEPop ate;
    
    public PresenterConfiguration(ATEPop a, AID amc, AID ame) {
        super(a);
        this.amc = amc;
        this.ame = ame;
        this.done = false;
        this.ate = a;
    }
    
    @Override
    public void action() {
        MessageHelper mh = new MessageHelper();

        // Containeurs pour l'AME
        mh.create(ACLMessage.INFORM, MessageHelper.ID_CONT_AME);
        mh.addReceiver(ame);
        try {
            ate.send(mh.get(ObjectCodec.encode((ArrayList)ate.getNomCont())));
        } catch(Exception ex) {
            ate.println("Erreur: Impossible de sérialiser la liste des containeurs.");
        }
        
        
        // Probleme
        mh.reset();
        mh.create(ACLMessage.INFORM, MessageHelper.ID_PROBL_AMC);
        mh.addReceiver(this.amc);
        try {
            mh.setObject(ate.getProbleme());
            myAgent.send(mh.get());
        } catch(IOException ex) {
            ate.println("Erreur: Impossible de sérialiser le probleme.");
        }
        
        // On attend une confirmation de l'amc pour envoyer la suite
        // Cela permet d'éviter des NullPointerException sur l'amc dans le cas
        // où son problème ne serait pas encore configuré
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId(MessageHelper.ID_PROBL_AMC),
                                                 MessageTemplate.MatchSender(this.amc));
        ACLMessage msgRecu = myAgent.receive(mt);
        
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.CONFIRM) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    // Parametres
                    mh.reset();
                    mh.create(ACLMessage.INFORM, MessageHelper.ID_PARAM_AMC);
                    mh.addReceiver(this.amc);
                    try {
                            mh.setObject(ate.getJp());
                    } catch(IOException ex) {
                        System.out.println("Erreur ATEPop: Impossible de sérialiser le jeu de parametres.");
                    }
                    myAgent.send(mh.get());

                    // Operateurs
                    ArrayList<Operateur> ops = new ArrayList<Operateur>();
                    // 1: Choix du croisement
                    String crois = ate.getOp_croisement().get(new Random().nextInt(ate.getOp_croisement().size()));
                    if(crois.equals("lox")) ops.add(new CroisementLOX());
                    else if(crois.equals("ox")) ops.add(new CroisementOX());
                    else if(crois.equals("x1")) ops.add(new CroisementX1());
                    // 2: Choix de la mutation
                    String muta = ate.getOp_mutation().get(new Random().nextInt(ate.getOp_mutation().size()));
                    if(muta.equals("move")) ops.add(new MutationMove());
                    else if(muta.equals("swap")) ops.add(new MutationSwap());
                    // 3: Envoi
                    mh.reset();
                    mh.create(ACLMessage.INFORM, MessageHelper.ID_OPERA_AMC);
                    mh.addReceiver(this.amc);
                    try {
                        mh.setObject(ops);
                    } catch(IOException ex) {
                        System.out.println("Erreur ATEPop: Impossible de sérialiser le jeu d'opérateur.");
                    }
                    myAgent.send(mh.get());
                    
                    this.done = true;
                }
            } else
                block();
        } else
            block(10000);
    }

    @Override
    public boolean done() {
        return this.done;
    }

}
