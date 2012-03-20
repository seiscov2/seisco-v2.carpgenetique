package carpgenetique.comportement.amc;

import carpgenetique.agent.AMCPop;
import carpgenetique.util.MessageHelper;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

/**
 * <p>Ce comportement permet de confirmer l'initialisation de l'agent.
 * <p>ACL Message ID utilisé :
 * <p>ID_INIT_AMC
 * 
 * @author Jerome
 * @version 2011
 * @see seisco.agent.AgentMobileCalcul#isInit()
 */
public class ConfirmerInitialisation extends Behaviour {

    private boolean done;
    private AMCPop amc;
    public ConfirmerInitialisation(AMCPop a) {
        super(a);
        this.done = false;
        this.amc = a;
    }
    
    @Override
    public void action() {
        if(amc.isInit()) {
            MessageHelper mh = new MessageHelper();
            mh.create(ACLMessage.INFORM, MessageHelper.ID_INIT_AMC);
            mh.addReceiver(amc.getATE());
            myAgent.send(mh.get("init_done"));
            this.done = true;
        }
    }

    @Override
    public boolean done() {
        return this.done;
    }
    
}
