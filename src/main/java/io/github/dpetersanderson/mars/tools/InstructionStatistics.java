/*
Copyright (c) 2009,  Ingo Kofler, ITEC, Klagenfurt University, Austria

Developed by Ingo Kofler (ingo.kofler@itec.uni-klu.ac.at)
Based on the Instruction Counter tool by Felipe Lessa (felipe.lessa@gmail.com)

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
package io.github.dpetersanderson.mars.tools;

import io.github.dpetersanderson.mars.InstructionCategory;
import io.github.dpetersanderson.mars.ProgramStatement;
import io.github.dpetersanderson.mars.mips.hardware.AccessNotice;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.Memory;
import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessNotice;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Observable;
import javax.swing.*;

/**
 *
 * A MARS tool for obtaining instruction statistics by instruction category.
 * <p>
 * The code of this tools is initially based on the Instruction counter tool by Felipe Lassa.
 *
 * @author Ingo Kofler {@literal <ingo.kofler@itec.uni-klu.ac.at>}
 *
 */
// @SuppressWarnings("serial")
public class InstructionStatistics extends AbstractMarsToolAndApplication {
    private final EnumMap<InstructionCategory, Integer> instructionCounts = new EnumMap<>(InstructionCategory.class);

    /** name of the tool */
    private static final String NAME = "Instruction Statistics";

    /** version and author information of the tool */
    private static final String VERSION = "Version 1.1 (Ingo Kofler, saitewasreset)";

    /** heading of the tool */
    private static final String HEADING = "";

    private JTextField totalInstructionCounter;
    private JTextField finalCyclesCounter;
    private final EnumMap<InstructionCategory, JTextField> instructionCounters =
            new EnumMap<>(InstructionCategory.class);
    private final EnumMap<InstructionCategory, JTextField> instructionWeightedCyclesCounters =
            new EnumMap<>(InstructionCategory.class);
    private final EnumMap<InstructionCategory, JProgressBar> instructionProgressBars =
            new EnumMap<>(InstructionCategory.class);
    private final EnumMap<InstructionCategory, JProgressBar> instructionWeightedCyclesProgressBars =
            new EnumMap<>(InstructionCategory.class);

    // From Felipe Lessa's instruction counter.  Prevent double-counting of instructions
    // which happens because 2 read events are generated.
    /**
     * The last address we saw. We ignore it because the only way for a
     * program to execute twice the same instruction is to enter an infinite
     * loop, which is not insteresting in the POV of counting instructions.
     */
    protected int lastAddress = -1;

    /**
     * Simple constructor, likely used to run a stand-alone enhanced instruction counter.
     * @param title String containing title for title bar
     * @param heading String containing text for heading shown in upper part of window.
     */
    public InstructionStatistics(String title, String heading) {
        super(title, heading);
    }

    /**
     * Simple construction, likely used by the MARS Tools menu mechanism.
     */
    public InstructionStatistics() {
        super(InstructionStatistics.NAME + ", " + InstructionStatistics.VERSION, InstructionStatistics.HEADING);
    }

    /**
     * returns the name of the tool
     *
     * @return the tools's name
     */
    public String getName() {
        return NAME;
    }

    /**
     * creates the display area for the tool as required by the API
     *
     * @return a panel that holds the GUI of the tool
     */
    protected JComponent buildMainDisplayArea() {
        // Create GUI elements for the tool
        JPanel panel = new JPanel(new GridBagLayout());

        totalInstructionCounter = new JTextField("0", 10);
        totalInstructionCounter.setEditable(false);
        finalCyclesCounter = new JTextField("0", 10);
        finalCyclesCounter.setEditable(false);

        for (InstructionCategory category : InstructionCategory.values()) {
            JTextField categoryCounterTextField = new JTextField("0", 10);
            categoryCounterTextField.setEditable(false);
            JProgressBar categoryProgressBar = new JProgressBar(JProgressBar.HORIZONTAL);
            categoryProgressBar.setStringPainted(true);

            JTextField categoryWeightedCyclesCounterTextField = new JTextField("0", 10);
            categoryWeightedCyclesCounterTextField.setEditable(false);
            JProgressBar categoryWeightedCyclesProgressBar = new JProgressBar(JProgressBar.HORIZONTAL);
            categoryWeightedCyclesProgressBar.setStringPainted(true);

            instructionCounters.put(category, categoryCounterTextField);
            instructionWeightedCyclesCounters.put(category, categoryWeightedCyclesCounterTextField);
            instructionProgressBars.put(category, categoryProgressBar);
            instructionWeightedCyclesProgressBars.put(category, categoryWeightedCyclesProgressBar);
        }

        List<String> finalCyclesParts = new ArrayList<>();

        for (InstructionCategory category : InstructionCategory.values()) {
            finalCyclesParts.add(String.format("%s * %d", category.getName(), category.getWeight()));
        }
        String finalCyclesFormula = String.format("FinalCycle = %s", String.join(" + ", finalCyclesParts));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.gridheight = c.gridwidth = 1;

        // create the label and text field for the total instruction counter
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 6;
        c.insets = new Insets(0, 0, 10, 0);
        panel.add(new JLabel(finalCyclesFormula), c);

        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 17, 0);
        panel.add(new JLabel("Total: "), c);
        c.gridx = 3;
        panel.add(totalInstructionCounter, c);
        c.gridx = 4;
        panel.add(new JLabel("Final Cycles: "), c);
        c.gridx = 5;
        panel.add(finalCyclesCounter, c);

        c.insets = new Insets(3, 3, 3, 3);

        JLabel instructionTypeLabel = new JLabel("Type");
        JLabel instructionCountLabel = new JLabel("Instruction Count");
        JLabel instructionWeightedCyclesLabel = new JLabel("Weighted Cycles");

        c.gridy++;
        c.gridx = 2;
        panel.add(instructionTypeLabel, c);
        c.gridx = 3;
        panel.add(instructionCountLabel, c);
        c.gridx = 5;
        panel.add(instructionWeightedCyclesLabel, c);

        c.gridx = 2;
        c.gridy++;
        // create label, text field and progress bar for each category
        for (InstructionCategory category : InstructionCategory.values()) {
            c.gridy++;
            c.gridx = 2;
            panel.add(new JLabel(category.getName() + ":   "), c);
            c.gridx = 3;
            panel.add(instructionCounters.get(category), c);
            c.gridx = 4;
            panel.add(instructionProgressBars.get(category), c);
            c.gridx = 5;
            panel.add(instructionWeightedCyclesCounters.get(category), c);
            c.gridx = 6;
            panel.add(instructionWeightedCyclesProgressBars.get(category), c);
        }

        return panel;
    }

    /**
     * registers the tool as observer for the text segment of the MIPS program
     *
     */
    protected void addAsObserver() {
        addAsObserver(Memory.textBaseAddress, Memory.textLimitAddress);
    }

    /**
     * decodes the instruction and determines the category of the instruction.
     *
     * The instruction is decoded by extracting the operation and function code of the 32-bit instruction.
     * Only the most relevant instructions are decoded and categorized.
     *
     * @param stmt the instruction to decode
     * @return the category of the instruction
     */
    protected InstructionCategory getInstructionCategory(ProgramStatement stmt) {
        int opCode = stmt.getBinaryStatement() >>> (32 - 6);
        int funct = stmt.getBinaryStatement() & 0x1F;

        // R-Type
        if (opCode == 0x00) {
            if (funct == 0x00) return InstructionCategory.OTHER; // sll
            if (0x02 <= funct && funct <= 0x07) return InstructionCategory.OTHER; // srl, sra, sllv, srlv, srav
            if (funct == 0x08 || funct == 0x09) return InstructionCategory.JUMP; // jr, jalr

            if ((funct >= 0x10) && (funct <= 0x13)) return InstructionCategory.OTHER; // mfhi, mthi, mflo, mtlo
            if ((funct >= 0x18) && (funct <= 0x19)) return InstructionCategory.MULT; // mult,multu
            if ((funct >= 0x1A) && (funct <= 0x1B)) return InstructionCategory.DIV; // div, divu
            // add, addu, sub, subu
            // and, or, xor, nor
            // slt, sltu
            return InstructionCategory.OTHER;
        }
        if (opCode == 0x01) {
            if (funct <= 0x07) return InstructionCategory.BRANCH; // bltz, bgez, bltzl, bgezl
            if (0x10 <= funct && funct <= 0x13) return InstructionCategory.BRANCH; // bltzal, bgezal, bltzall, bgczall
            return InstructionCategory.OTHER;
        }
        if (opCode == 0x02 || opCode == 0x03) return InstructionCategory.JUMP; // j, jal
        if (opCode <= 0x07) return InstructionCategory.BRANCH; // beq, bne, blez, bgtz
        if (opCode <= 0x0F) return InstructionCategory.OTHER; // addi, addiu, slti, sltiu, andi, ori, xori, lui
        if (0x14 <= opCode && opCode <= 0x17) return InstructionCategory.BRANCH; // beql, bnel, blezl, bgtzl
        if (0x20 <= opCode && opCode <= 0x26) return InstructionCategory.MEM; // lb, lh, lwl, lw, lbu, lhu, lwr
        if (0x28 <= opCode && opCode <= 0x2E) return InstructionCategory.MEM; // sb, sh, swl, sw, swr

        return InstructionCategory.OTHER;
    }

    /**
     * method that is called each time the MIPS simulator accesses the text segment.
     * Before an instruction is executed by the simulator, the instruction is fetched from the program memory.
     * This memory access is observed and the corresponding instruction is decoded and categorized by the tool.
     * According to the category the counter values are increased and the display gets updated.
     *
     * @param resource the observed resource
     * @param notice signals the type of access (memory, register etc.)
     */
    protected void processMIPSUpdate(Observable resource, AccessNotice notice) {

        if (!notice.accessIsFromMIPS()) return;

        // check for a read access in the text segment
        if (notice.getAccessType() == AccessNotice.READ && notice instanceof MemoryAccessNotice memAccNotice) {
            // The next three statments are from Felipe Lessa's instruction counter.  Prevents double-counting.
            int a = memAccNotice.getAddress();
            if (a == lastAddress) return;
            lastAddress = a;

            try {
                // access the statement in the text segment without notifying other tools etc.
                ProgramStatement stmt = Memory.getInstance().getStatementNoNotify(memAccNotice.getAddress());

                // necessary to handle possible null pointers at the end of the program
                // (e.g., if the simulator tries to execute the next instruction after the last instruction in the text
                // segment)
                if (stmt != null) {
                    InstructionCategory category = getInstructionCategory(stmt);

                    instructionCounts.compute(category, (k, v) -> v == null ? 1 : v + 1);

                    updateDisplay();
                }
            } catch (AddressErrorException e) {
                // silently ignore these exceptions
            }
        }
    }

    /**
     * performs initialization tasks of the counters before the GUI is created.
     *
     */
    protected void initializePreGUI() {
        lastAddress = -1; // from Felipe Lessa's instruction counter tool
    }

    /**
     * resets the counter values of the tool and updates the display.
     *
     */
    protected void reset() {
        lastAddress = -1; // from Felipe Lessa's instruction counter tool
        instructionCounts.clear();
        updateDisplay();
    }

    /**
     * updates the text fields and progress bars according to the current counter values.
     *
     */
    protected void updateDisplay() {
        int totalInstructionCount = 0;
        int finalCycles = 0;

        for (InstructionCategory category : InstructionCategory.values()) {
            Integer categoryInstructionCount = instructionCounts.get(category);

            if (categoryInstructionCount == null) {
                categoryInstructionCount = 0;
            }

            totalInstructionCount += categoryInstructionCount;
            finalCycles += categoryInstructionCount * category.getWeight();
        }
        totalInstructionCounter.setText(Integer.toString(totalInstructionCount));
        finalCyclesCounter.setText(Integer.toString(finalCycles));

        for (var entry : instructionCounts.entrySet()) {
            InstructionCategory category = entry.getKey();
            Integer categoryInstructionCount = entry.getValue();

            if (categoryInstructionCount == null) {
                categoryInstructionCount = 0;
            }

            int categoryWeightedCyclesCount = categoryInstructionCount * category.getWeight();

            JTextField categoryCounterTextField = instructionCounters.get(category);
            JTextField categoryWeightedCyclesCounterTextField = instructionWeightedCyclesCounters.get(category);
            JProgressBar categoryProgressBar = instructionProgressBars.get(category);
            JProgressBar categoryWeightedCyclesProgressBar = instructionWeightedCyclesProgressBars.get(category);

            categoryCounterTextField.setText(categoryInstructionCount.toString());
            categoryWeightedCyclesCounterTextField.setText(Integer.toString(categoryWeightedCyclesCount));
            categoryProgressBar.setMaximum(totalInstructionCount);
            categoryProgressBar.setValue(categoryInstructionCount);
            categoryWeightedCyclesProgressBar.setMaximum(finalCycles);
            categoryWeightedCyclesProgressBar.setValue(categoryWeightedCyclesCount);
        }
    }
}
