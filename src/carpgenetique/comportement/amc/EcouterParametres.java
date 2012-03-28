package carpgenetique.comportement.amc;

import carp.ProblemeCARP;
import carpgenetique.agent.AMCPop;
import carpgenetique.algo.AlgoGenCARP;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import seisco.algo.JeuParametres;
import seisco.util.Parametre;

/**
 * <p>Ce comportement permet d'écouter les parametres envoyés par l'ATE.
 * <p>ACL Message ID utilisé :
 * <p>ID_PARAM_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class EcouterParametres extends Behaviour {
    
    private AMCPop amc;
    
    public EcouterParametres(AMCPop a) {
        super(a);
        this.amc = a;
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchConversationId(MessageHelper.ID_PARAM_AMC);
        ACLMessage msgRecu = myAgent.receive(mt);
        
        if(msgRecu != null) {
            if(msgRecu.getPerformative() == ACLMessage.INFORM) {
                if(msgRecu.getLanguage()!=null && msgRecu.getLanguage().equals("JavaSerialization")) {
                    if(msgRecu.getContent() == null)
                        throw new NullPointerException();
                    
                    try {
                        amc.getAlgo().setParametres((JeuParametres)msgRecu.getContentObject());
                    } catch(UnreadableException ex) {
                        amc.println("Erreur: Impossible de lire le jeu de parametres.");
                        // Generation d'un jeu par défaut
                        JeuParametres jp = new JeuParametres();
                        jp.addParametre(new Parametre("se_pourc_min", 10));
                        jp.addParametre(new Parametre("se_pourc_max", 90));
                        jp.addParametre(new Parametre("generation", 10000));
                        jp.addParametre(new Parametre("gen_cons", 20));
                        jp.addParametre(new Parametre("stagne_dem", 10));
                        jp.addParametre(new Parametre("population", 400));
                        jp.addParametre(new Parametre("prob_crois", new Float(0.6)));
                        jp.addParametre(new Parametre("prob_muta" , new Float(0.4)));
                        jp.addParametre(new Parametre("survie"    , new Float(0.2)));
                        jp.addParametre(new Parametre("noclone_type", "task"));
                        
                        
                        amc.getAlgo().setParametres(jp);
                    }
                    
                    // Maintenant qu'on connait la taille de la population, on peut la générer
                    ((AlgoGenCARP)amc.getAlgo()).getPopulation().setNoCloneType(amc.getParametre("noclone_type", String.class));
                    ((AlgoGenCARP)amc.getAlgo()).getPopulation().genererIndividus(amc.getParametre("population", Integer.class), (ProblemeCARP)amc.getAlgo().getProbleme());
                    
                    amc.setEtat("initParam", true);
                }
            }
        } else
            block();
    }

    @Override
    public boolean done() {
        return amc.isInit("Param");
    }

}
