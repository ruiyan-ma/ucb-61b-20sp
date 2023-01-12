package enigma;

import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author shaw ma
 */
class Alphabet {

    /** Instance variable: String _alphabet.
     * Used to store the alphabet. */
    private String _alphabet;

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated.
     *  @param chars as the String to be stored in _alphabet. */
    Alphabet(String chars) {
        _alphabet = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _alphabet.indexOf(ch) != -1;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= 0 & index < size()) {
            return _alphabet.charAt(index);
        } else {
            throw error("character index " + index + " out of range.");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (contains(ch)) {
            return _alphabet.indexOf(ch);
        } else {
            throw error("character " + ch + " doesn't exist.");
        }
    }
}
