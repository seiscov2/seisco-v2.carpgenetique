package carpgenetique.comportement.amc;

import carpgenetique.agent.AMCPop;
import carpgenetique.algo.Individu;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * <p>Ce comportement répond à toutes demandes, en provenance de l'AME ou de l'ATE,
 * d'envoi du meilleur individu.
 * <p>ACL Message ID utilisé :
 * <p>ID_UPDATESOL_AMC
 * 
 * @author Jerome
 * @version 2012
 */
public class UpdateSolution extends CyclicBehaviour {

    private AMCPop amc;
    
    public UpdateSolution(AMCPop a) {
        super(a);
        this.amc = a;
    }
    
    @Override
    public void action() {
        if((Boolean)amc.getEtat("updateSolution").getValeur()) {
            
            MessageHelper mh = new MessageHelper();
            mh.create(ACLMessage.INFORM, MessageHelper.ID_UPDATESOL_AMC);
            mh.addReceiver(amc.getAME());
            mh.addReceiver(amc.getATE());
            
            Individu sol = (Individu)amc.getCacheSolution();
            if(sol != null)
                amc.send(mh.get(String.valueOf(sol.getFitness())));
            
            amc.setEtat("updateSolution", false);
        }
    }

}
