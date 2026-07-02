package io.github.dpetersanderson.mars.mips.hardware;

import io.github.dpetersanderson.mars.simulator.SimulatorNotice;

public interface SimulatorListener {
    void stateChanged(SimulatorNotice notice);
}
