package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author shaw ma
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored.
     *  @param cycles represents cycles.
     *  @param alphabet represents the alphabet. */
    Permutation(String cycles, Alphabet alphabet) {
        /* String trim() returns a string whose value is this string, with any
         * leeading and trailing whitespace removed.
         * String[] split(String regex) splits this string around matches
         * of the given regular expression.
         * 不在任何cycle中的character不存，map时直接return自身。*/
        _alphabet = alphabet;
        String cyc = cycles.trim().replace("(", "").replace(" ", "");
        _cycles = cyc.split("\\)");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
    *  c0c1...cm.
    *  @param cycle is the cycle need to be added in. */
    private void addCycle(String cycle) {
        String[] newCycles = new String[_cycles.length + 1];
        for (int i = 0; i < _cycles.length; i++) {
            newCycles[i] = _cycles[i];
        }
        newCycles[_cycles.length + 1] = cycle;
        _cycles = newCycles;
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
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        /* p is the index of the character which need to be permuted, in
         * alphabet.
         * If p is not in any cycle in _cycles, then return itself. */
        char pChar = _alphabet.toChar(p);
        for (int i = 0; i < _cycles.length; i++) {
            int pIndex = _cycles[i].indexOf(pChar);
            if (pIndex != -1) {
                char pNext = _cycles[i].charAt((pIndex + 1)
                                               % _cycles[i].length());
                return _alphabet.toInt(pNext);
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char cChar = _alphabet.toChar(c);
        for (int i = 0; i < _cycles.length; i++) {
            int cIndex = _cycles[i].indexOf(cChar);
            if (cIndex != -1) {
                int cNextIndex = (cIndex - 1) % _cycles[i].length();
                if (cNextIndex < 0) {
                    cNextIndex += _cycles[i].length();
                }
                char cNext = _cycles[i].charAt(cNextIndex);
                return _alphabet.toInt(cNext);
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int pIndex = _alphabet.toInt(p);
        return _alphabet.toChar(permute(pIndex));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int cIndex = _alphabet.toInt(c);
        return _alphabet.toChar(invert(cIndex));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        /* A permutation for which no value maps to itself means that all
         * character in a cycle of _cycles. */
        int totalAlphaNum = 0;
        for (int i = 0; i < _cycles.length; i++) {
            totalAlphaNum += _cycles[i].length();
        }
        if (totalAlphaNum == _alphabet.size()) {
            return true;
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles stored in a String array. */
    private String[] _cycles;
}
