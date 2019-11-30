package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Vineet Vashist
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
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
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
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output.
     *  I/P
     *  * B Beta I II III AAAA
     * HELLO WORLD
     * * B Beta I II III AAAA
     * ILBDA AMTAZ*/
    private void process() {

        Machine M = readConfig();

        if (_input.hasNextLine()) {
            setUp(M, _input.nextLine());
        }

        String temp = "";
        while (_input.hasNextLine()) {
            String convert = _input.nextLine();
            if (convert.contains("*")) {
                temp += convert.substring(convert.indexOf("*"));
                setUp(M, temp);
                temp = "";
            } else {
                printMessageLine(M.convert(convert.replaceAll(" ", "")));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numrotors;
            int pawls;

            String alpha = _config.next();
            alpha = alpha.trim();
            _alphabet = new Alphabet(alpha);

            if (_config.hasNextInt()) {
                numrotors  = _config.nextInt();
            } else {
                throw new EnigmaException("Bad config file, "
                       + "couldn't set numrotors");
            }
            if (_config.hasNextInt()) {
                pawls = _config.nextInt();
                _config.nextLine();
            } else {
                throw new EnigmaException("Bad config file, "
                       + "couldn't set the pawls");
            }

            while (_config.hasNextLine()) {
                conflist.add(_config.nextLine().trim());
            }

            for (int i = 0; i < conflist.size(); i++) {
                if (conflist.get(i).startsWith("(")) {
                    conflist.set(i - 1, conflist.get(i - 1) + conflist.get(i));
                    conflist.remove(i);
                }
            }

            for (int i = 0; i < conflist.size(); i++) {
                String[] c = conflist.get(i).split(" ", 3);
                rname = c[0];
                rfeature = c[1];
                cycles = c[2];
                _allrotors.add(readRotor());
            }
            return new Machine(_alphabet, numrotors, pawls, _allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (rfeature.charAt(0) == 'M') {
                String notch = rfeature.substring(1);
                return new MovingRotor(rname,
                        new Permutation(cycles, _alphabet), notch);

            } else if (rfeature.charAt(0) == 'N') {
                String notch = rfeature.substring(1);
                return new FixedRotor(rname,
                        new Permutation(cycles, _alphabet));
            } else if (rfeature.charAt(0) == 'R') {
                String notch = rfeature.substring(1);
                return new Reflector(rname, new Permutation(cycles, _alphabet));
            } else {
                throw new EnigmaException("Configuration setting invalid"
                        + " : failed in readrotor ");
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.charAt(0) != '*') {
            throw error("Invalid setting : should start with '*'");
        }

        int numofslot = M.numRotors();

        String myset = settings.replace("*", "");
        myset = myset.trim();


        String[] indsetting = myset.split(" ", numofslot + 2);

        String[] rotorinslot = new String[numofslot];

        for (int i = 0; i < numofslot; i++) {

            rotorinslot[i] = indsetting[i];
        }

        M.insertRotors(rotorinslot);
        M.setRotors(indsetting[numofslot]);

        if (indsetting.length == numofslot + 2) {
            String plugcycle = indsetting[indsetting.length - 1];
            M.setPlugboard(new Permutation(plugcycle, _alphabet));
        } else {
            M.setPlugboard(new Permutation("", _alphabet));
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        ArrayList<String> test = new ArrayList<>();

        int len = msg.length();
        for (int i = 0; i < len; i += 5) {
            test.add(msg.substring(i, Math.min(len, i + 5)));
        }
        String v = test.toString();
        v =  v.replaceAll("[\\[]", "");
        v =  v.replaceAll(", ", " ");
        v = v.replaceAll("[\\]]", "");
        _output.println(v);

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Collection of all the rotors of the machine. */
    private Collection<Rotor> _allrotors = new ArrayList<>();

    /**Placeholder string to set the name of the rotor. */
    private String rname;

    /**Placeholder string to store the config of a rotor. */
    private String rfeature;

    /**Placeholder string to store the permutations cycles. */
    private String cycles;

    /**Placeholder list containing the entire config line of a rotor. */
    private ArrayList<String> conflist = new ArrayList<>();

    /**Placeholder string to store the output sting from the machine. */
    private String outres = "";
}
