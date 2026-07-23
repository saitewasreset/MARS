package io.github.dpetersanderson.mars.mips.instructions;

import io.github.dpetersanderson.mars.mips.instructions.impl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

/*
Copyright (c) 2003-2013,  Pete Sanderson and Kenneth Vollmar

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
 * The list of Instruction objects, each of which represents a MIPS instruction.
 * The instruction may either be basic (translates into binary machine code) or
 * extended (translates into sequence of one or more basic instructions).
 *
 * @author Pete Sanderson and Ken Vollmar
 * @version August 2003-5
 */
public class InstructionSet {
    private final ArrayList<Instruction> instructionList;
    private ArrayList<MatchMap> opcodeMatchMaps;
    /**
     * Creates a new InstructionSet object.
     */
    public InstructionSet() {
        instructionList = new ArrayList<>();
    }
    /**
     * Retrieve the current instruction set.
     */
    public ArrayList<Instruction> getInstructionList() {
        return instructionList;
    }
    /**
     * Adds all instructions to the set.  A given extended instruction may have
     * more than one Instruction object, depending on how many formats it can have.
     * @see Instruction
     * @see BasicInstruction
     * @see ExtendedInstruction
     */
    public void populate() {
        /* Here is where the parade begins.  Every instruction is added to the set here.*/

        // ////////////////////////////////////   BASIC INSTRUCTIONS START HERE ////////////////////////////////

        instructionList.add(new NopInstruction());
        instructionList.add(new AddInstruction());
        instructionList.add(new SubInstruction());
        instructionList.add(new AddiInstruction());
        instructionList.add(new AdduInstruction());
        instructionList.add(new SubuInstruction());
        instructionList.add(new AddiuInstruction());
        instructionList.add(new MultInstruction());
        instructionList.add(new MultuInstruction());
        instructionList.add(new MulInstruction());
        instructionList.add(new MaddInstruction());
        instructionList.add(new MadduInstruction());
        instructionList.add(new MsubInstruction());
        instructionList.add(new MsubuInstruction());
        instructionList.add(new DivInstruction());
        instructionList.add(new DivuInstruction());
        instructionList.add(new MfhiInstruction());
        instructionList.add(new MfloInstruction());
        instructionList.add(new MthiInstruction());
        instructionList.add(new MtloInstruction());
        instructionList.add(new AndInstruction());
        instructionList.add(new OrInstruction());
        instructionList.add(new AndiInstruction());
        instructionList.add(new OriInstruction());
        instructionList.add(new NorInstruction());
        instructionList.add(new XorInstruction());
        instructionList.add(new XoriInstruction());
        instructionList.add(new SllInstruction());
        instructionList.add(new SllvInstruction());
        instructionList.add(new SrlInstruction());
        instructionList.add(new SraInstruction());
        instructionList.add(new SravInstruction());
        instructionList.add(new SrlvInstruction());
        instructionList.add(new LwInstruction());
        instructionList.add(new LlInstruction());
        instructionList.add(new LwlInstruction());
        instructionList.add(new LwrInstruction());
        instructionList.add(new SwInstruction());
        instructionList.add(new ScInstruction());
        instructionList.add(new SwlInstruction());
        instructionList.add(new SwrInstruction());
        instructionList.add(new LuiInstruction());
        instructionList.add(new BeqInstruction());
        instructionList.add(new BneInstruction());
        instructionList.add(new BgezInstruction());
        instructionList.add(new BgezalInstruction());
        instructionList.add(new BgtzInstruction());
        instructionList.add(new BlezInstruction());
        instructionList.add(new BltzInstruction());
        instructionList.add(new BltzalInstruction());
        instructionList.add(new SltInstruction());
        instructionList.add(new SltuInstruction());
        instructionList.add(new SltiInstruction());
        instructionList.add(new SltiuInstruction());
        instructionList.add(new MovnInstruction());
        instructionList.add(new MovzInstruction());
        instructionList.add(new MovfInstruction());
        instructionList.add(new Movf2Instruction());
        instructionList.add(new MovtInstruction());
        instructionList.add(new Movt2Instruction());
        instructionList.add(new BreakInstruction());
        instructionList.add(new Break2Instruction());
        instructionList.add(new SyscallInstruction());
        instructionList.add(new JInstruction());
        instructionList.add(new JrInstruction());
        instructionList.add(new JalInstruction());
        instructionList.add(new JalrInstruction());
        instructionList.add(new Jalr2Instruction());
        instructionList.add(new LbInstruction());
        instructionList.add(new LhInstruction());
        instructionList.add(new LhuInstruction());
        instructionList.add(new LbuInstruction());
        instructionList.add(new SbInstruction());
        instructionList.add(new ShInstruction());
        instructionList.add(new CloInstruction());
        instructionList.add(new ClzInstruction());
        instructionList.add(new Mfc0Instruction());
        instructionList.add(new Mtc0Instruction());

        /////////////////////// Floating Point Instructions Start Here ////////////////
        instructionList.add(new AddSInstruction());
        instructionList.add(new SubSInstruction());
        instructionList.add(new MulSInstruction());
        instructionList.add(new DivSInstruction());
        instructionList.add(new SqrtSInstruction());
        instructionList.add(new FloorWSInstruction());
        instructionList.add(new CeilWSInstruction());
        instructionList.add(new RoundWSInstruction());
        instructionList.add(new TruncWSInstruction());
        instructionList.add(new AddDInstruction());
        instructionList.add(new SubDInstruction());
        instructionList.add(new MulDInstruction());
        instructionList.add(new DivDInstruction());
        instructionList.add(new SqrtDInstruction());
        instructionList.add(new FloorWDInstruction());
        instructionList.add(new CeilWDInstruction());
        instructionList.add(new RoundWDInstruction());
        instructionList.add(new TruncWDInstruction());
        instructionList.add(new Bc1tInstruction());
        instructionList.add(new Bc1t2Instruction());
        instructionList.add(new Bc1fInstruction());
        instructionList.add(new Bc1f2Instruction());
        instructionList.add(new CEqSInstruction());
        instructionList.add(new CEqS2Instruction());
        instructionList.add(new CLeSInstruction());
        instructionList.add(new CLeS2Instruction());
        instructionList.add(new CLtSInstruction());
        instructionList.add(new CLtS2Instruction());
        instructionList.add(new CEqDInstruction());
        instructionList.add(new CEqD2Instruction());
        instructionList.add(new CLeDInstruction());
        instructionList.add(new CLeD2Instruction());
        instructionList.add(new CLtDInstruction());
        instructionList.add(new CLtD2Instruction());
        instructionList.add(new AbsSInstruction());
        instructionList.add(new AbsDInstruction());
        instructionList.add(new CvtDSInstruction());
        instructionList.add(new CvtDWInstruction());
        instructionList.add(new CvtSDInstruction());
        instructionList.add(new CvtSWInstruction());
        instructionList.add(new CvtWDInstruction());
        instructionList.add(new CvtWSInstruction());
        instructionList.add(new MovDInstruction());
        instructionList.add(new MovfDInstruction());
        instructionList.add(new MovfD2Instruction());
        instructionList.add(new MovtDInstruction());
        instructionList.add(new MovtD2Instruction());
        instructionList.add(new MovnDInstruction());
        instructionList.add(new MovzDInstruction());
        instructionList.add(new MovSInstruction());
        instructionList.add(new MovfSInstruction());
        instructionList.add(new MovfS2Instruction());
        instructionList.add(new MovtSInstruction());
        instructionList.add(new MovtS2Instruction());
        instructionList.add(new MovnSInstruction());
        instructionList.add(new MovzSInstruction());
        instructionList.add(new Mfc1Instruction());
        instructionList.add(new Mtc1Instruction());
        instructionList.add(new NegDInstruction());
        instructionList.add(new NegSInstruction());
        instructionList.add(new Lwc1Instruction());
        instructionList.add(new Ldc1Instruction());
        instructionList.add(new Swc1Instruction());
        instructionList.add(new Sdc1Instruction());
        ////////////////////////////  THE TRAP INSTRUCTIONS & ERET  ////////////////////////////
        instructionList.add(new TeqInstruction());
        instructionList.add(new Teq2Instruction());
        instructionList.add(new TeqiInstruction());
        instructionList.add(new TneInstruction());
        instructionList.add(new TneiInstruction());
        instructionList.add(new TgeInstruction());
        instructionList.add(new TgeuInstruction());
        instructionList.add(new TgeiInstruction());
        instructionList.add(new TgeiuInstruction());
        instructionList.add(new TltInstruction());
        instructionList.add(new TltuInstruction());
        instructionList.add(new TltiInstruction());
        instructionList.add(new TltiuInstruction());
        instructionList.add(new EretInstruction());

        ////////////// READ PSEUDO-INSTRUCTION SPECS FROM DATA FILE AND ADD //////////////////////
        addPseudoInstructions();

        // Initialization step.  Create token list for each instruction example.  This is
        // used by parser to determine user program correct syntax.
        for (Instruction instruction : instructionList) {
            instruction.createExampleTokenList();
        }

        HashMap<Integer, HashMap<Integer, Instruction>> maskMap = new HashMap<>();
        ArrayList<MatchMap> matchMaps = new ArrayList<>();
        for (Instruction rawInstr : instructionList) {
            if (rawInstr instanceof BasicInstruction basic) {
                Integer mask = basic.getOpcodeMask();
                Integer match = basic.getOpcodeMatch();
                HashMap<Integer, Instruction> matchMap = maskMap.get(mask);
                if (matchMap == null) {
                    matchMap = new HashMap<>();
                    maskMap.put(mask, matchMap);
                    matchMaps.add(new MatchMap(mask, matchMap));
                }
                matchMap.put(match, basic);
            }
        }

        Collections.sort(matchMaps);
        this.opcodeMatchMaps = matchMaps;
    }

    public BasicInstruction findByBinaryCode(int binaryInstr) {
        ArrayList<MatchMap> matchMaps = this.opcodeMatchMaps;
        for (MatchMap map : matchMaps) {
            BasicInstruction ret = map.find(binaryInstr);
            if (ret != null) return ret;
        }
        return null;
    }

    /*  METHOD TO ADD PSEUDO-INSTRUCTIONS
     */

    private void addPseudoInstructions() {
        InputStream is = null;
        BufferedReader in = null;

        // leading "/" prevents package name being prepended to filepath.
        is = this.getClass().getResourceAsStream("/PseudoOps.txt");

        if (is == null) {
            throw new RuntimeException("Error: MIPS pseudo-instruction file PseudoOps.txt not found.");
        }

        in = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            String pseudoOp;
            StringBuilder template;
            String firstTemplate;
            String token;
            String description;
            StringTokenizer tokenizer;
            while ((line = in.readLine()) != null) {
                // skip over: comment lines, empty lines, lines starting with blank.
                if (!line.startsWith("#") && !line.startsWith(" ") && !line.isEmpty()) {
                    description = "";
                    tokenizer = new StringTokenizer(line, "\t");
                    pseudoOp = tokenizer.nextToken();
                    template = new StringBuilder();
                    firstTemplate = null;

                    while (tokenizer.hasMoreTokens()) {
                        token = tokenizer.nextToken();
                        if (token.startsWith("#")) {
                            // Optional description must be last token in the line.
                            description = token.substring(1);
                            break;
                        }
                        if (token.startsWith("COMPACT")) {
                            // has second template for Compact (16-bit) memory config -- added DPS 3 Aug 2009
                            firstTemplate = template.toString();
                            template = new StringBuilder();
                            continue;
                        }
                        template.append(token);
                        if (tokenizer.hasMoreTokens()) {
                            template.append("\n");
                        }
                    }
                    ExtendedInstruction inst = (firstTemplate == null)
                            ? new ExtendedInstruction(pseudoOp, template.toString(), description)
                            : new ExtendedInstruction(pseudoOp, firstTemplate, template.toString(), description);
                    instructionList.add(inst);
                    // if (firstTemplate != null) System.out.println("\npseudoOp: "+pseudoOp+"\ndefault
                    // template:\n"+firstTemplate+"\ncompact template:\n"+template);
                }
            }
            in.close();
        } catch (IOException ioe) {
            System.out.println("Internal Error: MIPS pseudo-instructions could not be loaded.");
            System.exit(0);
        } catch (Exception ioe) {
            System.out.println("Error: Invalid MIPS pseudo-instruction specification.");
            System.exit(0);
        }
    }

    /**
     *  Given an operator mnemonic, will return the corresponding Instruction object(s)
     *  from the instruction set.  Uses straight linear search technique.
     *  @param name operator mnemonic (e.g. addi, sw,...)
     *  @return list of corresponding Instruction object(s), or null if not found.
     */
    public ArrayList<Instruction> matchOperator(String name) {
        ArrayList<Instruction> matchingInstructions = new ArrayList<>();
        // Linear search for now....
        for (Instruction instruction : instructionList) {
            if (instruction.getName().equalsIgnoreCase(name)) {
                matchingInstructions.add(instruction);
            }
        }

        if (matchingInstructions.isEmpty()) {
            return null;
        }

        return matchingInstructions;
    }

    /**
     *  Given a string, will return the Instruction object(s) from the instruction
     *  set whose operator mnemonic prefix matches it.  Case-insensitive.  For example
     *  "s" will match "sw", "sh", "sb", etc.  Uses straight linear search technique.
     *  @param name a string
     *  @return list of matching Instruction object(s), or null if none match.
     */
    public ArrayList<Instruction> prefixMatchOperator(String name) {
        ArrayList<Instruction> matchingInstructions = new ArrayList<>();
        // Linear search for now....
        if (name != null) {
            for (Instruction instruction : instructionList) {
                if (instruction.getName().toLowerCase().startsWith(name.toLowerCase())) {
                    matchingInstructions.add(instruction);
                }
            }
        }
        return matchingInstructions;
    }

    private static class MatchMap implements Comparable<MatchMap> {
        private final int mask;
        private final int maskLength; // number of 1 bits in mask
        private final HashMap<Integer, Instruction> matchMap;

        public MatchMap(int mask, HashMap<Integer, Instruction> matchMap) {
            this.mask = mask;
            this.matchMap = matchMap;

            int k = 0;
            int n = mask;
            while (n != 0) {
                k++;
                n &= n - 1;
            }
            this.maskLength = k;
        }

        public boolean equals(Object o) {
            return o instanceof MatchMap && mask == ((MatchMap) o).mask;
        }

        public int compareTo(MatchMap o) {
            int d = o.maskLength - this.maskLength;
            if (d == 0) d = this.mask - o.mask;
            return d;
        }

        public BasicInstruction find(int instr) {
            int match = instr & mask;
            return (BasicInstruction) matchMap.get(match);
        }
    }
}
