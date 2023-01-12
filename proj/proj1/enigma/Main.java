package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author ryan ma
 */
public final class Main {
    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            Main mainProcess = new Main(args);
            mainProcess.process();
            return;
        } catch (EnigmaException excp) {
            System.out.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        /* The constructor of Main class. */
        if (args.length < 1 || args.length > 3) {
            throw error("Error in Main.java: Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("Error in Main.java: could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("Error in Main.java: could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        String machineSetting = _input.nextLine().trim();
        if (!machineSetting.startsWith("*")) {
            throw new EnigmaException("Error in Main.java: Wrong setting format!");
        }
        setUp(enigma, machineSetting);
        String message;
        String result;
        while (_input.hasNextLine()) {
            message = _input.nextLine().trim();
            if (message.isEmpty()) {
                _output.println();
                continue;
            }
            if (message.startsWith("*")) {
                machineSetting = message;
                setUp(enigma, machineSetting);
                continue;
            }
            message = message.replace(" ", "");
            result = enigma.convert(message);
            printMessageLine(result);
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphabet = _config.nextLine();
            if (alphabet.contains("(") | alphabet.contains(")")
                    | alphabet.contains(" ")) {
                throw new EnigmaException("Error in Main.java: Wrong config format!");
            }
            _alphabet = new Alphabet(alphabet);

            int slotNum = 0;
            int pawlNum = 0;
            if (_config.hasNextInt()) {
                slotNum = _config.nextInt();
            } else {
                throw new EnigmaException("Error in Main.java: Wrong config format!");
            }
            if (_config.hasNextInt()) {
                pawlNum = _config.nextInt();
            } else {
                throw new EnigmaException("Error in Main.java: Wrong config format!");
            }

            ArrayList<Rotor> availRotors = new ArrayList();
            while (_config.hasNext()) {
                availRotors.add(readRotor());
            }

            return new Machine(_alphabet, slotNum, pawlNum, availRotors);
        } catch (NoSuchElementException excp) {
            throw error("Error in Main.java: configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        /* 因为config file里可能会有一个rotor占据了两行的情况，所以
         * 使用hasNext(Pattern pattern)匹配，匹配到最后一个(...)
         * 即为当前rotor config的结束*/
        try {
            String Name = _config.next();
            String TypeAndNotch = _config.next();
            String Notches = TypeAndNotch.substring(1);
            String Cycles = "";
            while (_config.hasNext(Pattern.compile("(\\(.+\\))+"))) {
                Cycles += _config.next();
            }
            Permutation Perm = new Permutation(Cycles, _alphabet);

            if (TypeAndNotch.charAt(0) == 'M') {
                return new MovingRotor(Name, Perm, Notches);
            } else if (TypeAndNotch.charAt(0) == 'N') {
                return new FixedRotor(Name, Perm);
            } else {
                return new Reflector(Name, Perm);
            }
        } catch (NoSuchElementException excp) {
            throw error("Error in Main.java: bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        /* 见spec中Input and Output部分关于settings的格式叙述
         * Example: * B Beta III IV I AXLE (YF) (ZH)
         * Pulgboard pair可能没有
         * PlugIndex用于存储Plugboard pair的起始位置 */
        String Setting = settings.trim();
        if (!Setting.startsWith("*")) {
            throw new EnigmaException("Error in Main.java: Wrong format of setting!");
        }
        Setting = Setting.replace("*", "").trim();
        String[] Settings = Setting.split(" ");
        int plugIndex = Settings.length - 1;
        for (; plugIndex > 0; plugIndex--) {
            if (!Settings[plugIndex].startsWith("(")) {
                plugIndex += 1;
                break;
            }
        }
        boolean ringExist = false;
        if (plugIndex - 1 != M.numRotors()) {
            if (plugIndex - 2 == M.numRotors()) {
                ringExist = true;
            } else {
                throw new EnigmaException("Not enough rotors in setting! ");
            }
        }
        /* 接下来将Settings中的Rotor, positionSetting, PlugboardPair拆开 */
        String[] Rotors;
        String positionSetting;
        String ringSetting = null;
        if (!ringExist) {
            Rotors = new String[plugIndex - 1];
            positionSetting = Settings[plugIndex - 1];
        } else {
            Rotors = new String[plugIndex - 2];
            positionSetting = Settings[plugIndex - 2];
            ringSetting = Settings[plugIndex - 1];
        }
        for (int i = 0; i < Rotors.length; i++) {
            Rotors[i] = Settings[i];
        }
        boolean plugPairExist;
        String plugboardPair = "";
        if (plugIndex == Settings.length) {
            plugPairExist = false;
        } else {
            plugPairExist = true;
            for (int i = plugIndex; i < Settings.length; i++) {
                plugboardPair += Settings[i];
            }
        }
        for (int i = 0; i < Rotors.length; i++) {
            for (int j = i + 1; j < Rotors.length; j++) {
                if (Rotors[i].equals(Rotors[j])) {
                    throw new EnigmaException("Error in Main.java: Repeated Rotor! ");
                }
            }
        }
        M.insertRotors(Rotors);
        if (!M.getRotor(0).reflecting()) {
            throw new EnigmaException("Error in Main.java: First rotor should be a reflector! ");
        }
        if (!ringExist) {
            M.setRotors(positionSetting);
        } else {
            char[] positionSettings = positionSetting.toCharArray();
            char[] ringSettings = ringSetting.toCharArray();
            int[] ringInt = new int[ringSettings.length];
            for (int i = 0; i < positionSettings.length; i++) {
                int Position = _alphabet.toInt(positionSettings[i]);
                ringInt[i] = _alphabet.toInt(ringSettings[i]);
                int newPosition = Position - ringInt[i];
                if (newPosition < 0) {
                    newPosition += 26;
                }
                positionSettings[i] = _alphabet.toChar(newPosition);
            }
            positionSetting = String.valueOf(positionSettings);
            M.setRotors(positionSetting);
            for (int i = 1; i < Rotors.length; i++) {
                Rotor rotor = M.getRotor(i);
                rotor.modifyNotch(ringInt[i - 1]);
            }
        }
        if (plugPairExist) {
            Permutation plugBoard = new Permutation(plugboardPair, _alphabet);
            M.setPlugboard(plugBoard);
        } else {
            Permutation plugBoard = new Permutation("", _alphabet);
            M.setPlugboard(plugBoard);
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if ((i + 1) % 5 == 0) {
                _output.print(" ");
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
