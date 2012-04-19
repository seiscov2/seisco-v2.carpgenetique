package carpgenetique.comportement.amc;

import carp.ProblemeCARP;
import carpgenetique.agent.AMCPop;
import carpgenetique.algo.AlgoGenCARP;
import carpgenetique.algo.Individu;
import jade.core.behaviours.Behaviour;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import seisco.algo.AlgorithmException;
import seisco.util.DateHelper;

/**
 * <p>Ce comportement permet l'exécution de l'algorithme et est à la base du 
 * lancement des différents comportement de l'AMC.
 * 
 * @author Jerome
 * @version 2012
 */
public class ExecuterAlgorithme extends Behaviour {

    private AMCPop amc;
    private int nbAttenteSolutions;
    private long timeExecution = 0;
    
    public ExecuterAlgorithme(AMCPop a) {
        super(a);
        this.amc = a;
        this.nbAttenteSolutions = 5;
    }
    
    @Override
    public void action() { 
        if(!amc.isInit()) return; // si l'agent n'est pas initialisé, ne pas lancer l'algo
        
        int gen = amc.getCurrentGeneration();
        int maxGen = amc.getParametre("generation", Integer.class);
        
        if(gen > maxGen) { // Si fin de la generation
            amc.println("GENERATION TERMINEE\n\tINUTILE DE DEPLACER L'AGENT DE NOUVEAU !!!");
            return;
        }
        
        // Attente de solutions
        if(attenteSolutions()) {
            this.nbAttenteSolutions--;
            if(this.nbAttenteSolutions <= 0) {
                amc.setEtat("demandeSolution", false);
                amc.setEtat("attenteSolution", false);
                
                amc.println("AGENT DESYNCHRONISE !!!");
            }
        } else {
            this.nbAttenteSolutions = 5;
        }
        
        if(canRunAlgo()) {
            
            Date deb_time = new Date();
                    
            try {
                Individu meilleur=null;
                        
                int x = amc.getParametre("gen_cons", Integer.class);
                // Boucle X generation
                for(int i = 0; i < x; i++) {
                
                    amc.getAlgo().executer();
                
                    ((AlgoGenCARP)amc.getAlgo()).getPopulation().trier();
                
                    meilleur = ((AlgoGenCARP)amc.getAlgo()).getPopulation().getIndividus().get(0);
                
                    int aff = 10;
                    if(((AlgoGenCARP)amc.getAlgo()).getPopulation().getPopulationSize() < 10) aff = ((AlgoGenCARP)amc.getAlgo()).getPopulation().getPopulationSize();
                    
                    String m = "";
                    
                    DecimalFormat df = new DecimalFormat();
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    
                    for(int h = 0; h < aff-1; h++)
                        m += " "  + df.format(((AlgoGenCARP)amc.getAlgo()).getPopulation().getIndividus().get(h+1).getFitness());
   
                    // Affichage du nombre de génération
                    amc.println(gen + "/" + maxGen + "\t " + ((AlgoGenCARP)amc.getAlgo()).getPopulation().getPopulationSize() + " " + df.format(meilleur.getFitness()) + m);
                    
                    // Incrémentation du compteur de generation
                    gen++;
                    amc.setCurrentGeneration(gen);
                    
                    // Vérification de demande de solution
                    if(meilleur.getFitness() != amc.getLastFitness()) {
                        amc.setCountFitness(0);
                        amc.setLastFitness(meilleur.getFitness());
                    } else
                        amc.setCountFitness(amc.getCountFitness()+1);
                    if(amc.getCountFitness() > amc.getParametre("stagne_dem", Integer.class) && canAskSolution()) {
                        amc.setEtat("demandeSolution", true);
                        amc.setCountFitness(0);
                    }
                }
                
                // Mise en cache du meilleur individu (par rapport à la fitness
                Individu cache = (Individu)amc.getCacheSolution();
                if(cache == null || meilleur.getFitness() < cache.getFitness()) {
                    try {
                        amc.setCacheSolution(meilleur.copy());
                    } catch(CloneNotSupportedException ex) {
                        amc.println("Erreur: Impossible de mettre l'individu en cache (copy fail).");
                    }
                    amc.setEtat("updateSolution", true);
                }
                
            } catch (AlgorithmException ex) {
                amc.println("Erreur lors de l'execution de l'algorithme " + amc.getAlgo().getNom());
            }
            
            Date fin_time = new Date();
            timeExecution += fin_time.getTime() - deb_time.getTime();
            amc.setAlgoExecTime(timeExecution);
        } else
            block(2000);
    }

    /**
     * <p>Retourne si le comportement doit est fini ou non.
     * <p>Il est considéré comme terminé si :
     * <ul>
     *  <li>Le nombre de génération est réalisé.
     *  <li>L'AMC recoit l'ordre de se déplacer.
     * </ul>
     * @return <code>true</code> si le comportement est fini, sinon <code>false</code>.
     * @since 2012
     */
    @Override
    public boolean done() {
        if(!amc.isInit()) return false; // si l'agent n'est pas initialisé, ne pas lancer l'algo
        
        int gen = amc.getCurrentGeneration();
        int maxGen = amc.getParametre("generation", Integer.class);
        
        if(gen >= maxGen) {// si fin de la generation 
            amc.setEtat("finExecution", true);
                    
            //((AlgoGenCARP)amc.getAlgo()).getPopulation().getIndividus().get(0).afficher();
            
            // Affichage du temps utilisé par l'AMC lors de son exécution
            amc.setFinExecution(new Date());
            
            amc.println("Temps écoulé total: " + DateHelper.formatMillisecondes(amc.getFinExecution()-amc.getDebutExecution()));
            amc.println("Dont " + DateHelper.formatMillisecondes(timeExecution) + " pour l'exécution de l'algoritme.");
            amc.println("Dont " + DateHelper.formatMillisecondes(amc.getAlgo().getTimeObjectiveFunction()) + " pour les exécutions successives de la méthode fonctionObjectif");
            //amc.println("Dont " + DateHelper.formatMillisecondes(((ProblemeCARP)amc.getAlgo().getProbleme()).getTimeBoucles()) + " pour les exécutions successives des boucles dans la fonction split");
            //amc.println("Dont " + DateHelper.formatMillisecondes(((ProblemeCARP)amc.getAlgo().getProbleme()).getTimeCalculDist()) + " pour les exécutions successives de la méthode de calcul de distance");
		
            
            return true;
        }
        // on vérifie une demande de déplacement
        else if((Boolean)amc.getEtat("deplacement").getValeur())
            return true;
        
        return false;
    }

    /**
     * <p>Permet de savoir si l'algo peut continuer a tourner.
     * <p>Liste des conditions de mise en pause:
     * <ul>
     *  <li>Déplacement en cours.
     *  <li>Demande de meilleure solution.
     * </ul>
     * 
     * @return <code>true</code> si l'algorithme peut etre exécuté, sinon <code>false</code>
     * @since 2012
     */
    private boolean canRunAlgo() {
        boolean canRun = true;
        
        canRun &= !(Boolean)amc.getEtat("deplacement").getValeur();
        
        canRun &= !attenteSolutions();
        
        canRun &= !(Boolean)amc.getEtat("updateSolution").getValeur();
        canRun &= !(Boolean)amc.getEtat("presenteSolution").getValeur();
        
        return canRun;
    }
    
    /**
     * <p>Permet de savoir si l'AMC est en attente de solutions ou non.
     * 
     * @return <code>true</code> si en attente de solutions, sinon <code>false</code>
     * @since 2012
     */
    private boolean attenteSolutions() {
        boolean att = true;
        
        att &= (Boolean)amc.getEtat("demandeSolution").getValeur();
        att &= (Boolean)amc.getEtat("attenteSolution").getValeur();
        
        return att;
    }
    
    /**
     * <p>Permet de savoir si l'algo va demander ou non une solution à son AME.
     * <p>Actuellement, il demande une solution uniquement s'il se trouve 
     * entre <code>min</code>% et <code>max</code>% (compris) de son nombre de génération.
     * @return <code>true</code> si on peut demandé des solutions, sinon <code>false</code>
     * @since 2012
     */
    private boolean canAskSolution() {
        int min = amc.getParametre("se_pourc_min", Integer.class);
        int max = amc.getParametre("se_pourc_max", Integer.class);

        int maxGen = amc.getParametre("generation", Integer.class);
        int gen = amc.getCurrentGeneration();
        
        int actuel = (int)(((float)gen/maxGen) * 100);
        
        return (actuel<min || actuel>max) ? false : true;
    }
}
