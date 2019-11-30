package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Vineet Vashist
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alphachars = chars;
        if (_alphachars.contains(" ") || _alphachars.contains("*")
                || _alphachars.contains(")") || _alphachars.contains("(")) {
            throw new EnigmaException("Invalid characters found, "
                   + "cannot have ' ' '*','(' and ')' as characters ");
        }

        for (int i = 0; i < _alphachars.length(); i++) {
            for (int j = i + 1; j < _alphachars.length(); j++) {
                if (_alphachars.charAt(i) == _alphachars.charAt(j)) {
                    throw new EnigmaException("There are duplicate alphabets "
                            + "present in the config file at");
                }
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphachars.length();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < size(); i++) {
            if (_alphachars.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {

        if (index >= 0 || index < size()) {
            return _alphachars.charAt(index);
        } else {
            throw new EnigmaException("Alphabet index out of bounds");
        }
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < size(); i++) {
            if (_alphachars.charAt(i) == ch) {
                return i;
            }
        }
        throw new EnigmaException("Couldn't locate input char :" + ch
                + " in the alphabet [" + _alphachars + "]");

    }

    /**String that contains the alphabet of the machine.*/
    private String _alphachars;
}
