package carpgenetique.algo;

import java.util.Random;
import seisco.algo.Operateur;

/**
 * <p>
 * Opérateur de croisement X1
 * </p>
 * <p>
 * Pour deux parents P1 et P2 de longueur t, on commence par choisir p=1 et
 * tirer au sort une position q avec p ≤ q. Pour construire l'enfant E1, la
 * portion de P1 entre p et q inclus est copiée dans E1, aux mêmes positions.
 * Dans X1, P2 est ensuite balayé de 1 à t et les éléments non déjà présents
 * dans l'enfant remplissent de gauche à droite les positions libres de
 * l'enfant. La construction de l'enfant E2 est identique, en permutant les
 * rôles de P1 et P2.
 * </p>
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see Operateur
 */
public class CroisementX1 extends Operateur {

    /**
     * <p>Applique l'{@link Operateur} de croisement X1 aux objets passés à la méthode.
     * 
     * @param operandes
     *  le set d'{@link Object} sur lequel on tente d'appliquer l'{@link Operateur}
     * @return
     *  <p><b>null</b> si <code>operandes</code> ne
     *  contient pas que des {@link Individu}.
     *  <p>sinon, un nouveau set d'{@link Object}
     *  sur lequel on a appliqué l'{@link Operateur}.
     * @since 2008
     * @see Operateur#operate(java.lang.Object[]) 
     */
    @Override
    public Object[] operate(Object...operandes) {
        boolean allInstanceOfIndividu = true;

        for(Object o : operandes)
            allInstanceOfIndividu &= o instanceof Individu;

        if(allInstanceOfIndividu){
            Individu[] individus = new Individu[operandes.length];
            for(int i = 0; i < individus.length; i++)
                individus[i] = (Individu) operandes[i];
            
            return operate(individus);
        }
        
        return null;
    }

    /**
     * <p>
     * Applique l'{@link Operateur} de croisement X1
     * aux {@link Individu} passés à la méthode.
     * </p>
     * 
     * @param individus
     *  le set d'{@link Individu} sur lequel on tente d'appliquer l'{@link Operateur}
     * @return
     *  <p><b>null</b> si le nombre d'<code>individus</code> est ≤ 1 .
     *  <p>sinon, un nouveau set d'{@link Individu} de taille égale au nombre
     *  d'<code>individus</code> (décrémenté de 1 si impair, car l'opérateur
     *  est appliqué deux par deux…) sur lequel on a appliqué l'{@link Operateur}.
     * @since 2012
     */
    private Object[] operate(Individu... individus) {
        if(individus.length > 1) {
            int p,q;
            Individu[] result = new Individu[individus.length];
            for(int i = 0; i < individus.length-1; i+=2) {
                Individu parent1 = individus[i];
                Individu parent2 = individus[i+1];

                int tailleChromosome = parent1.getTaches().size();
                
                // Le parametre de Individu permet de fixer le nombre de taches
                // Voir CroisementLOX pour plus de description
                Individu enfant1 = new Individu(tailleChromosome);
                Individu enfant2 = new Individu(tailleChromosome);

                //enfant1.getMesTaches().setSize(tailleChromosome);
                //enfant2.getMesTaches().setSize(tailleChromosome);
                
                p = 1;
                do{
                    q = (new Random()).nextInt(tailleChromosome);
                } while(p==q);

                for(int j=p; j <= q; j++) {
                    enfant1.getTaches().set(j, parent1.getTaches().get(j));
                    enfant2.getTaches().set(j, parent2.getTaches().get(j));
                }

                int parcoursParent2 = 0;
                int parcoursEnfant1 =0;
                do {
                    if(parcoursEnfant1<p || parcoursEnfant1>q) {
                        if(!enfant1.getTaches().contains(parent2.getTaches().get(parcoursParent2))){
                            enfant1.getTaches().set(parcoursEnfant1, parent2.getTaches().get(parcoursParent2));
                            parcoursEnfant1++;
                        }
                        parcoursParent2++;
                    } else
                        parcoursEnfant1 = q+1;
                } while(parcoursParent2<tailleChromosome && parcoursEnfant1<tailleChromosome);

                int parcoursParent1 = 0;
                int parcoursEnfant2 =0;
                do {
                    if(parcoursEnfant2<p || parcoursEnfant2>q){
                        if(!enfant2.getTaches().contains(parent1.getTaches().get(parcoursParent1))){
                            enfant2.getTaches().set(parcoursEnfant2, parent1.getTaches().get(parcoursParent1));
                            parcoursEnfant2++;
                        }
                        parcoursParent1++;
                    } else
                        parcoursEnfant2 = q+1;
                } while(parcoursParent1<tailleChromosome && parcoursEnfant2<tailleChromosome);

                result[i] = enfant1;
                result[i+1] = enfant2;
            }
            
            return result;
        }
        
        return null;
    }
}
