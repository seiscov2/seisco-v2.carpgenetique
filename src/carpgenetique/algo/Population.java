package carpgenetique.algo;

import carp.ProblemeCARP;
import jade.content.Concept;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import seisco.util.graphe.Arc;

/**
 * <p>
 * Représente une population d'{@link Individu} pour
 * l'algorithme génétique qui s'applique au CARP.
 * </p>
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see Concept
 */
public class Population implements Concept {
    private String nom;
    private List<Individu> individus;
    
    /**
     * <p>Type de population.
     * Vaut soit "task", "fitness" ou "none".
     * Toute autre valeur sera considérée équivalente à "none".
     * 
     * <p>
     * Les différents types et leur signification:
     * <ul>
     *  <li>
     *      "<b> task </b>" : pas d'{@link Individu} ayant
     *      la même liste de tâches qu'un autre.
     *  </li>
     *  <li>
     *      "<b> fitness </b>" : pas d'{@link Individu}
     *      ayant le même fitness qu'un autre.
     *  </li>
     *  <li>
     *      "<b> none </b>" : aucune restriction, les
     *      {@link Individu}s peuvent avoir des clones.
     *  </li>
     * </ul>
     * 
     * @since 2012
     * @see Individu#equals(java.lang.Object) 
     */
    private String noCloneType = "none";

    /**
     * <p>Instancie une nouvelle {@link Population}
     * 
     * @since 2008
     */
    public Population() {
        super();
        nom = new String();
        individus = new ArrayList<Individu>();
    }

    /**
     * <p>Instancie une nouvelle {@link Population} et la nomme
     * 
     * @param nom 
     *  le nom de la {@link Population} sous forme de {@link String}.
     * @since 2008
     */
    public Population(String nom){
        this();
        this.nom = nom;
    }

    /**
     * <p>
     * Instancie une nouvelle {@link Population} et
     * lui fournit déjà un set d'{@link Individu}.
     * </p>
     * 
     * @param individus 
     *  la liste d'{@link Individu} déjà initialisée.
     * @since 2008
     */
    public Population(List<Individu> individus) {
        this();
        this.individus = individus;
    }

    /**
     * <p>
     * Instancie une nouvelle {@link Population}, la nomme
     * et lui fournit déjà un set d'{@link Individu}.
     * </p>
     * 
     * @param nom
     *  le nom de la {@link Population} sous forme de {@link String}.
     * @param individus
     *  la liste d'{@link Individu} déjà initialisée.
     * @since 2008
     */
    public Population(String nom, List<Individu> individus) {
        super();
        this.nom = nom;
        this.individus = individus;
    }

    /**
     * <p>Retourne le nom de la {@link Population}
     * 
     * @return le nom de la {@link Population}.
     * @since 2008
     * @see #setNom(java.lang.String) 
     */
    public String getNom() {
        return nom;
    }

    /**
     * <p>Remplace le nom de la {@link Population}
     * 
     * @param nom le nouveau nom de la {@link Population}
     * @since 2008
     * @see #getNom() 
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * <p>
     * Remplace le type de population.
     * Si le paramètre ne vaut pas "task" ou "fitness",
     * {@link #noCloneType} prendra la valeur "none".
     * 
     * @param noCloneType le nouveau type de population
     * @since 2012
     * @see #noCloneType
     * @see #getNoCloneType() 
     */
    public void setNoCloneType(String noCloneType) {
        if(noCloneType.equalsIgnoreCase("task") || noCloneType.equalsIgnoreCase("fitness"))
            this.noCloneType = noCloneType.toLowerCase();
        else
            this.noCloneType = "none";
    }

    /**
     * <p>
     * Retourne le type de population.
     * 
     * @return {@link #noCloneType}
     * @since 2012
     * @see #setNoCloneType(java.lang.String) 
     */
    public String getNoCloneType() {
        return noCloneType;
    }

    /**
     * <p>Retourne la liste des {@link Individu} de la {@link Population}
     * 
     * @return tous les {@link Individu} de la {@link Population}.
     * @since 2008
     * @see #setIndividus(java.util.List) 
     */
    public List<Individu> getIndividus() {
        return individus;
    }

    /**
     * <p>Remplace tous les {@link Individu} de la {@link Population}.
     * 
     * @param individus
     *  les nouveaux {@link Individu} qui remplacent
     *  la {@link Population}, sous forme de liste.
     * @since 2008
     * @see #getIndividus() 
     */
    public void setIndividus(List<Individu> individus) {
        this.individus = individus;
    }

    /**
     * <p>
     * Retourne le nombre d'{@link Individu} qui constitue la {@link Population}.
     * </p>
     * 
     * @return
     *  la taille de la liste d'{@link Individu}
     *  sous forme d'<code>int</code>.
     * @since 2008
     */
    public int getPopulationSize() {
        return individus.size();
    }

    /**
     * <p>Ajoute un nouvel {@link Individu} à la {@link Population} courante.
     * 
     * @param nouvelIndividu
     *  l'{@link Individu} à ajouter à la {@link Population}.
     * @return
     *  <p><b>true</b>  si l'{@link Individu} a été correctement ajouté
     *  <p><b>false</b> si l'{@link Individu} est déjà dans la {@link Population}
     *                  ou s'il n'a pas été correctement ajouté.
     * @since 2008
     * @see #retirerIndividu(carpgenetique.algo.Individu) 
     */
    public boolean ajouterIndividu(Individu nouvelIndividu) {
        if(noCloneType.equals("task"))
            if(individus.contains(nouvelIndividu))
                return false;
        
        if(noCloneType.equals("fitness"))
            for(Individu i : individus)
                if(i.getFitness() == nouvelIndividu.getFitness())
                    return false;
        
        /*
         * Dans tous les autres cas on l'ajoute !
         * Càd quand noCloneType vaut "none" ou
         * s'il n'y a pas de clones déjà présents.
         */
        return individus.add(nouvelIndividu);
        
    }

    /**
     * <p>Supprime un {@link Individu} de la {@link Population}.
     * 
     * @param i l'{@link Individu} qu'on désire supprimer
     * @return 
     *  <p><b>true</b>  si l'{@link Individu} a été correctement supprimé,
     *  <p><b>false</b> dans le cas contraire.
     * @since 2008
     * @see #ajouterIndividu(carpgenetique.algo.Individu) 
     */
    public boolean retirerIndividu(Individu i) {
        return individus.remove(i);
    }

    /**
    * <p>
    * Génère des {@link Individu} différents
    * et les ajoute à la {@link Population}.
    * </p>
    * 
    * @param nombre le nombre d'{@link Individu} à générer
    * @param p      le {@link ProblemeCARP} pour le lier aux chromosomes
    * @return la liste des {@link Individu} générés
    * @since 2012
    */
    public List<Individu> genererIndividus(int nombre, ProblemeCARP p) {
        int i=0;

        if(individus.isEmpty()) {
            Individu firstIndividu = new Individu();
			for (Iterator<Arc> it = ProblemeCARP.getGraphe().getArcs().iterator(); it.hasNext();)
				firstIndividu.getTaches().add(it.next());
			
            this.ajouterIndividu(firstIndividu);
            float fitness = p.fonctionObjectif(firstIndividu);
            firstIndividu.setFitness(fitness);
            i++;
        }

        for( ; i < nombre; i++) {
            Individu lastIndividu = individus.get(individus.size()-1);
            
            Individu nouvelIndividu = new Individu();
            for(Arc t : lastIndividu.getTaches())
                nouvelIndividu.getTaches().add(t);
            
            do {
                int indice1 = (new Random()).nextInt(ProblemeCARP.getGraphe().getArcs().size()-1);
                int indice2 = (new Random()).nextInt(ProblemeCARP.getGraphe().getArcs().size()-1);
                Arc tache1 = nouvelIndividu.getTaches().get(indice1);
                Arc tache2 = nouvelIndividu.getTaches().get(indice2);
                nouvelIndividu.getTaches().set(indice1, tache2);
                nouvelIndividu.getTaches().set(indice2, tache1);
            } while(!ajouterIndividu(nouvelIndividu));

            float fitness = p.fonctionObjectif(nouvelIndividu);
            nouvelIndividu.setFitness(fitness);
        }
		
	this.trier();
		
        return this.individus;
    }

    /**
     * <p>
     * Trie la {@link Population} par ordre croissant de fitness.
     * L'algorithme utilisé est un simple tri à bulles.
     * 
     * @since 2012
     */
    public void trier() {
        int longueur = individus.size();
        boolean inversion;	        
        do {
            inversion=false;
            for(int i = 0; i<longueur-1; i++)
                if(individus.get(i).getFitness() > individus.get(i+1).getFitness()){

                    Collections.swap(individus, i, i+1);
                    
                    inversion=true;
                }
        } while(inversion);
    }

    /**
     * <p>
     * Retourne sous forme de {@link String}
     * la représentation de la {@link Population}.
     * </p>
     * 
     * @return
     *  la {@link Population}, son nom et ses {@link Individu}.
     *  Le tout sous la forme d'un {@link String}.
     * @since 2012
     * @see Individu#toString() 
     */
    public String toString() {
        String resultat = "Population " + nom + "\n";
        int index = 1;
        for(Individu i : individus)
            resultat += index + ") " + i.toString();
        
        return resultat;
    }
}
