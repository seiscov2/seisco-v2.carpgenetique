package carpgenetique.comportement.ate;

import carpgenetique.agent.AMCPop;
import carpgenetique.agent.AMEPop;
import carpgenetique.agent.ATEPop;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.util.List;

/**
 * <p>Ce comportement initialise les agents (AMCPop & AMEPop) et leur associe 
 * un comportement pour leur envoyer la configuration.
 * 
 * @author Jerome
 * @version 2012
 */
public class InitialiserAgents extends OneShotBehaviour {
    
    private ATEPop ate;
    
    public InitialiserAgents(ATEPop a) {
        super(a);
        this.ate = a;
    }

    @Override
    public void action() {
        try {
            for(int i=0; i < ate.getNbAgents(); i++) {
                // Création du containerId pour le déplacement
                ContainerID loc = new ContainerID();
                
                String nomContainer="Main-Container";
                
                List<String> nomCont = ate.getNomCont();
                if(!nomCont.isEmpty()) {
                    int id = i;
                    if(id>=nomCont.size())
                        id -= ((nomCont.size()) * (i/nomCont.size()));
                    
                    nomContainer = nomCont.get(id);
                }
                
                loc.setName(nomContainer);
                
                // Création de l'amc
                AMCPop amcc = new AMCPop();
                AgentController acamc = ate.getContainerController().acceptNewAgent("amc_" + (i+1), amcc);
                acamc.start();
                
                ate.getAmc().put("amc_" + (i+1), false);
                
                // Création de l'ame
                AMEPop amec = new AMEPop();
                AgentController acame = ate.getContainerController().acceptNewAgent("ame_" + (i+1), amec);
                acame.start();
                
                ate.addAmeController(acame);
                ate.addAmeContainer(loc);
                
                // Envoi de la configuration
                AID ame = new AID("ame_" + (i+1), AID.ISLOCALNAME);
                AID amc = new AID("amc_" + (i+1), AID.ISLOCALNAME);
                ate.addBehaviour(new PresenterConfiguration(ate, amc, ame));
                
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ex) {

                }
            }
        } catch (StaleProxyException ex) {
            ate.println("Erreur: Impossible de récupérer le ContainerController.");
        }
    }
}
