package io.github.dpetersanderson.mars;

import io.github.dpetersanderson.mars.mips.hardware.*;
import io.github.dpetersanderson.mars.util.Binary;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

final class MarsCommandLine {
    static final int DECIMAL = 0;
    static final int HEXADECIMAL = 1;
    static final int ASCII = 2;

    private static final String RANGE_SEPARATOR = "-";
    private static final String PROGRAM_ARGUMENT_SEPARATOR = "--";

    private MarsCommandLine() {}

    static ParseResult parse(String[] args) {
        PrintStream output = contains(args, "--messages-to-stderr") ? System.err : System.out;
        boolean noCopyright = contains(args, "--no-copyright");

        if (!noCopyright) {
            output.println("MARS " + Globals.version + "  Copyright " + Globals.copyrightYears + " "
                    + Globals.copyrightHolders + "\n");
        }

        Arguments arguments = new Arguments();
        CommandLine commandLine = new CommandLine(arguments);
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);

        SplitArguments splitArguments = splitProgramArguments(args);
        try {
            commandLine.parseArgs(splitArguments.marsArguments.toArray(new String[0]));
            if (commandLine.isUsageHelpRequested()) {
                commandLine.usage(output);
                return ParseResult.notRunnable(output, 0);
            }

            ParsedCommand parsedCommand = arguments.toParsedCommand(splitArguments.programArguments);
            return ParseResult.runnable(output, parsedCommand);
        } catch (CommandLine.ParameterException e) {
            output.println(e.getMessage());
            e.getCommandLine().usage(output);
            return ParseResult.notRunnable(output, 1);
        } catch (IllegalArgumentException e) {
            output.println(e.getMessage());
            return ParseResult.notRunnable(output, 1);
        }
    }

    static String[] checkMemoryAddressRange(String arg) {
        if (arg.indexOf(RANGE_SEPARATOR) <= 0 || arg.indexOf(RANGE_SEPARATOR) >= arg.length() - 1) {
            throw new IllegalArgumentException("Invalid/unaligned address or invalid range: " + arg);
        }

        String start = arg.substring(0, arg.indexOf(RANGE_SEPARATOR));
        String end = arg.substring(arg.indexOf(RANGE_SEPARATOR) + 1);
        try {
            if (Binary.stringToInt(start) > Binary.stringToInt(end)
                    || !Memory.wordAligned(Binary.stringToInt(start))
                    || !Memory.wordAligned(Binary.stringToInt(end))) {
                throw new IllegalArgumentException("Invalid/unaligned address or invalid range: " + arg);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid/unaligned address or invalid range: " + arg, e);
        }
        return new String[] {start, end};
    }

    private static boolean contains(String[] args, String option) {
        for (String arg : args) {
            if (option.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static SplitArguments splitProgramArguments(String[] args) {
        List<String> marsArguments = new ArrayList<>();
        List<String> programArguments = new ArrayList<>();
        boolean inProgramArguments = false;
        for (String arg : args) {
            if (inProgramArguments) {
                programArguments.add(arg);
            } else if (PROGRAM_ARGUMENT_SEPARATOR.equals(arg)) {
                inProgramArguments = true;
            } else {
                marsArguments.add(arg);
            }
        }
        return new SplitArguments(marsArguments, programArguments);
    }

    private static String normalizeRegister(String register) {
        String normalized = register.startsWith("$") ? register : "$" + register;
        if (RegisterFile.getUserRegister(normalized) == null && Coprocessor1.getRegister(normalized) == null) {
            throw new IllegalArgumentException("Invalid register name: " + register);
        }
        return normalized;
    }

    private static void validateFile(String filename) {
        if (!new File(filename).exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + filename);
        }
    }

    @Command(
            name = "Mars",
            mixinStandardHelpOptions = true,
            synopsisHeading = "Usage:%n",
            descriptionHeading = "%nDescription:%n",
            optionListHeading = "%nOptions:%n",
            parameterListHeading = "%nParameters:%n",
            sortOptions = false,
            description = "Assemble and run MIPS programs. With no arguments, MARS starts the GUI.")
    static final class Arguments {
        @Option(
                names = {"-a", "--assemble-only"},
                description = "Assemble only; do not simulate.")
        boolean assembleOnly;

        @Option(
                names = "--assemble-error-exit-code",
                paramLabel = "CODE",
                converter = IntegerDecodeConverter.class,
                description = "Exit with CODE if an assembly error occurs.")
        int assembleErrorExitCode;

        @Option(
                names = "--simulate-error-exit-code",
                paramLabel = "CODE",
                converter = IntegerDecodeConverter.class,
                description = "Exit with CODE if a simulation error occurs.")
        int simulateErrorExitCode;

        @Option(
                names = "--display-format",
                paramLabel = "FORMAT",
                defaultValue = "hex",
                converter = DisplayFormatConverter.class,
                description = "Display memory and registers as hex, dec, or ascii. Default: ${DEFAULT-VALUE}.")
        int displayFormat;

        @Option(
                names = {"-b", "--brief"},
                description = "Do not display register names or memory addresses with values.")
        boolean brief;

        @Option(
                names = {"-d", "--debug"},
                description = "Display MARS debugging statements.")
        boolean debug;

        @Option(names = "--delayed-branching", description = "Enable MIPS delayed branching.")
        boolean delayedBranching;

        @Option(
                names = "--dump",
                arity = "3",
                paramLabel = "SEGMENT_OR_RANGE FORMAT FILE",
                description = "Dump memory to a file. May be repeated.")
        List<String> dumpValues = new ArrayList<>();

        @Option(
                names = "--memory-configuration",
                paramLabel = "NAME",
                description = "Use memory configuration Default, CompactDataAtZero, or CompactTextAtZero.")
        String memoryConfiguration;

        @Option(names = "--messages-to-stderr", description = "Write MARS messages to standard error.")
        boolean messagesToStderr;

        @Option(names = "--no-copyright", description = "Do not display the copyright notice.")
        boolean noCopyright;

        @Option(names = "--no-pseudo", description = "Do not allow pseudo-instructions or extended formats.")
        boolean noPseudo;

        @Option(
                names = {"-p", "--project"},
                description = "Assemble all assembly files in the first input file's directory.")
        boolean project;

        @Option(names = "--start-at-main", description = "Start execution at global label main, if defined.")
        boolean startAtMain;

        @Option(names = "--self-modifying-code", description = "Allow writes and branches to text or data segments.")
        boolean selfModifyingCode;

        @Option(names = "--warnings-are-errors", description = "Treat assembler warnings as errors.")
        boolean warningsAreErrors;

        @Option(names = "--instruction-count", description = "Display the count of executed basic instructions.")
        boolean instructionCount;

        @Option(
                names = "--instruction-statistics",
                description = "Display the count of executed basic instructions and weighted cycles by category.")
        boolean instructionStatistics;

        @Option(
                names = "--max-steps",
                paramLabel = "COUNT",
                converter = IntegerDecodeConverter.class,
                description = "Maximum simulation step count. Non-positive values mean no maximum.")
        int maxSteps = -1;

        @Option(
                names = "--register",
                paramLabel = "REGISTER",
                description = "Display a register after simulation. May be repeated.")
        List<String> registers = new ArrayList<>();

        @Option(
                names = "--memory",
                paramLabel = "START-END",
                description = "Display a memory range after simulation. May be repeated.")
        List<String> memoryRanges = new ArrayList<>();

        @Parameters(index = "0..*", paramLabel = "FILE", description = "Assembly source files.")
        List<String> files = new ArrayList<>();

        ParsedCommand toParsedCommand(List<String> programArguments) {
            if (memoryConfiguration != null) {
                MemoryConfiguration config = MemoryConfigurations.getConfigurationByName(memoryConfiguration);
                if (config == null) {
                    throw new IllegalArgumentException("Invalid memory configuration: " + memoryConfiguration);
                }
            }

            List<String> normalizedRegisters = new ArrayList<>();
            for (String register : registers) {
                normalizedRegisters.add(normalizeRegister(register));
            }

            List<String[]> normalizedMemoryRanges = new ArrayList<>();
            for (String memoryRange : memoryRanges) {
                normalizedMemoryRanges.add(checkMemoryAddressRange(memoryRange));
            }

            for (String file : files) {
                validateFile(file);
            }

            List<String[]> dumps = new ArrayList<>();
            for (int i = 0; i < dumpValues.size(); i += 3) {
                dumps.add(new String[] {dumpValues.get(i), dumpValues.get(i + 1), dumpValues.get(i + 2)});
            }

            return new ParsedCommand(
                    !assembleOnly,
                    debug,
                    displayFormat,
                    !brief,
                    project,
                    !noPseudo,
                    delayedBranching,
                    warningsAreErrors,
                    startAtMain,
                    instructionCount,
                    instructionStatistics,
                    selfModifyingCode,
                    maxSteps,
                    assembleErrorExitCode,
                    simulateErrorExitCode,
                    memoryConfiguration,
                    normalizedRegisters,
                    normalizedMemoryRanges,
                    files,
                    dumps,
                    programArguments);
        }
    }

    static final class IntegerDecodeConverter implements ITypeConverter<Integer> {
        public Integer convert(String value) {
            return Integer.decode(value);
        }
    }

    static final class DisplayFormatConverter implements ITypeConverter<Integer> {
        public Integer convert(String value) {
            if ("dec".equalsIgnoreCase(value)) {
                return DECIMAL;
            }
            if ("hex".equalsIgnoreCase(value)) {
                return HEXADECIMAL;
            }
            if ("ascii".equalsIgnoreCase(value)) {
                return ASCII;
            }
            throw new IllegalArgumentException("Expected one of: hex, dec, ascii");
        }
    }

    static final class SplitArguments {
        final List<String> marsArguments;
        final List<String> programArguments;

        SplitArguments(List<String> marsArguments, List<String> programArguments) {
            this.marsArguments = marsArguments;
            this.programArguments = programArguments;
        }
    }

    static final class ParseResult {
        private final PrintStream output;
        private final int exitCode;
        private final ParsedCommand command;

        private ParseResult(PrintStream output, int exitCode, ParsedCommand command) {
            this.output = output;
            this.exitCode = exitCode;
            this.command = command;
        }

        static ParseResult runnable(PrintStream output, ParsedCommand command) {
            return new ParseResult(output, 0, command);
        }

        static ParseResult notRunnable(PrintStream output, int exitCode) {
            return new ParseResult(output, exitCode, null);
        }

        PrintStream output() {
            return output;
        }

        int exitCode() {
            return exitCode;
        }

        boolean shouldRun() {
            return command != null;
        }

        ParsedCommand command() {
            return command;
        }
    }

    static final class ParsedCommand {
        final boolean simulate;
        final boolean debug;
        final int displayFormat;
        final boolean verbose;
        final boolean assembleProject;
        final boolean pseudo;
        final boolean delayedBranching;
        final boolean warningsAreErrors;
        final boolean startAtMain;
        final boolean countInstructions;
        final boolean instructionStatistics;
        final boolean selfModifyingCode;
        final int maxSteps;
        final int assembleErrorExitCode;
        final int simulateErrorExitCode;
        final String memoryConfiguration;
        final List<String> registers;
        final List<String[]> memoryRanges;
        final List<String> filenames;
        final List<String[]> dumpTriples;
        final List<String> programArguments;

        ParsedCommand(
                boolean simulate,
                boolean debug,
                int displayFormat,
                boolean verbose,
                boolean assembleProject,
                boolean pseudo,
                boolean delayedBranching,
                boolean warningsAreErrors,
                boolean startAtMain,
                boolean countInstructions,
                boolean instructionStatistics,
                boolean selfModifyingCode,
                int maxSteps,
                int assembleErrorExitCode,
                int simulateErrorExitCode,
                String memoryConfiguration,
                List<String> registers,
                List<String[]> memoryRanges,
                List<String> filenames,
                List<String[]> dumpTriples,
                List<String> programArguments) {
            this.simulate = simulate;
            this.debug = debug;
            this.displayFormat = displayFormat;
            this.verbose = verbose;
            this.assembleProject = assembleProject;
            this.pseudo = pseudo;
            this.delayedBranching = delayedBranching;
            this.warningsAreErrors = warningsAreErrors;
            this.startAtMain = startAtMain;
            this.countInstructions = countInstructions;
            this.instructionStatistics = instructionStatistics;
            this.selfModifyingCode = selfModifyingCode;
            this.maxSteps = maxSteps;
            this.assembleErrorExitCode = assembleErrorExitCode;
            this.simulateErrorExitCode = simulateErrorExitCode;
            this.memoryConfiguration = memoryConfiguration;
            this.registers = Collections.unmodifiableList(new ArrayList<>(registers));
            this.memoryRanges = Collections.unmodifiableList(copyRanges(memoryRanges));
            this.filenames = Collections.unmodifiableList(new ArrayList<>(filenames));
            this.dumpTriples = Collections.unmodifiableList(copyRanges(dumpTriples));
            this.programArguments = Collections.unmodifiableList(new ArrayList<>(programArguments));
        }

        private static List<String[]> copyRanges(List<String[]> ranges) {
            List<String[]> copy = new ArrayList<>();
            for (String[] range : ranges) {
                copy.add(Arrays.copyOf(range, range.length));
            }
            return copy;
        }
    }
}
