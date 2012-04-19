package carpgenetique.algo;

import carp.ProblemeCARP;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import seisco.algo.AlgorithmException;
import seisco.algo.Algorithme;
import seisco.algo.Operateur;
import seisco.util.Parametre;

/**
 * <p>Représente l'algorithme génétique pour le CARP.
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see Algorithme
 */
public class AlgoGenCARP extends Algorithme {

    public static final String TAUX_SURVIE = "survie";
    public static final String PROB_CROISEMENT = "prob_crois";
    public static final String PROB_MUTATION = "prob_muta";

    private Population population;

    /**
     * <p>Instancie un algorithme génétique pour le CARP
     * 
     * @param probleme
     *  le {@link ProblemeCARP} à lier à l'algorithme courant
     * @param pop 
     *  la {@link Population} à lier à l'algorithme courant
     * @since 2012
     * @see Algorithme#Algorithme(java.lang.String, seisco.probleme.Probleme) 
     */
    public AlgoGenCARP(ProblemeCARP probleme, Population pop) {
        super("GenCARP", probleme);
        this.population = pop;
    }

    /**
     * <p>Retourne la {@link Population} de l'algorithme génétique
     * 
     * @return 
     *  la population de l'algorithme génétique sous forme de {@link Population}.
     * @since 2008
     * @see Population
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * <p>Remplace la population de l'algorithme génétique
     * 
     * @param population 
     *  la nouvelle population de l'algorithme
     *  génétique sous forme de {@link Population}.
     * @since 2008
     * @see Population
     */
    public void setPopulation(Population population) {
        this.population = population;
    }
    
    /**
     * <p>
     * Méthode de sélection d'un individu de la population
     * par le procédé de la roue de la fortune.
     * </p>
     * 
     * @return l'{@link Individu} sélectionné
     * @since 2008
     */
    private Individu selectionner() {
        int i;
        int popsize = population.getPopulationSize();
        float sum, pick;
        float sumfitness = 0;

        for(Individu ind : population.getIndividus()) 
            sumfitness += (1/ind.getFitness());

        pick = (float) Math.random();
        
        sum = 0;

        if(sumfitness != 0) {
            for(i = 0; (sum < pick) && (i < popsize-1); i++)
                sum += (1/population.getIndividus().get(i).getFitness())/sumfitness;
        }
        else
            i = (new Random()).nextInt(popsize-1);

        return population.getIndividus().get(i);
    }
    
    
    /**
     * <p>Exécute une fois l'algorithme génétique sur la population.
     * 
     * @throws AlgorithmException
     *      quand les {@link Parametre} ou les {@link Operateur} n'ont pas pu être chargés
     * @since 2012
     * @see AlgorithmException
     */
    @Override
    public void executer() throws AlgorithmException {
        // Récupération de la proportion d'individus qui survivent
        float tauxSurvie = -1;
        for(Parametre param : parametres.getParametres())
            if(param.getNom().equals(TAUX_SURVIE))
                tauxSurvie = (Float) param.getValeur();
        
        if(tauxSurvie==-1)
            throw new AlgorithmException("Failed to load algorithm's parameters");

        // Vérification des opérateurs
        if(operateurs==null)
            throw new AlgorithmException("Failed to load operators");
        if(operateurs.isEmpty())
            throw new AlgorithmException("Failed to load operators");
        if(operateurs.size()!=2)
            throw new AlgorithmException("Failed to load operators");

        // Récupération des probabilités d'application des opérateurs
        float probCroisement = -1, probMutation = -1;
        for(Parametre param : parametres.getParametres()) {
            if(param.getNom().equals(PROB_CROISEMENT))
                probCroisement = (Float) param.getValeur();
            else if(param.getNom().equals(PROB_MUTATION))
                probMutation = (Float) param.getValeur();
        }
        
        if(probCroisement==-1 || probMutation==-1)
            throw new AlgorithmException("Failed to load algorithm's parameters");
        
        //Trier par ordre décroissant la population selon la fitness des individus
        population.trier();

        
        Population anciennePopulation = population;
        Population nouvellePopulation = new Population(anciennePopulation.getNom());
        nouvellePopulation.setNoCloneType(anciennePopulation.getNoCloneType());
        
        //Mettre à jour les individus survivants
        int nbIndividusSurvie = Math.round(anciennePopulation.getPopulationSize() * tauxSurvie);
        
        for(int i = 0; i < nbIndividusSurvie; i++) {
            Individu ind = anciennePopulation.getIndividus().get(i);
            nouvellePopulation.ajouterIndividu(ind);
        }
        
        //Application des opérateurs aux individus selectionnés
        int borneSup = anciennePopulation.getPopulationSize()-nbIndividusSurvie;
        Individu[] enfants = new Individu[2];
        
        for(int i=0; i < borneSup; ) {
            Individu ind1 = selectionner();
            Individu ind2 = selectionner();

            float tauxAppOpCrois = (float) Math.random();
            float tauxAppOpMuta = (float) Math.random();

            // SCOHY
            // modification de croisement / mutation
            // si le croisement a lieu mais pas la mutation, le else de la mutation va écraser l'effet du croisement
            /*
            if(tauxAppOpCrois <= probCroisement) {
                enfants = (Individu[]) getOperateurs().get(0).operate(ind1,ind2);
            } else {
                enfants[0] = ind1;
                enfants[1] = ind2;
            }

            if(tauxAppOpMuta<=probMutation){
                enfants = (Individu[]) getOperateurs().get(1).operate((Object[]) enfants);
            } else {
                enfants[0] = ind1;
                enfants[1] = ind2;
            }
            //*/
            //*
            if(tauxAppOpCrois <= probCroisement) 
                enfants = (Individu[])getOperateurs().get(0).operate(ind1, ind2);
            else {
                enfants[0] = ind1;
                enfants[1] = ind2;
            }

            if(tauxAppOpMuta<=probMutation)
                enfants = (Individu[]) getOperateurs().get(1).operate((Object[]) enfants);
            //*/
            //fin modif

            
            if(nouvellePopulation.getNoCloneType().equals("fitness")) {
                if((i < borneSup)) {
                    calculateFitness(enfants[0]);
                    if(nouvellePopulation.ajouterIndividu(enfants[0])) i++;
                }
                
                if((i < borneSup)) {
                    calculateFitness(enfants[1]);
                    if(nouvellePopulation.ajouterIndividu(enfants[1])) i++;
                }
            }
            else {
                /*
                 * cette portion de code sera plus rapide
                 * car le fitness n'est pas calculé à chaque fois
                 */
                if((i < borneSup) && nouvellePopulation.ajouterIndividu(enfants[0])) {
                    calculateFitness(enfants[0]);
                    i++;
                }

                if((i < borneSup) && nouvellePopulation.ajouterIndividu(enfants[1])) {
                    calculateFitness(enfants[1]);
                    i++;
                }
            }
        }
        
        population = nouvellePopulation;
    }
    
    /**
     * <p>
     * Évalue un {@link Individu} et garde le
     * temps d'exécution de cette méthode.
     * 
     * @param bob l'individu à évaluer
     * @since 2012
     */
    private void calculateFitness(Individu bob) {
        Date start = new Date();
        float newFitness = getProbleme().fonctionObjectif(bob);				
        Date end = new Date();
        bob.setFitness(newFitness);
        timeObjectiveFunction += end.getTime() - start.getTime();
    }

}
