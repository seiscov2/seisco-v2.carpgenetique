package carpgenetique.algo;

import java.util.Random;
import seisco.algo.Operateur;

/**
 * <p>
 * Opérateur de croisement OX
 * </p>
 * <p>
 * Pour deux parents P1 et P2 de longueur t, on commence par tirer au sort deux
 * positions p et q avec p ≤ q. Pour construire l'enfant E1, la portion de P1
 * entre p et q inclus est copiée dans E1, aux mêmes positions. Dans OX, le
 * balayage de P2 et le rangement dans l'enfant s'effectuent de façon
 * circulaire, en commençant à l'index q+1 (mod t). La construction de l'enfant
 * E2 est identique, en permutant les rôles de P1 et P2.
 * </p>
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see Operateur
 */
public class CroisementOX extends Operateur {

    /**
     * <p>Applique l'{@link Operateur} de croisement OX aux objets passés à la méthode.
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

        if(allInstanceOfIndividu) {
            Individu[] individus = new Individu[operandes.length];
            for(int i = 0; i < individus.length; i++)
                individus[i] = (Individu) operandes[i];
            
            return operate(individus);
        }
        
        return null;

    }

    /**
     * <p>
     * Applique l'opérateur de croisement OX
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
        if(individus.length>1) {
            int a,b,p,q;
            Individu[] result = new Individu[individus.length];
            for(int i = 0; i<individus.length-1; i+=2) {
                Individu parent1 = individus[i];
                Individu parent2 = individus[i+1];

                int tailleChromosome = parent1.getTaches().size();
                
                // Le parametre de Individu permet de fixer le nombre de taches
                // Voir CroisementLOX pour plus de description
                Individu enfant1 = new Individu(tailleChromosome);
                Individu enfant2 = new Individu(tailleChromosome);

                a = (new Random()).nextInt(tailleChromosome);
                b = (new Random()).nextInt(tailleChromosome);

                if(a < b) {
                    p = a;
                    q = b;
                } else {
                    p = b;
                    q = a;
                }

                for(int j=p; j <= q; j++) {
                    enfant1.getTaches().set(j, parent1.getTaches().get(j));
                    enfant2.getTaches().set(j, parent2.getTaches().get(j));	
                }

                int parcoursParent2 = q+1;
                int parcoursEnfant1 = q+1;
                do {
                    if(parcoursParent2==tailleChromosome)
                        parcoursParent2 = 0;
                    
                    if(parcoursEnfant1==tailleChromosome)
                        parcoursEnfant1 = 0;
                    
                    if(parcoursEnfant1<p || parcoursEnfant1>q) {
                        if(!enfant1.getTaches().contains(parent2.getTaches().get(parcoursParent2))){
                            enfant1.getTaches().set(parcoursEnfant1, parent2.getTaches().get(parcoursParent2));
                            parcoursEnfant1++;
                        }
                        
                        parcoursParent2++;
                    }
                } while(parcoursEnfant1 != p);

                int parcoursParent1 = q+1;
                int parcoursEnfant2 = q+1;
                do {
                    if(parcoursParent1==tailleChromosome)
                        parcoursParent1 = 0;
                    
                    if(parcoursEnfant2==tailleChromosome)
                        parcoursEnfant2 = 0;
                    
                    if(parcoursEnfant2<p || parcoursEnfant2>q) {
                        if(!enfant2.getTaches().contains(parent1.getTaches().get(parcoursParent1))) {
                            enfant2.getTaches().set(parcoursEnfant2, parent1.getTaches().get(parcoursParent1));
                            parcoursEnfant2++;
                        }
                        
                        parcoursParent1++;
                    }
                } while(parcoursEnfant2 != p);

                result[i] = enfant1;
                result[i+1] = enfant2;

            }
            return result;
        }
        
        return null;
    }

}
