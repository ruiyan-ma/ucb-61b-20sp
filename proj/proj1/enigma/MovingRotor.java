package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author shaw ma
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        char setTing = permutation().alphabet().toChar(setting());
        if (_notches.indexOf(setTing) != -1) {
            return true;
        }
        return false;
    }

    @Override
    void advance() {
        /* advance()只负责完成advance这个步骤；由外部调用
         * 来执行advance，其本身不关心advance的条件*/
        set(permutation().wrap(setting() + 1));
    }

    /** Return _notches. */
    String getNotches() {
        return _notches;
    }

    /** Modify _notches according to RING setting. */
    void modifyNotch(int ring) {
        char[] notches = _notches.toCharArray();
        int[] notchInt = new int[notches.length];
        for (int i = 0; i < notches.length; i++) {
            notchInt[i] = alphabet().toInt(notches[i]) - ring;
            if (notchInt[i] < 0) {
                notchInt[i] += 26;
            }
            notches[i] = alphabet().toChar(notchInt[i]);
        }
        _notches = String.valueOf(notches);
    }

    /** Notches of this moving rotor. */
    private String _notches;

}
