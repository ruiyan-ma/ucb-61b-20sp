package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author shaw ma
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
    void set(int posn) {
        if (posn != 0) {
            throw error("Reflector has only one position.");
        }
    }

    @Override
    int convertBackward(int e) {
        throw error("Reflector can not convert backward.");
    }

    @Override
    void set(char cposn) {
        if (permutation().alphabet().toInt(cposn) != 0) {
            throw error("Reflector has only one position.");
        }
    }

}
