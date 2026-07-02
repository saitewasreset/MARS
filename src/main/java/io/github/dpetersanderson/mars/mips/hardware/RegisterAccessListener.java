package io.github.dpetersanderson.mars.mips.hardware;

public interface RegisterAccessListener {
    void registerAccessed(RegisterAccessNotice notice);
}
