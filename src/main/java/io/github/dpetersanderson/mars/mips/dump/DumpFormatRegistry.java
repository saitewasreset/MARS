package io.github.dpetersanderson.mars.mips.dump;

import java.util.List;

public final class DumpFormatRegistry {
    private static final List<DumpFormat> dumpFormats = List.of(
            new AsciiTextDumpFormat(),
            new BinaryDumpFormat(),
            new BinaryTextDumpFormat(),
            new HexTextDumpFormat(),
            new IntelHexDumpFormat(),
            new SegmentWindowDumpFormat());

    private DumpFormatRegistry() {}

    public static List<DumpFormat> getDumpFormats() {
        return dumpFormats;
    }
}
