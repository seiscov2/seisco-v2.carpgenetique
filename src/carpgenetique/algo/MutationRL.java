package carpgenetique.algo;

import seisco.algo.Operateur;

public class MutationRL extends Operateur {

    @Override
    public Object[] operate(Object... operandes) {
        boolean allInstanceOfIndividu = true;

        for(Object o : operandes)
            allInstanceOfIndividu &= o instanceof Individu;
        
        if(allInstanceOfIndividu) {
            Individu[] individus = new Individu[operandes.length];
            for(int i = 0; i < individus.length; i++)
                individus[i] = (Individu) operandes[i];
            
            return operate(individus);
        }
        else return null;
    }
    
    private Object[] operate(Individu... individus) {
        if(individus.length > 0) {
            return null;
        }
        return null;
    }
    
}
