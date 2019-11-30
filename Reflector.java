package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Vineet Vashist
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    int convertForward(int p) {
        return permutation().permute(p);
    }

    @Override
    int convertBackward(int e) {
        throw new EnigmaException("Invalid backword call!"
               + "Reflector doesnt need backward mapping, "
               + "only permutes in one direction ");
    }


    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

}
