package carpgenetique.comportement.amc;

import carpgenetique.agent.AMCPop;
import carpgenetique.algo.AlgoGenCARP;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * <p>Ce comportement va demander des individus à l'AME en cas de stagne de
 * la population.
 * <p>ACL Message ID utilisé :
 * <p>ID_SOL_DEMANDE
 * 
 * @author Jerome
 * @version 2012
 */
public class DemandeIndividu extends CyclicBehaviour {

    private AMCPop amc;
    
    public DemandeIndividu(AMCPop a) {
        super(a);
        this.amc = a;
    }
    
    @Override
    public void action() {
        // Si demande de solution
        if((Boolean)amc.getEtat("demandeSolution").getValeur()) {
            MessageHelper mh = new MessageHelper();
            mh.create(ACLMessage.QUERY_IF, MessageHelper.ID_SOL_DEMANDE);
            mh.addReceiver(amc.getAME());
            myAgent.send(mh.get(String.valueOf(((AlgoGenCARP)amc.getAlgo()).getPopulation().getIndividus().get(0).getFitness())));
            
            amc.setEtat("demandeSolution", false);
            amc.setEtat("attenteSolution", true);
        }
    }

}
