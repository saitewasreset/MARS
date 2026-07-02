package io.github.dpetersanderson.mars.mips.hardware;

public interface MemoryAccessListener {
    void memoryAccessed(MemoryAccessNotice notice);
}
