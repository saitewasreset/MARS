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
import io.github.dpetersanderson.mars.mips.hardware.AccessNotice;
import io.github.dpetersanderson.mars.mips.hardware.Memory;
import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessNotice;
import io.github.dpetersanderson.mars.util.InstructionStatisticsCounter;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    private InstructionStatisticsCounter instructionStatisticsCounter = new InstructionStatisticsCounter();

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
     * method that is called each time the MIPS simulator accesses the text segment.
     * Before an instruction is executed by the simulator, the instruction is fetched from the program memory.
     * This memory access is observed and the corresponding instruction is decoded and categorized by the tool.
     * According to the category the counter values are increased and the display gets updated.
     *
     * @param notice signals the type of access (memory, register etc.)
     */
    protected void processMIPSUpdate(AccessNotice notice) {

        if (!notice.accessIsFromMIPS()) return;

        // check for a read access in the text segment
        if (notice instanceof MemoryAccessNotice memAccNotice) {
            instructionStatisticsCounter.processMIPSUpdate(memAccNotice);
        }
    }

    /**
     * performs initialization tasks of the counters before the GUI is created.
     *
     */
    protected void initializePreGUI() {}

    /**
     * resets the counter values of the tool and updates the display.
     *
     */
    protected void reset() {
        instructionStatisticsCounter = new InstructionStatisticsCounter();
        updateDisplay();
    }

    /**
     * updates the text fields and progress bars according to the current counter values.
     *
     */
    protected void updateDisplay() {
        BigDecimal totalInstructionCount = BigDecimal.ZERO;
        BigDecimal finalCycles = BigDecimal.ZERO;

        Map<InstructionCategory, BigDecimal> instructionCounts = instructionStatisticsCounter.getInstructionCounts();

        for (InstructionCategory category : InstructionCategory.values()) {
            BigDecimal categoryInstructionCount = instructionCounts.get(category);

            if (categoryInstructionCount == null) {
                categoryInstructionCount = BigDecimal.ZERO;
            }

            totalInstructionCount = totalInstructionCount.add(categoryInstructionCount);
            finalCycles = finalCycles.add(categoryInstructionCount.multiply(category.getWeight()));
        }
        totalInstructionCounter.setText(totalInstructionCount.toPlainString());
        finalCyclesCounter.setText(finalCycles.toPlainString());

        for (InstructionCategory category : InstructionCategory.values()) {
            BigDecimal categoryInstructionCount = instructionCounts.get(category);

            if (categoryInstructionCount == null) {
                categoryInstructionCount = BigDecimal.ZERO;
            }

            BigDecimal categoryWeightedCyclesCount = categoryInstructionCount.multiply(category.getWeight());

            JTextField categoryCounterTextField = instructionCounters.get(category);
            JTextField categoryWeightedCyclesCounterTextField = instructionWeightedCyclesCounters.get(category);
            JProgressBar categoryProgressBar = instructionProgressBars.get(category);
            JProgressBar categoryWeightedCyclesProgressBar = instructionWeightedCyclesProgressBars.get(category);

            categoryCounterTextField.setText(categoryInstructionCount.toPlainString());
            categoryWeightedCyclesCounterTextField.setText(categoryWeightedCyclesCount.toPlainString());

            categoryProgressBar.setMaximum(1000);

            int progressBarApproximateValue;

            if (!totalInstructionCount.equals(BigDecimal.ZERO)) {
                progressBarApproximateValue = categoryInstructionCount
                        .divideToIntegralValue(totalInstructionCount)
                        .multiply(BigDecimal.valueOf(1000))
                        .intValue();
            } else {
                progressBarApproximateValue = 0;
            }

            categoryProgressBar.setValue(progressBarApproximateValue);

            categoryWeightedCyclesProgressBar.setMaximum(1000);

            int categoryWeightedCyclesProgressBarApproximateValue;

            if (!finalCycles.equals(BigDecimal.ZERO)) {
                categoryWeightedCyclesProgressBarApproximateValue = categoryWeightedCyclesCount
                        .divideToIntegralValue(finalCycles)
                        .multiply(BigDecimal.valueOf(1000))
                        .intValue();
            } else {
                categoryWeightedCyclesProgressBarApproximateValue = 0;
            }

            categoryWeightedCyclesProgressBar.setValue(categoryWeightedCyclesProgressBarApproximateValue);
        }
    }
}
