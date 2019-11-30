package enigma;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;


import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Vineet Vashist
 *  */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;

        if (numRotors > 1) {
            _numslot = numRotors;
        } else {
            throw new EnigmaException(" Invalid Number of rotors passed. :"
            + numRotors + "  | Should be > 1. ");
        }

        if (pawls >= 0 && pawls < _numslot) {
            _pawls = pawls;
        } else {
            throw new EnigmaException(" Invalid pawl count passed. :"
                    + pawls + "  | Should be between 0 <= PAWLS < " + _numslot);
        }

        _allrotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numslot;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _tempallrotors = _allrotors;
        selectedrotors = new ArrayList<>();

        if (numRotors() != rotors.length) {
            throw new EnigmaException("Rotor slot count ( "
                    + numRotors() + "does not match the selected "
                    + "rotor count" + rotors.length);
        }
        int fixcheck = (numRotors() - numPawls() - 1);
        int lesscheck = 0;

        for (int i = 0; i < rotors.length; i++) {
            String s = rotors[i];
            Iterator iterator = _tempallrotors.iterator();
            while (iterator.hasNext()) {
                Rotor r = (Rotor) iterator.next();
                String rname = r.name();
                if (s.equals(rname)) {
                    lesscheck++;
                    if (i == 0 && r.reflecting()) {
                        selectedrotors.add(r);
                    } else if (!selectedrotors.contains(r) && !r.reflecting()) {

                        if (fixcheck != 0 && r.rotates()) {
                            throw new EnigmaException("Misplaced "
                                    + "pos. of fixed rotor in Machine");
                        } else if (!r.rotates()) {
                            fixcheck--;
                            selectedrotors.add(r);
                        } else {
                            selectedrotors.add(r);
                        }
                    } else {
                        throw new EnigmaException("Rotors cannot be repeated"
                                + ", duplicate found for" + r.name());
                    }
                }
            }
        }
        if (lesscheck != numRotors()) {
            throw new EnigmaException("Unequal number of rotors found.");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        int len = numRotors() - 1;
        if (len != setting.length()) {
            throw new EnigmaException(" Rotor settings not "
                   + "defined for all the rotors, found : "
                    + setting.length() + "instead of : " + len);
        }

        for (int i = 0; i < setting.length(); i++) {
            char c = setting.charAt(i);
            if (_alphabet.contains(c)) {
                selectedrotors.get(i + 1).set(c);
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine.
     *
     *  AAAB  AAAC  AABA  AABB  AABC  AACA  ABAB  ABAC
     * ABBA  ABBB  ABBC  ABCA  ACAB  ACAC  ACBA  ACBB
     * ACBC  ACCA  AAAB
     * */
    int convert(int c) {
        int temppawl = _pawls;
        Set<Rotor> advancecheck = new HashSet<>();

        int pos = _plugboard.permute(_plugboard.wrap(c));

        for (int i = selectedrotors.size() - 1; i >= 0; i--) {
            Rotor r = selectedrotors.get(i);

            if (r.rotates() && temppawl != 0) {
                if (i == selectedrotors.size() - 1) {
                    advancecheck.add(r);
                } else if (selectedrotors.get(i + 1).atNotch()) {
                    advancecheck.add(r);
                    advancecheck.add(selectedrotors.get(i + 1));
                }
                temppawl--;
            }
        }

        for (Rotor r : advancecheck) {
            r.advance();
        }

        for (int i = selectedrotors.size() - 1; i >= 0; i--) {
            Rotor r = selectedrotors.get(i);
            int var = r.permutation().wrap(pos);
            pos = r.convertForward(var);
        }

        for (int i = 1; i < selectedrotors.size(); i++) {
            Rotor r = selectedrotors.get(i);

            int var = r.permutation().wrap(pos);
            pos = r.convertBackward(var);
        }
        int result =  _plugboard.permute(_plugboard.wrap(pos));

        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {

        String inmsg = msg;
        String result = "";

        for (int i = 0; i < inmsg.length(); i++) {
            int intpos = _alphabet.toInt(inmsg.charAt(i));
            int p = convert(intpos);
            result += _alphabet.toChar(p);
        }

        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of Rotor slots. */
    private final int _numslot;

    /** Number of pawals : 0 <= PAWLS < NUMROTORS pawls  .*/
    private final int _pawls;

    /** List of all the available rotors.*/
    private final Collection<Rotor> _allrotors;

    /** temp list of all the available rotors.*/
    private Collection<Rotor> _tempallrotors = new ArrayList<>();

    /** Selected rotors for the machine.*/
    private ArrayList<Rotor> selectedrotors;

    /** Setting up the machines Plugboard. */
    private Permutation _plugboard;

}
