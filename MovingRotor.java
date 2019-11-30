package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Vineet Vashist
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        rotornotch = notches.split("");
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    @Override
    boolean atNotch() {
        for (String c : rotornotch) {
            if (permutation().alphabet().toChar(setting()) == c.charAt(0)) {
                return true;
            }
        }
        return false;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        int nextpos = permutation().wrap(setting() + 1);
        set(nextpos);
    }

    /** Notches for the current rotor. */
    private String[] rotornotch;


}
