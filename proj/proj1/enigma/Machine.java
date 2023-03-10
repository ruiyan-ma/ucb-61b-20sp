package enigma;

import java.util.Collection;
import java.util.Iterator;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author ryan ma
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _slotNum = numRotors;
        _pawlNum = pawls;
        _avaiRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _slotNum;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawlNum;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _Rotors = new Rotor[numRotors()];
        for (int i = 0; i < rotors.length; i++) {
            boolean foundRotor = false;
            Iterator<Rotor> iter = _avaiRotors.iterator();
            while (iter.hasNext()) {
                Rotor rotor = iter.next();
                if (rotors[i].equals(rotor.name())) {
                    _Rotors[i] = rotor;
                    foundRotor = true;
                    break;
                }
            }
            if (!foundRotor) {
                throw new EnigmaException(rotors[i] + " not available!");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _slotNum - 1) {
            throw new EnigmaException("Error in Machine.java: Wrong length of position setting!");
        }
        for (int i = 0; i < setting.length(); i++) {
            _Rotors[i + 1].set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        /* ????????????Advance:
         * ???advance????????????????????????moving rotor???AtNotch?????? */
        if (_pawlNum <= 1) {
            _Rotors[_Rotors.length - 1].advance();
        } else {
            boolean[] RotorAtNotch = new boolean[_pawlNum];
            for (int i = 0; i < RotorAtNotch.length - 1; i++) {
                RotorAtNotch[RotorAtNotch.length - 1 - i] =
                    _Rotors[_Rotors.length - 1 - i].atNotch();
            }

            // ???????????????rotor??????
            _Rotors[_Rotors.length - 1].advance();
            /* ??????????????????rotor????????????Notch
             * ????????????rotor?????????(???advance??????)??????notch??????left rotor??????
             * ??????moving rotor: ????????????notch?????????????????????left rotor??????
             * ???????????????Rotor???????????????????????????????????????????????????Rotor???Notch
             * ???????????????rotor?????????????????? */
            if (RotorAtNotch[RotorAtNotch.length - 1] & (!RotorAtNotch[RotorAtNotch.length - 2])) {
                _Rotors[_Rotors.length - 2].advance();
            }
            for (int i = 1; i < RotorAtNotch.length - 1; i++) {
                if (RotorAtNotch[RotorAtNotch.length - 1 - i]) {
                    _Rotors[_Rotors.length - 1 - i].advance();
                    if (!RotorAtNotch[RotorAtNotch.length - 2 - i]) {
                        _Rotors[_Rotors.length - 2 - i].advance();
                    }
                }
            }
        }

        /* ????????????convert:
         * Plug permute -> ConvertForward... -> Reflector ->
         * ConvertBackward... -> Plug invert */
        int Number = _plugboard.permute(c);
        for (int i = _Rotors.length - 1; i > 0; i--) {
            Number = _Rotors[i].convertForward(Number);
        }
        Number = _Rotors[0].convertForward(Number);
        for (int i = 1; i < _Rotors.length; i++) {
            Number = _Rotors[i].convertBackward(Number);
        }
        Number = _plugboard.invert(Number);
        return Number;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
    *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replace(" ", "");
        int[] intArr = new int [msg.length()];
        for (int i = 0; i < intArr.length; i++) {
            intArr[i] = _alphabet.toInt(msg.charAt(i));
        }
        char[] charArr = new char[intArr.length];
        String resultMsg = "";
        for (int i = 0; i < intArr.length; i++) {
            charArr[i] = _alphabet.toChar(convert(intArr[i]));
        }
        resultMsg = resultMsg.valueOf(charArr);
        return resultMsg;
    }

    /** Return _Rotors[ INDEX ]. */
    Rotor getRotor(int index) {
        return _Rotors[index];
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** The number of rotor slots. */
    private int _slotNum;

    /** The number of rotor pawls. */
    private int _pawlNum;

    /** The set of all my available rotors. */
    private final Collection<Rotor> _avaiRotors;

    /** Array of rotors that I'm using now. */
    private Rotor[] _Rotors;

    /** The plugboard. */
    private Permutation _plugboard;
}
