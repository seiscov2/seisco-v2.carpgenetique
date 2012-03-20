package carpgenetique.algo;

import java.util.Random;
import seisco.algo.Operateur;
import seisco.util.graphe.Arc;

/**
 * <p>
 * Swap tire au sort deux positions p et q entre 1 et t inclus
 * et permute les tâches situées à ces positions.
 * </p>
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see Operateur
 */
public class MutationSwap extends Operateur {
    
    /**
     * <p>Applique l'{@link Operateur} de mutation SWAP aux objets passés à la méthode.
     * 
     * @param operandes
     *  le set d'{@link Object} sur lequel on tente d'appliquer l'opérateur
     * @return
     *  <p><b>null</b> si <code>operandes</code> ne
     *  contient pas que des {@link Individu}.
     *  <p>sinon, un nouveau set d'{@link Object}
     *  sur lequel on a appliqué l'{@link Object}.
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
     * Applique l'{@link Operateur} de mutation SWAP
     * aux {@link Individu} passés à la méthode.
     * </p>
     * 
     * @param individus
     *  le set d'{@link Individu} sur lequel on tente d'appliquer l'{@link Operateur}
     * @return
     *  <p><b>null</b> si le nombre d'<code>individus</code> est ≤ 0 .
     *  <p>sinon, un nouveau set d'{@link Individu} de taille égale au nombre
     *  d'<code>individus</code> sur lequel on a appliqué l'{@link Operateur}.
     * @since 2012
     */
    private Object[] operate(Individu... individus) {
        if(individus.length>0) {
            int index,p,q;
            Individu[] result = new Individu[individus.length];

            index = 0;

            for(Individu i : individus) {
                Individu nouvelIndividu = new Individu();

                for(Arc t : i.getTaches())
                nouvelIndividu.ajouterTache(t);

                Random rand = new Random();
                p = rand.nextInt(i.getTaches().size());
                do {
                    q = rand.nextInt(i.getTaches().size());
                } while (p==q);

                Arc temp = nouvelIndividu.getTaches().get(p);
                nouvelIndividu.getTaches().set(p,nouvelIndividu.getTaches().get(q));
                nouvelIndividu.getTaches().set(q, temp);

                result[index] = nouvelIndividu;

                index++;
            }

            return result;
        }
        
        return null;
    }

}
