package io.github.dpetersanderson.mars;

import io.github.dpetersanderson.mars.mips.dump.DumpFormat;
import io.github.dpetersanderson.mars.mips.dump.DumpFormatLoader;
import io.github.dpetersanderson.mars.mips.hardware.*;
import io.github.dpetersanderson.mars.simulator.ProgramArgumentList;
import io.github.dpetersanderson.mars.util.Binary;
import io.github.dpetersanderson.mars.util.FilenameFinder;
import io.github.dpetersanderson.mars.util.MemoryDump;
import io.github.dpetersanderson.mars.venus.VenusUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import javax.swing.*;

/*
Copyright (c) 2003-2012,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */

/**
 * Launch the Mars application
 *
 * @author Pete Sanderson
 * @version December 2009
 **/
public class MarsLaunch {

    /**
     * Main accepts modern UNIX-style command line arguments.
     *
     * <p>Usage: Mars [options] filename [additional filenames] [-- program arguments]
     *
     * <p>Run {@code Mars --help} for the complete option list. If no arguments are provided, MARS starts the
     * graphical IDE.
     **/
    private boolean simulate;

    private int displayFormat;
    private boolean verbose; // display register name or address along with contents
    private boolean assembleProject; // assemble only the given file or all files in its directory
    private boolean pseudo; // pseudo instructions allowed in source code or not.
    private boolean delayedBranching; // MIPS delayed branching is enabled.
    private boolean warningsAreErrors; // Whether assembler warnings should be considered errors.
    private boolean startAtMain; // Whether to start execution at statement labeled 'main'
    private boolean countInstructions; // Whether to count and report number of instructions executed
    private boolean selfModifyingCode; // Whether to allow self-modifying code (e.g. write to text segment)
    private static final String rangeSeparator = "-";
    private static final int splashDuration = 2000; // time in MS to show splash screen
    private static final int memoryWordsPerLine = 4; // display 4 memory words, tab separated, per line
    private static final int DECIMAL = MarsCommandLine.DECIMAL; // memory and register display format
    private static final int HEXADECIMAL = MarsCommandLine.HEXADECIMAL; // memory and register display format
    private static final int ASCII = MarsCommandLine.ASCII; // memory and register display format
    private ArrayList registerDisplayList;
    private ArrayList memoryDisplayList;
    private ArrayList filenameList;
    private MIPSprogram code;
    private int maxSteps;
    private int instructionCount;
    private PrintStream out; // stream for display of command line output
    private ArrayList dumpTriples = null; // each element holds 3 arguments for dump option
    private ArrayList programArgumentList; // optional program args for MIPS program (becomes argc, argv)
    private int assembleErrorExitCode; // MARS command exit code to return if assemble error occurs
    private int simulateErrorExitCode; // MARS command exit code to return if simulation error occurs

    public MarsLaunch(String[] args) {
        boolean gui = (args.length == 0);
        Globals.initialize(gui);
        if (gui) {
            launchIDE();
        } else { // running from command line.
            // assure command mode works in headless environment (generates exception if not)
            System.setProperty("java.awt.headless", "true");
            simulate = true;
            displayFormat = HEXADECIMAL;
            verbose = true;
            assembleProject = false;
            pseudo = true;
            delayedBranching = false;
            warningsAreErrors = false;
            startAtMain = false;
            countInstructions = false;
            selfModifyingCode = false;
            instructionCount = 0;
            assembleErrorExitCode = 0;
            simulateErrorExitCode = 0;
            registerDisplayList = new ArrayList();
            memoryDisplayList = new ArrayList();
            filenameList = new ArrayList();
            MemoryConfigurations.setCurrentConfiguration(MemoryConfigurations.getDefaultConfiguration());
            // do NOT use Globals.program for command line MARS -- it triggers 'backstep' log.
            code = new MIPSprogram();
            maxSteps = -1;
            out = System.out;
            MarsCommandLine.ParseResult parseResult = MarsCommandLine.parse(args);
            out = parseResult.output();
            if (parseResult.shouldRun()) {
                applyCommandOptions(parseResult.command());
                if (runCommand()) {
                    displayMiscellaneousPostMortem();
                    displayRegistersPostMortem();
                    displayMemoryPostMortem();
                }
                dumpSegments();
            } else {
                Globals.exitCode = parseResult.exitCode();
            }
            System.exit(Globals.exitCode);
        }
    }

    /////////////////////////////////////////////////////////////
    // Perform any specified dump operations.  See "dump" option.
    //

    private void dumpSegments() {

        if (dumpTriples == null) return;

        for (int i = 0; i < dumpTriples.size(); i++) {
            String[] triple = (String[]) dumpTriples.get(i);
            File file = new File(triple[2]);
            Integer[] segInfo = MemoryDump.getSegmentBounds(triple[0]);
            // If not segment name, see if it is address range instead.  DPS 14-July-2008
            if (segInfo == null) {
                try {
                    String[] memoryRange = checkMemoryAddressRange(triple[0]);
                    segInfo = new Integer[2];
                    segInfo[0] = Binary.stringToInt(memoryRange[0]); // low end of range
                    segInfo[1] = Binary.stringToInt(memoryRange[1]); // high end of range
                } catch (NumberFormatException nfe) {
                    segInfo = null;
                } catch (NullPointerException npe) {
                    segInfo = null;
                }
            }
            if (segInfo == null) {
                out.println("Error while attempting to save dump, segment/address-range " + triple[0] + " is invalid!");
                continue;
            }
            DumpFormatLoader loader = new DumpFormatLoader();
            List<DumpFormat> dumpFormats = loader.loadDumpFormats();
            DumpFormat format = DumpFormatLoader.findDumpFormatGivenCommandDescriptor(dumpFormats, triple[1]);
            if (format == null) {
                out.println("Error while attempting to save dump, format " + triple[1] + " was not found!");
                continue;
            }
            try {
                int highAddress = Globals.memory.getAddressOfFirstNull(segInfo[0].intValue(), segInfo[1].intValue())
                        - Memory.WORD_LENGTH_BYTES;
                if (highAddress < segInfo[0].intValue()) {
                    out.println("This segment has not been written to, there is nothing to dump.");
                    continue;
                }
                format.dumpMemoryRange(file, segInfo[0].intValue(), highAddress);
            } catch (FileNotFoundException e) {
                out.println("Error while attempting to save dump, file " + file + " was not found!");
                continue;
            } catch (AddressErrorException e) {
                out.println("Error while attempting to save dump, file " + file + "!  Could not access address: "
                        + e.getAddress() + "!");
                continue;
            } catch (IOException e) {
                out.println("Error while attempting to save dump, file " + file + "!  Disk IO failed!");
                continue;
            }
        }
    }

    /////////////////////////////////////////////////////////////////
    // There are no command arguments, so run in interactive mode by
    // launching the GUI-fronted integrated development environment.

    private void launchIDE() {
        // System.setProperty("apple.laf.useScreenMenuBar", "true"); // Puts MARS menu on Mac OS menu bar
        new MarsSplashScreen(splashDuration).showSplash();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Turn off metal's use of bold fonts
                // UIManager.put("swing.boldMetal", Boolean.FALSE);
                new VenusUI("MARS " + Globals.version);
            }
        });
        return;
    }

    private void applyCommandOptions(MarsCommandLine.ParsedCommand command) {
        simulate = command.simulate;
        displayFormat = command.displayFormat;
        verbose = command.verbose;
        assembleProject = command.assembleProject;
        pseudo = command.pseudo;
        delayedBranching = command.delayedBranching;
        warningsAreErrors = command.warningsAreErrors;
        startAtMain = command.startAtMain;
        countInstructions = command.countInstructions;
        selfModifyingCode = command.selfModifyingCode;
        maxSteps = command.maxSteps;
        assembleErrorExitCode = command.assembleErrorExitCode;
        simulateErrorExitCode = command.simulateErrorExitCode;
        Globals.debug = command.debug;
        if (command.memoryConfiguration != null) {
            MemoryConfigurations.setCurrentConfiguration(
                    MemoryConfigurations.getConfigurationByName(command.memoryConfiguration));
        }
        registerDisplayList.addAll(command.registers);
        for (String[] memoryRange : command.memoryRanges) {
            memoryDisplayList.add(memoryRange[0]);
            memoryDisplayList.add(memoryRange[1]);
        }
        filenameList.addAll(command.filenames);
        if (!command.dumpTriples.isEmpty()) {
            dumpTriples = new ArrayList(command.dumpTriples);
        }
        if (!command.programArguments.isEmpty()) {
            programArgumentList = new ArrayList(command.programArguments);
        }
    }

    //////////////////////////////////////////////////////////////////////
    // Carry out the mars command: assemble then optionally run
    // Returns false if no simulation (run) occurs, true otherwise.

    private boolean runCommand() {
        boolean programRan = false;
        if (filenameList.size() == 0) {
            return programRan;
        }
        try {
            Globals.getSettings().setBooleanSettingNonPersistent(Settings.DELAYED_BRANCHING_ENABLED, delayedBranching);
            Globals.getSettings()
                    .setBooleanSettingNonPersistent(Settings.SELF_MODIFYING_CODE_ENABLED, selfModifyingCode);
            File mainFile = new File((String) filenameList.get(0)).getAbsoluteFile(); // First file is "main" file
            ArrayList filesToAssemble;
            if (assembleProject) {
                filesToAssemble = FilenameFinder.getFilenameList(mainFile.getParent(), Globals.fileExtensions);
                if (filenameList.size() > 1) {
                    // Using "p" project option PLUS listing more than one filename on command line.
                    // Add the additional files, avoiding duplicates.
                    filenameList.remove(0); // first one has already been processed
                    ArrayList moreFilesToAssemble =
                            FilenameFinder.getFilenameList(filenameList, FilenameFinder.MATCH_ALL_EXTENSIONS);
                    // Remove any duplicates then merge the two lists.
                    for (int index2 = 0; index2 < moreFilesToAssemble.size(); index2++) {
                        for (int index1 = 0; index1 < filesToAssemble.size(); index1++) {
                            if (filesToAssemble.get(index1).equals(moreFilesToAssemble.get(index2))) {
                                moreFilesToAssemble.remove(index2);
                                index2--; // adjust for left shift in moreFilesToAssemble...
                                break; // break out of inner loop...
                            }
                        }
                    }
                    filesToAssemble.addAll(moreFilesToAssemble);
                }
            } else {
                filesToAssemble = FilenameFinder.getFilenameList(filenameList, FilenameFinder.MATCH_ALL_EXTENSIONS);
            }
            if (Globals.debug) {
                out.println("--------  TOKENIZING BEGINS  -----------");
            }
            ArrayList MIPSprogramsToAssemble =
                    code.prepareFilesForAssembly(filesToAssemble, mainFile.getAbsolutePath(), null);
            if (Globals.debug) {
                out.println("--------  ASSEMBLY BEGINS  -----------");
            }
            // Added logic to check for warnings and print if any. DPS 11/28/06
            ErrorList warnings = code.assemble(MIPSprogramsToAssemble, pseudo, warningsAreErrors);
            if (warnings != null && warnings.warningsOccurred()) {
                out.println(warnings.generateWarningReport());
            }
            RegisterFile.initializeProgramCounter(startAtMain); // DPS 3/9/09
            if (simulate) {
                // store program args (if any) in MIPS memory
                new ProgramArgumentList(programArgumentList).storeProgramArguments();
                // establish observer if specified
                establishObserver();
                if (Globals.debug) {
                    out.println("--------  SIMULATION BEGINS  -----------");
                }
                programRan = true;
                boolean done = code.simulate(maxSteps);
                if (!done) {
                    out.println("\nProgram terminated when maximum step limit " + maxSteps + " reached.");
                }
            }
            if (Globals.debug) {
                out.println("\n--------  ALL PROCESSING COMPLETE  -----------");
            }
        } catch (ProcessingException e) {
            Globals.exitCode = (programRan) ? simulateErrorExitCode : assembleErrorExitCode;
            out.println(e.errors().generateErrorAndWarningReport());
            out.println("Processing terminated due to errors.");
        }
        return programRan;
    }

    //////////////////////////////////////////////////////////////////////
    // Check for memory address subrange.  Has to be two integers separated
    // by "-"; no embedded spaces.  e.g. 0x00400000-0x00400010
    // If number is not multiple of 4, will be rounded up to next higher.

    private String[] checkMemoryAddressRange(String arg) throws NumberFormatException {
        String[] memoryRange = null;
        if (arg.indexOf(rangeSeparator) > 0 && arg.indexOf(rangeSeparator) < arg.length() - 1) {
            // assume correct format, two numbers separated by -, no embedded spaces.
            // If that doesn't work it is invalid.
            memoryRange = new String[2];
            memoryRange[0] = arg.substring(0, arg.indexOf(rangeSeparator));
            memoryRange[1] = arg.substring(arg.indexOf(rangeSeparator) + 1);
            // NOTE: I will use homegrown decoder, because Integer.decode will throw
            // exception on address higher than 0x7FFFFFFF (e.g. sign bit is 1).
            if (Binary.stringToInt(memoryRange[0]) > Binary.stringToInt(memoryRange[1])
                    || !Memory.wordAligned(Binary.stringToInt(memoryRange[0]))
                    || !Memory.wordAligned(Binary.stringToInt(memoryRange[1]))) {
                throw new NumberFormatException();
            }
        }
        return memoryRange;
    }

    /////////////////////////////////////////////////////////////////
    // Required for counting instructions executed, if that option is specified.
    // DPS 19 July 2012
    private void establishObserver() {
        if (countInstructions) {
            Observer instructionCounter = new Observer() {
                private int lastAddress = 0;

                public void update(Observable o, Object obj) {
                    if (obj instanceof AccessNotice) {
                        AccessNotice notice = (AccessNotice) obj;
                        if (!notice.accessIsFromMIPS()) return;
                        if (notice.getAccessType() != AccessNotice.READ) return;
                        MemoryAccessNotice m = (MemoryAccessNotice) notice;
                        int a = m.getAddress();
                        if (a == lastAddress) return;
                        lastAddress = a;
                        instructionCount++;
                    }
                }
            };
            try {
                Globals.memory.addObserver(instructionCounter, Memory.textBaseAddress, Memory.textLimitAddress);
            } catch (AddressErrorException aee) {
                out.println("Internal error: MarsLaunch uses incorrect text segment address for instruction observer");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    // Displays any specified runtime properties. Initially just instruction count
    // DPS 19 July 2012
    private void displayMiscellaneousPostMortem() {
        if (countInstructions) {
            out.println("\n" + instructionCount);
        }
    }

    //////////////////////////////////////////////////////////////////////
    // Displays requested register or registers

    private void displayRegistersPostMortem() {
        int value; // handy local to use throughout the next couple loops
        String strValue;
        // Display requested register contents
        out.println();
        Iterator regIter = registerDisplayList.iterator();
        while (regIter.hasNext()) {
            String reg = regIter.next().toString();
            if (RegisterFile.getUserRegister(reg) != null) {
                // integer register
                if (verbose) out.print(reg + "\t");
                value = RegisterFile.getUserRegister(reg).getValue();
                out.println(formatIntForDisplay(value));
            } else {
                // floating point register
                float fvalue = Coprocessor1.getFloatFromRegister(reg);
                int ivalue = Coprocessor1.getIntFromRegister(reg);
                double dvalue = Double.NaN;
                long lvalue = 0;
                boolean hasDouble = false;
                try {
                    dvalue = Coprocessor1.getDoubleFromRegisterPair(reg);
                    lvalue = Coprocessor1.getLongFromRegisterPair(reg);
                    hasDouble = true;
                } catch (InvalidRegisterAccessException irae) {
                }
                if (verbose) {
                    out.print(reg + "\t");
                }
                if (displayFormat == HEXADECIMAL) {
                    // display float (and double, if applicable) in hex
                    out.print(Binary.binaryStringToHexString(Binary.intToBinaryString(ivalue)));
                    if (hasDouble) {
                        out.println("\t" + Binary.binaryStringToHexString(Binary.longToBinaryString(lvalue)));
                    } else {
                        out.println("");
                    }
                } else if (displayFormat == DECIMAL) {
                    // display float (and double, if applicable) in decimal
                    out.print(fvalue);
                    if (hasDouble) {
                        out.println("\t" + dvalue);
                    } else {
                        out.println("");
                    }
                } else { // displayFormat == ASCII
                    out.print(Binary.intToAscii(ivalue));
                    if (hasDouble) {
                        out.println("\t" + Binary.intToAscii(Binary.highOrderLongToInt(lvalue))
                                + Binary.intToAscii(Binary.lowOrderLongToInt(lvalue)));
                    } else {
                        out.println("");
                    }
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    // Formats int value for display: decimal, hex, ascii
    private String formatIntForDisplay(int value) {
        String strValue;
        switch (displayFormat) {
            case DECIMAL:
                strValue = "" + value;
                break;
            case HEXADECIMAL:
                strValue = Binary.intToHexString(value);
                break;
            case ASCII:
                strValue = Binary.intToAscii(value);
                break;
            default:
                strValue = Binary.intToHexString(value);
        }
        return strValue;
    }

    //////////////////////////////////////////////////////////////////////
    // Displays requested memory range or ranges

    private void displayMemoryPostMortem() {
        int value;
        // Display requested memory range contents
        Iterator memIter = memoryDisplayList.iterator();
        int addressStart = 0, addressEnd = 0;
        while (memIter.hasNext()) {
            try { // This will succeed; error would have been caught during command arg parse
                addressStart = Binary.stringToInt(memIter.next().toString());
                addressEnd = Binary.stringToInt(memIter.next().toString());
            } catch (NumberFormatException nfe) {
            }
            int valuesDisplayed = 0;
            for (int addr = addressStart; addr <= addressEnd; addr += Memory.WORD_LENGTH_BYTES) {
                if (addr < 0 && addressEnd > 0) break; // happens only if addressEnd is 0x7ffffffc
                if (valuesDisplayed % memoryWordsPerLine == 0) {
                    out.print((valuesDisplayed > 0) ? "\n" : "");
                    if (verbose) {
                        out.print("Mem[" + Binary.intToHexString(addr) + "]\t");
                    }
                }
                try {
                    // Allow display of binary text segment (machine code) DPS 14-July-2008
                    if (Memory.inTextSegment(addr) || Memory.inKernelTextSegment(addr)) {
                        Integer iValue = Globals.memory.getRawWordOrNull(addr);
                        value = (iValue == null) ? 0 : iValue.intValue();
                    } else {
                        value = Globals.memory.getWord(addr);
                    }
                    out.print(formatIntForDisplay(value) + "\t");
                } catch (AddressErrorException aee) {
                    out.print("Invalid address: " + addr + "\t");
                }
                valuesDisplayed++;
            }
            out.println();
        }
    }
}
