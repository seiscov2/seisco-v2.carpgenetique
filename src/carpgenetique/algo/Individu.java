package carpgenetique.algo;

import carp.SolutionCARP;
import java.util.ArrayList;
import seisco.util.graphe.Arc;

/**
 * <p>Représente un individu pour l'algorithme génétique qui s'applique au CARP
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see SolutionCARP
 */
public class Individu extends SolutionCARP {
    private float fitness = 0;

    /**
     * <p>Instancie un nouvel {@link Individu}
     * 
     * @since 2008
     * @see SolutionCARP#SolutionCARP() 
     */
    public Individu() {
        super();
    }

    /**
     * <p>Instancie un nouvel {@link Individu} avec un nombre de tâches prédéfini
     * 
     * @param nbTaches 
     *  le nombre de tâche que l'individu sera prêt à accueillir
     * 
     * @since 2012
     * @see SolutionCARP#SolutionCARP(int) 
     */
    public Individu(int nbTaches) {
        super(nbTaches);
    }
    
    /**
     * <p>Instancie un nouvel {@link Individu} avec des tâches prédéfinies
     * 
     * @param taches 
     *  l'{@link ArrayList} d'{@link Arc} déjà initialisée
     * 
     * @since 2012
     * @see SolutionCARP#SolutionCARP(java.util.ArrayList) 
     */
    public Individu(ArrayList<Arc> taches) {
        super(taches);
    }

    /**
     * <p>
     * Instancie un nouvel {@link Individu} avec des tâches prédéfinies et une fitness.
     * </p>
     * 
     * @param fitness
     *  la fitness que l'individu se verra attribuer
     * @param taches 
     *  l'{@link ArrayList} d'{@link Arc} déjà initialisée
     * @since 2012
     * @see SolutionCARP#SolutionCARP(java.util.ArrayList) 
     */
    public Individu(float fitness, ArrayList<Arc> taches) {
        super(taches);
        this.fitness = fitness;
    }

    /**
     * <p>Retourne la capacité d'adaptation de l'{@link Individu} au problème
     * 
     * @return
     *  <p>la fitness sous la forme d'un {@link Float}.
     *  <p>Plus elle est élevée, plus l'individu est meilleur
     * @since 2008
     * @see #setFitness(float) 
     */
    public float getFitness() {
        return fitness;
    }

    /**
     * <p>Remplace la capacité d'adaptation de l'{@link Individu} au problème
     * 
     * @param fitness <p>la fitness sous la forme d'un {@link Float}.
     * @since 2008
     * @see #getFitness() 
     */
    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    /**
     * <p>Vérifie l'égalité entre deux {@link Individu}.
     * 
     * @param obj
     *  l'{@link Object} à comparer avec l'instance courante
     * @return
     *  <p><b>true</b> si l'égalité est vérifiée.
     *  <p><b>false</b> dans le cas contraire.
     * @since 2012
     * @see SolutionCARP#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Individu)
            return super.equals(obj);

        return false;
    }

    /**
     * <p>
     * Retourne la représentation de la solution de
     * l'individu sous la forme d'un {@link String}.
     * </p>
     * 
     * @return
     *  la fitness et la liste des tâches de
     *  l'individu sous la forme d'un {@link String}.
     * @since 2012
     * @see SolutionCARP#toString() 
     */
    @Override
    public String toString(){
        String resultat = "Individu - Fitness = " + fitness + "\n";
        resultat += super.toString();

        return resultat;
    }
    
    /**
     * <p>Retourne une copie neuve de l'{@link Individu} courant.
     * 
     * @return
     *  une copie neuve de l'individu courant
     *  sous la forme d'un {@link Individu}.
     * @throws CloneNotSupportedException quand la copie ne peut avoir lieu
     * @since 2012
     * @see SolutionCARP#copy() 
     * @see Collections#copy(java.util.List, java.util.List) 
     */
    @Override
    public Individu copy() throws CloneNotSupportedException {
        Individu i = (Individu)super.copy();
        i.fitness = this.fitness;
        
        return i;
    }
}
