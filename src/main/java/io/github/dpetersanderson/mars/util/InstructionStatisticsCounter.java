package io.github.dpetersanderson.mars.util;

import io.github.dpetersanderson.mars.InstructionCategory;
import io.github.dpetersanderson.mars.ProgramStatement;
import io.github.dpetersanderson.mars.mips.hardware.AccessNotice;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.Memory;
import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessNotice;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class InstructionStatisticsCounter {
    private final EnumMap<InstructionCategory, BigDecimal> instructionCounts = new EnumMap<>(InstructionCategory.class);

    public InstructionStatisticsCounter() {
        for (InstructionCategory instructionCategory : InstructionCategory.values()) {
            instructionCounts.put(instructionCategory, BigDecimal.ZERO);
        }
    }

    /**
     * The last address we saw. We ignore it because the only way for a
     * program to execute twice the same instruction is to enter an infinite
     * loop, which is not insteresting in the POV of counting instructions.
     */
    private int lastAddress = -1;

    /**
     * decodes the instruction and determines the category of the instruction.
     *
     * The instruction is decoded by extracting the operation and function code of the 32-bit instruction.
     * Only the most relevant instructions are decoded and categorized.
     *
     * @param stmt the instruction to decode
     * @return the category of the instruction
     */
    public static InstructionCategory getInstructionCategory(ProgramStatement stmt) {
        int binaryStatement = stmt.getBinaryStatement();
        int opCode = binaryStatement >>> 26;
        int rs = (binaryStatement >>> 21) & 0x1F;
        int rt = (binaryStatement >>> 16) & 0x1F;
        int funct = binaryStatement & 0x3F;

        return switch (opCode) {
            case 0x00 ->
                switch (funct) {
                    case 0x08, 0x09 -> InstructionCategory.JUMP; // jr, jalr
                    case 0x18, 0x19 -> InstructionCategory.MULT; // mult, multu
                    case 0x1A, 0x1B -> InstructionCategory.DIV; // div, divu
                    default -> InstructionCategory.OTHER;
                };
            case 0x01 ->
                switch (rt) {
                    case 0x00, 0x01, 0x10, 0x11 -> InstructionCategory.BRANCH; // bltz, bgez, bltzal, bgezal
                    default -> InstructionCategory.OTHER; // trap immediate instructions
                };
            case 0x02, 0x03 -> InstructionCategory.JUMP; // j, jal
            case 0x04, 0x05, 0x06, 0x07, 0x14, 0x15, 0x16, 0x17 -> InstructionCategory.BRANCH;
            case 0x11 -> {
                if (rs == 0x08) { // bc1f, bc1t
                    yield InstructionCategory.BRANCH;
                }
                if (rs == 0x10 || rs == 0x11) { // single- or double-precision arithmetic
                    if (funct == 0x02) yield InstructionCategory.MULT; // mul.s, mul.d
                    if (funct == 0x03) yield InstructionCategory.DIV; // div.s, div.d
                }
                yield InstructionCategory.OTHER;
            }
            case 0x1C ->
                switch (funct) {
                    case 0x00, 0x01, 0x02, 0x04, 0x05 -> InstructionCategory.MULT; // madd, maddu, mul, msub, msubu
                    default -> InstructionCategory.OTHER;
                };
            case 0x20,
                    0x21,
                    0x22,
                    0x23,
                    0x24,
                    0x25,
                    0x26, // lb, lh, lwl, lw, lbu, lhu, lwr
                    0x28,
                    0x29,
                    0x2A,
                    0x2B,
                    0x2E, // sb, sh, swl, sw, swr
                    0x30,
                    0x31,
                    0x35, // ll, lwc1, ldc1
                    0x38,
                    0x39,
                    0x3D -> InstructionCategory.MEM; // sc, swc1, sdc1
            default -> InstructionCategory.OTHER;
        };
    }

    public Map<InstructionCategory, BigDecimal> getInstructionCounts() {
        return instructionCounts.clone();
    }

    public Map<InstructionCategory, BigDecimal> getWeightedCycles() {
        EnumMap<InstructionCategory, BigDecimal> weightedCycles = new EnumMap<>(InstructionCategory.class);

        for (InstructionCategory instructionCategory : InstructionCategory.values()) {
            BigDecimal count = instructionCounts.get(instructionCategory);

            if (count == null) {
                count = BigDecimal.ZERO;
            }

            weightedCycles.put(instructionCategory, count.multiply(instructionCategory.getWeight()));
        }

        return weightedCycles;
    }

    public void processMIPSUpdate(MemoryAccessNotice notice) {

        if (!notice.accessIsFromMIPS()) return;

        // check for a read access in the text segment
        if (notice.getAccessType() == AccessNotice.AccessType.READ) {
            // The next three statments are from Felipe Lessa's instruction counter.  Prevents double-counting.
            int address = notice.getAddress();
            if (address == lastAddress) return;
            lastAddress = address;

            try {
                // access the statement in the text segment without notifying other tools etc.
                ProgramStatement stmt = Memory.getInstance().getStatementNoNotify(notice.getAddress());

                // necessary to handle possible null pointers at the end of the program
                // (e.g., if the simulator tries to execute the next instruction after the last instruction in the text
                // segment)
                if (stmt != null) {
                    InstructionCategory category = InstructionStatisticsCounter.getInstructionCategory(stmt);

                    instructionCounts.compute(category, (k, v) -> v == null ? BigDecimal.ONE : v.add(BigDecimal.ONE));
                }
            } catch (AddressErrorException e) {
                // silently ignore these exceptions
            }
        }
    }
}
