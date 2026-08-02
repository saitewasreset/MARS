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
