package carpgenetique.algo;

//~--- non-JDK imports --------------------------------------------------------
import seisco.algo.Operateur;

//~--- JDK imports ------------------------------------------------------------

import java.util.Random;

/**
 * <p>
 * Opérateur de croisement LOX
 * </p>
 * <p>
 * Pour deux parents P1 et P2 de longueur t, on commence par tirer au sort deux
 * positions p et q avec p ≤ q. Pour construire l'enfant E1, la portion de P1
 * entre p et q inclus est copiée dans E1, aux mêmes positions. Dans LOX, P2 est
 * ensuite balayé de 1 à t et les éléments non déjà présents dans l'enfant 
 * remplissent de gauche à droite les positions libres de l'enfant.
 * La construction de l'enfant E2 est identique,
 * en permutant les rôles de P1 et P2.
 * </p>
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see Operateur
 */
public class CroisementLOX extends Operateur {

    /**
     * <p>Applique l'{@link Operateur} de croisement LOX aux objets passés à la méthode.
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
    public Object[] operate(Object... operandes) {
        boolean allInstanceOfIndividu = true;

        for (Object o : operandes) {
            allInstanceOfIndividu &= o instanceof Individu;
        }

        if (allInstanceOfIndividu) {
            Individu[] individus = new Individu[operandes.length];

            for (int i = 0; i < individus.length; i++) {
                individus[i] = (Individu) operandes[i];
            }

            return operate(individus);
        }

        return null;
    }

    /**
     * <p>
     * Applique l'opérateur de croisement LOX
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
    private Individu[] operate(Individu... individus) {
        if (individus.length > 1) {
            Individu[] result = new Individu[individus.length];
            int a, b, p, q;

            for (int i = 0; i < individus.length-1; i += 2) {
                /*
                 * Pour chaque couple (placés consécutivement dans la
                 * population), on crée 2 enfants de même "taille" de chromosome
                 */
                Individu parent1 = individus[i];
                Individu parent2 = individus[i + 1];
                int tailleChromosome = parent1.getTaches().size();
                Individu enfant1 = new Individu(tailleChromosome);
                Individu enfant2 = new Individu(tailleChromosome);

                /*
                 * La taille du tableau est définie au moyen de la méthode
                 * "setNbTaches" de la classe SolutionCARP. Le parametre
                 * tailleChromosome des objets Individu (ci dessus) permet fait
                 * appel au constructeur qui fixe le nb de taches.
                 */

                a = (new Random()).nextInt(tailleChromosome);
                b = (new Random()).nextInt(tailleChromosome);

                if (a < b) {
                    p = a;
                    q = b;
                } else {
                    p = b;
                    q = a;
                }

                for (int j = p; j <= q; j++) {
                    /*
                     * On recopie un morceau (allant de p à q) du chromosome du
                     * premier parent vers le premier enfant et du second parent
                     * vers le second enfant
                     */
                    enfant1.getTaches().set(j, parent1.getTaches().get(j));
                    enfant2.getTaches().set(j, parent2.getTaches().get(j));
                }

                /*
                 * Pour l'enfant1, et en dehors de l'intervalle p-q, on recopie
                 * le reste du chromosome du deuxième parent
                 */
                int parcoursParent2 = 0;
                int parcoursEnfant1 = 0;

                do {
                    if ((parcoursEnfant1 < p) || (parcoursEnfant1 > q)) {
                        if (!enfant1.getTaches().contains(parent2.getTaches().get(parcoursParent2))) {
                            enfant1.getTaches().set(parcoursEnfant1, parent2.getTaches().get(parcoursParent2));
                            parcoursEnfant1++;
                        }

                        parcoursParent2++;
                    } else {
                        parcoursEnfant1 = q + 1;
                    }
                } while ((parcoursParent2 < tailleChromosome) && (parcoursEnfant1 < tailleChromosome));

                /*
                 * Pour l'enfant2, et en dehors de l'intervalle p-q, on recopie
                 * le reste du chromosome du premier parent
                 */
                int parcoursParent1 = 0;
                int parcoursEnfant2 = 0;

                do {
                    if ((parcoursEnfant2 < p) || (parcoursEnfant2 > q)) {
                        if (!enfant2.getTaches().contains(parent1.getTaches().get(parcoursParent1))) {
                            enfant2.getTaches().set(parcoursEnfant2, parent1.getTaches().get(parcoursParent1));
                            parcoursEnfant2++;
                        }

                        parcoursParent1++;
                    } else {
                        parcoursEnfant2 = q + 1;
                    }
                } while ((parcoursParent1 < tailleChromosome) && (parcoursEnfant2 < tailleChromosome));

                /*
                 * Les enfants sont ensuite placés dans la population de
                 * génération suivante
                 */
                result[i] = enfant1;
                result[i + 1] = enfant2;
            }

            return result;
        }

        return null;
    }
}