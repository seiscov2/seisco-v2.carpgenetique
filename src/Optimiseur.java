
import carp.GrapheCARP;
import carp.ProblemeCARP;
import carpgenetique.algo.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import seisco.algo.Operateur;
import seisco.util.Condition;
import seisco.util.Parametre;
import seisco.util.Propriete;
import seisco.util.graphe.Cout;
import seisco.util.graphe.Noeud;
import seisco.util.graphe.Arc;

/**
 * Optimiseur.java
 * @author Jerome
 */

public class Optimiseur {
    public static final String NAME_OP_CROIS_LOC = "loc";
	public static final String NAME_OP_CROIS_OC = "oc";
	public static final String NAME_OP_CROIS_X1 = "x1";
	
	public static final String NAME_OP_MUT_SWAP = "swap";
	public static final String NAME_OP_MUT_MOVE = "move";
        public static int j = 0;
	
	private String fichieraParser, id;
	private AlgoGenCARP algo;
	

	private int etape=0;
	private boolean etat = false;
	private long timeExecution = 0;
	private int nbEssais = 0;
	private boolean error = false;
	private ProblemeCARP nouveauProbleme = null;
	private int indice;
	private static final int MAX_NB_ESSAIS_LECTURE = 10;
	private boolean statut=false;
        
        private int generation;
        private int population;
        
        public Optimiseur() {
            fichieraParser = "C:\\prob.conf";
            generation = 500;
            population = 100;
        }
        
	private ProblemeCARP lire(String nomFichier) throws IOException, FileNotFoundException, NullPointerException {
		return ProblemeCARP.loadFromFile(nomFichier);
	}
        
        public void executeAlgo() {
            etat=true;
                if(etat==true){
			try {
				Date dateStart = new Date();
				
				for(int j=indice+1; j< this.generation; j++){
						/*if(j%150==0){
							int cpu = ((AgentTestAlgorithmeMobile)myAgent).determinerChargeCPU();
                                                        //System.out.println(cpu);
							if(cpu>=15){
								((AgentTestAlgorithmeMobile)myAgent).bouge1();
								System.out.println(j + " générations");
								((AgentTestAlgorithmeMobile)myAgent).setIndice(j);
								block(5000);
								return;
							}
						}*/
						algo.executer();
					}
				
				Date dateEnd = new Date();
				timeExecution = dateEnd.getTime() - dateStart.getTime();
			} 
			catch (Exception e) {
				e.printStackTrace();
				//doDelete();
			}
			
			//((AgentTestAlgorithmeMobile)myAgent).algo.getMaPopulation().trier();
                        algo.getPopulation().trier();
			
			String sortie="";
			
			sortie += "Population finale\n";
			sortie += algo.getPopulation().getIndividus().get(0).getFitness() + "\n\n";
			sortie += "Temps écoulé:\n";
			sortie += formatMillisecondes(timeExecution) + "\n";
			sortie += "Dont " + formatMillisecondes(algo.getTimeObjectiveFunction()) + " pour les exécutions successives de la méthode fonctionObjectif\n";
			sortie += "Dont " + formatMillisecondes(nouveauProbleme.getTimeBoucles()) + " pour les exécutions successives des boucles dans la fonction split\n";
			sortie += "Dont " + formatMillisecondes(nouveauProbleme.getTimeCalculDist()) + " pour les exécutions successives de la méthode de calcul de distance\n\n";
                        sortie += algo.getPopulation().getIndividus().get(0).toString();
                        
                        float moy = 0;
                        for(Individu i : algo.getPopulation().getIndividus())
                            moy+=i.getFitness();
                        
                        moy /= algo.getPopulation().getPopulationSize();
                        
                        sortie += "Fitness moyenne: " + moy + "\n";
                        
                        //for(int i=0;i<algo.getPopulation().getPopulationSize();i++)
                        //    sortie += i+": "+algo.getPopulation().getIndividus().get(i).getFitness()+"\n";
                        
                        int nb_trip = 0;
                        
                        
                        //System.out.println("Le coût de parcours de cette tache est : " + dist);
                        
                        System.out.println(sortie);
			//JOptionPane.showMessageDialog(null, sortie);
			}
			else{
				//block();
			}
        }
        
        public void init() {
            System.out.println("Initialisation de la population");
			// TODO Auto-generated method stub
			do{
				nbEssais++;
				try{
					nouveauProbleme = lire(fichieraParser);
				}
				catch(IOException ioException){
                                        ioException.printStackTrace();
					error = true;
				}
				catch(NullPointerException npException){
                                     npException.printStackTrace();
					error = true;
				}
			} while(error && nbEssais<=MAX_NB_ESSAIS_LECTURE);
			//SCOHY - Modification de la condition de la boucle -> en cas d'erreur de fichier, ca ne fait plus une boucle infinie

			//if(nbEssais>MAX_NB_ESSAIS_LECTURE)
			//	doDelete();
			
			Population pop = new Population("population tests");
                        //int m = 400;
                        pop.genererIndividus(this.population, nouveauProbleme);
			//pop.genererIndividus(100, nouveauProbleme);
                        //pop.genererIndividus(80, nouveauProbleme);
                        //pop.genererIndividus(85, nouveauProbleme);
                        //pop.genererIndividus(400, nouveauProbleme);
                        //pop.genererIndividus(200, nouveauProbleme);
                        //pop.genererIndividus(m, nouveauProbleme);
                        //pop.genererIndividus(1600, nouveauProbleme);
			pop.trier();

                        System.out.println("Population : " + this.population);
			System.out.println("Population initiale");
			System.out.println(pop.getIndividus().get(0).getFitness());
			System.out.println("Launching algorithm's execution");
			
			algo = new AlgoGenCARP(nouveauProbleme, pop);
			List<Parametre> parametres = new ArrayList<Parametre>();
			parametres.add(new Parametre<Float>(AlgoGenCARP.PROB_CROISEMENT, new Float(0.6))); //.99 0.6
			parametres.add(new Parametre<Float>(AlgoGenCARP.PROB_MUTATION, new Float(0.4))); //.1 0.4
			parametres.add(new Parametre<Float>(AlgoGenCARP.TAUX_SURVIE, new Float(0.2))); // .1 0.2
			algo.setParametres(parametres);
			List<Operateur> operateurs = new ArrayList<Operateur>();
			
                        // SCOHY
                        /* La liste des operateur en fonction est désactivée pour le moment
                         * On force ici en LOX + SWAP
                         */
			/*if(getArguments()!=null && (getArguments().length==1 || getArguments().length==2)){
				if(getArguments()[0]!=null){
					if(getArguments()[0].equals(NAME_OP_CROIS_OC))
						mesOperateurs.add(new CroisementOC());
					else if(getArguments()[0].equals(NAME_OP_CROIS_X1))
						mesOperateurs.add(new CroisementX1());
					else
						mesOperateurs.add(new CroisementLOC());	
				}
				else
					mesOperateurs.add(new CroisementLOC());
				
				if(getArguments()[1]!=null){
					if(getArguments()[1].equals(NAME_OP_MUT_MOVE))
						mesOperateurs.add(new MutationMove());
					else
						mesOperateurs.add(new MutationSwap());	
				}
				else
					mesOperateurs.add(new MutationSwap());
			}
			else{*/
				operateurs.add(new CroisementLOX());
				operateurs.add(new MutationSwap());
			//}
			
			for(Operateur o : operateurs)
				System.out.println(o);
			
			algo.setOperateurs(operateurs);
        }
        
        private String formatMillisecondes(long milliSeconds) {
		int hours = (int) (milliSeconds / (1000*60*60));
		int minutes = (int) ((milliSeconds % (1000*60*60)) / (1000*60));
		int seconds = (int) (((milliSeconds % (1000*60*60)) % (1000*60)) / 1000);
		String format = ((hours<10)?"0":"") + hours + ":" + ((minutes<10)?"0":"") +minutes + ":" + ((seconds<10)?"0":"") + seconds;
		return format;
	}
	
	private String formatMillisecondes(Date start, Date end) {
		return formatMillisecondes(end.getTime() - start.getTime());
	}
}
