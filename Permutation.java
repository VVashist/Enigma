
package enigma;
import java.util.HashMap;
import java.util.Map;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Vineet Vashist
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        tempcycles = cycles;

        tempcycles = tempcycles.replaceAll(" ", "");
        checkval(tempcycles);
        tempcycles = tempcycles.replaceAll("[(]", "");


        permcycle = tempcycles.split("[)]");

        for (String c : permcycle) {
            addCycle(c);
        }

        for (Map.Entry<Character, Character> entry : fwdhash.entrySet()) {
            bckhash.put(entry.getValue(), entry.getKey());
        }
    }

    /** Function to check if there are equal number of '(' ')' in the cycles.
     * And all c's are in Alphabet
     * @param cycles = contains the string of cycles for the permutation.*/

    void checkval(String cycles) {
        int str = 0; int lst = 0; int num = 0;
        char fr = '('; char lt = ')';
        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) == fr) {
                str++; num++;
                continue;
            } else if (cycles.charAt(i) == lt && num == 1) {
                lst++; num--;
                continue;
            }
            if (!alphabet().contains(cycles.charAt(i))) {
                throw new EnigmaException("Not a valid machine alphabet "
                        + "@parenthcheck-permutation : " + cycles);
            }
        }
        if (num != 0) {
            throw new EnigmaException("Invalid cycle format :"
                    + " each cycle must be between '(' and  ')' ");
        }
        if (str != lst) {
            throw new EnigmaException("Invalid cycle format :"
                   + " unequal number of '(' , ')' found");
        }
    }


    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm.
     * @param c = contains the string of cycles for the permutation.*/
    private void addCycle(String c) {

        for (int i = 0; i < c.length(); i++) {
            if (!fwdhash.containsKey(c.charAt(i))) {
                if (i == c.length() - 1) {
                    fwdhash.put(c.charAt(i), c.charAt(0));
                    break;
                }
                fwdhash.put(c.charAt(i), c.charAt(i + 1));

            } else {
                throw new EnigmaException("Found duplicate "
                       + "values in the permutation cycle" + c + " !");
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {

        int myindex = wrap(p);
        char c = alphabet().toChar(myindex);

        if (fwdhash.containsKey(c)) {
            char val = fwdhash.get(c);
            return alphabet().toInt(val);
        }
        return myindex;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {

        int myindex = wrap(c);

        char ch = alphabet().toChar(myindex);

        if (bckhash.containsKey(ch)) {
            char val = bckhash.get(ch);
            return alphabet().toInt(val);
        }
        return myindex;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {

        int temp = permute(alphabet().toInt(p));
        return alphabet().toChar(temp);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {

        int temp = invert(alphabet().toInt(c));
        return alphabet().toChar(temp);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String c : permcycle) {
            for (int i = 0; i < c.length(); i++) {
                if (!_alphabet.contains(c.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String array containing independent cycle rotations. */
    private String[] permcycle;

    /** temporary placeholder string containing the all the cycles. */
    private String tempcycles;

    /** Hashmap contianing forward key-value permutations in cycle rotations. */
    private HashMap<Character, Character> fwdhash = new HashMap<>();

    /** Hashmap contianing forward key-value permutations in cycle rotations. */
    private Map<Character, Character> bckhash = new HashMap<>();

}
