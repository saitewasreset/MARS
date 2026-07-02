package io.github.dpetersanderson.mars;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dpetersanderson.mars.mips.hardware.AccessNotice;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor0;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.Memory;
import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessListener;
import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessNotice;
import io.github.dpetersanderson.mars.mips.hardware.MemoryConfigurations;
import io.github.dpetersanderson.mars.mips.hardware.Register;
import io.github.dpetersanderson.mars.mips.hardware.RegisterAccessListener;
import io.github.dpetersanderson.mars.mips.hardware.RegisterAccessNotice;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.hardware.SettingsListener;
import io.github.dpetersanderson.mars.mips.hardware.SimulatorListener;
import io.github.dpetersanderson.mars.simulator.Simulator;
import io.github.dpetersanderson.mars.simulator.SimulatorNotice;
import io.github.dpetersanderson.mars.util.SystemIO;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarsListenerTest {

    @BeforeAll
    static void initializeMars() {
        Globals.initialize(false);
    }

    @BeforeEach
    void resetMarsState() {
        MemoryConfigurations.setCurrentConfiguration(MemoryConfigurations.getDefaultConfiguration());
        Globals.memory.clear();
        Globals.memory.removeAllMemoryAccessListeners();
        Globals.symbolTable.clear();
        Globals.program = null;
        Globals.exitCode = 0;
        Globals.debug = false;
        Globals.getSettings().setBooleanSettingNonPersistent(Settings.DELAYED_BRANCHING_ENABLED, false);
        Globals.getSettings().setBooleanSettingNonPersistent(Settings.SELF_MODIFYING_CODE_ENABLED, false);
        Globals.getSettings().setBooleanSettingNonPersistent(Settings.START_AT_MAIN, false);
        RegisterFile.resetRegisters();
        Coprocessor0.resetRegisters();
        Coprocessor1.resetRegisters();
        SystemIO.resetFiles();
    }

    @Test
    void registerAccessListenerReceivesWriteNotificationAndNotAfterRemove() {
        Register register = RegisterFile.getUserRegister("$t0");
        CountingRegisterAccessListener listener = new CountingRegisterAccessListener();

        register.addListener(listener);
        register.setValue(42);

        assertEquals(1, listener.callCount);
        assertEquals(AccessNotice.AccessType.WRITE, listener.lastNotice.getAccessType());
        assertEquals("$t0", listener.lastNotice.getRegisterName());
        assertEquals(register, listener.lastNotice.getRegister());

        register.removeListener(listener);
        register.setValue(99);

        assertEquals(1, listener.callCount);
    }

    @Test
    void registerAccessListenerReceivesReadNotification() {
        Register register = RegisterFile.getUserRegister("$t0");
        CountingRegisterAccessListener listener = new CountingRegisterAccessListener();

        register.setValue(55);
        register.addListener(listener);
        register.getValue();

        assertEquals(1, listener.callCount);
        assertEquals(AccessNotice.AccessType.READ, listener.lastNotice.getAccessType());
        assertEquals("$t0", listener.lastNotice.getRegisterName());
    }

    @Test
    void removeNonexistentListenerDoesNotThrow() {
        Register register = RegisterFile.getUserRegister("$t1");
        CountingRegisterAccessListener listener = new CountingRegisterAccessListener();

        assertDoesNotThrow(() -> register.removeListener(listener));
    }

    @Test
    void registerFileAddRegistersListenerAndDeleteRegistersObserver() {
        CountingRegisterAccessListener listener = new CountingRegisterAccessListener();

        RegisterFile.addRegistersListener(listener);
        RegisterFile.updateRegister("$t3", 101);

        assertTrue(listener.callCount >= 1,
                "Listener should fire at least once when writing to a GPR after addRegistersListener");

        listener.reset();
        RegisterFile.removeRegistersObserver(listener);
        RegisterFile.updateRegister("$t4", 202);

        assertEquals(0, listener.callCount,
                "Listener should NOT fire after removeRegistersObserver");
    }

    @Test
    void coprocessor0AddAndRemoveRegistersListener() {
        CountingRegisterAccessListener listener = new CountingRegisterAccessListener();

        Coprocessor0.addRegistersListener(listener);
        Coprocessor0.updateRegister("$12", 0xABCD);

        assertTrue(listener.callCount >= 1,
                "Listener should fire when writing to a Coprocessor0 register");

        listener.reset();
        Coprocessor0.removeRegistersListener(listener);
        Coprocessor0.updateRegister("$14", 0x1234);

        assertEquals(0, listener.callCount,
                "Listener should NOT fire after removeRegistersListener");
    }

    @Test
    void coprocessor1RemoveRegistersListenerStopsNotification() {
        CountingRegisterAccessListener listener = new CountingRegisterAccessListener();

        Coprocessor1.addRegistersListener(listener);
        Coprocessor1.updateRegister(0, 42);

        assertTrue(listener.callCount >= 1,
                "Listener should fire when writing to a Coprocessor1 register");

        listener.reset();
        Coprocessor1.removeRegistersListener(listener);
        Coprocessor1.updateRegister(1, 99);

        assertEquals(0, listener.callCount,
                "Listener should NOT fire after removeRegistersListener (regression for Copy-Paste bug: "
                        + "removeRegistersListener was calling addListener instead of removeListener)");
    }

    private static void safeMemorySet(int address, int value) {
        try {
            Globals.memory.set(address, value, Memory.WORD_LENGTH_BYTES);
        } catch (AddressErrorException e) {
            throw new RuntimeException(e);
        }
    }

    private static void safeMemorySetRawWord(int address, int value) {
        try {
            Globals.memory.setRawWord(address, value);
        } catch (AddressErrorException e) {
            throw new RuntimeException(e);
        }
    }

    private static int safeMemoryGet(int address) {
        try {
            return Globals.memory.get(address, Memory.WORD_LENGTH_BYTES);
        } catch (AddressErrorException e) {
            throw new RuntimeException(e);
        }
    }

    private static void safeAddMemoryAccessListener(MemoryAccessListener listener, int start, int end) {
        try {
            Globals.memory.addMemoryAccessListener(listener, start, end);
        } catch (AddressErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void memoryAccessListenerReceivesWriteNotification() {
        CountingMemoryAccessListener listener = new CountingMemoryAccessListener();

        Globals.memory.addMemoryAccessListener(listener);
        safeMemorySet(0x10000000, 0xDEADBEEF);

        assertEquals(1, listener.callCount);
        assertEquals(AccessNotice.AccessType.WRITE, listener.lastNotice.getAccessType());
        assertEquals(0x10000000, listener.lastNotice.getAddress());
        assertEquals(0xDEADBEEF, listener.lastNotice.getValue());

        Globals.memory.removeMemoryAccessListener(listener);
    }

    @Test
    void memoryAccessListenerRemoveStopsNotification() {
        CountingMemoryAccessListener listener = new CountingMemoryAccessListener();

        Globals.memory.addMemoryAccessListener(listener);
        safeMemorySet(0x10000100, 1);

        assertEquals(1, listener.callCount);

        listener.reset();
        Globals.memory.removeMemoryAccessListener(listener);
        safeMemorySet(0x10000100, 2);

        assertEquals(0, listener.callCount,
                "Listener should NOT fire after being removed");
    }

    @Test
    void memoryAccessListenerAddressRangeFiltering() {
        CountingMemoryAccessListener listener = new CountingMemoryAccessListener();

        safeAddMemoryAccessListener(listener, 0x10000000, 0x10000010);

        safeMemorySet(0x10000008, 1);
        assertEquals(1, listener.callCount,
                "Write inside registered range should trigger listener");

        listener.reset();
        safeMemorySet(0x10000200, 2);
        assertEquals(0, listener.callCount,
                "Write outside registered range should NOT trigger listener");

        Globals.memory.removeMemoryAccessListener(listener, 0x10000000, 0x10000010);
    }

    @Test
    void memoryAccessListenerRemoveAllStopsAllListeners() {
        CountingMemoryAccessListener a = new CountingMemoryAccessListener();
        CountingMemoryAccessListener b = new CountingMemoryAccessListener();

        Globals.memory.addMemoryAccessListener(a);
        Globals.memory.addMemoryAccessListener(b);
        safeMemorySet(0x10000000, 1);

        assertEquals(1, a.callCount);
        assertEquals(1, b.callCount);

        a.reset();
        b.reset();
        Globals.memory.removeAllMemoryAccessListeners();
        safeMemorySet(0x10000000, 2);

        assertEquals(0, a.callCount);
        assertEquals(0, b.callCount);
    }

    @Test
    void simulatorListenerReceivesStartAndStopNotifications(@TempDir Path tempDir) throws Exception {
        CountingSimulatorListener listener = new CountingSimulatorListener();
        Simulator.getInstance().addListener(listener);

        MIPSprogram program = TestUtils.assembleProgram(
                tempDir,
                "sim-listen.asm",
                ".text",
                ".globl main",
                "main:",
                "addi $v0, $zero, 10",
                "syscall");

        RegisterFile.initializeProgramCounter(true);
        boolean finished = program.simulate(50);
        assertTrue(finished, "Simulation should finish within max steps");

        assertTrue(listener.actions.size() >= 2,
                "Should receive at least SIMULATOR_START and SIMULATOR_STOP, got: " + listener.actions);

        boolean sawStart = listener.actions.stream()
                .anyMatch(action -> action == SimulatorNotice.SIMULATOR_START);
        boolean sawStop = listener.actions.stream()
                .anyMatch(action -> action == SimulatorNotice.SIMULATOR_STOP);

        assertTrue(sawStart, "Should receive SIMULATOR_START notification");
        assertTrue(sawStop, "Should receive SIMULATOR_STOP notification");

        Simulator.getInstance().removeListener(listener);
    }

    @Test
    void settingsListenerNotifiedOnBooleanSettingChange() {
        CountingSettingsListener listener = new CountingSettingsListener();
        Settings settings = Globals.getSettings();

        settings.addListener(listener);

        boolean current = settings.getBooleanSetting(Settings.DATA_SEGMENT_HIGHLIGHTING);
        settings.setBooleanSetting(Settings.DATA_SEGMENT_HIGHLIGHTING, !current);

        assertEquals(1, listener.callCount,
                "Listener should be called when a boolean setting changes");

        settings.setBooleanSetting(Settings.DATA_SEGMENT_HIGHLIGHTING, current);

        assertEquals(2, listener.callCount,
                "Listener should be called again when setting changes back");

        settings.removeListener(listener);
    }

    @Test
    void multipleListenersOnSameRegisterAllNotified() {
        Register register = RegisterFile.getUserRegister("$t5");
        CountingRegisterAccessListener a = new CountingRegisterAccessListener();
        CountingRegisterAccessListener b = new CountingRegisterAccessListener();

        register.addListener(a);
        register.addListener(b);
        register.setValue(77);

        assertEquals(1, a.callCount, "First listener should be notified");
        assertEquals(1, b.callCount, "Second listener should be notified");

        register.removeListener(a);
        register.removeListener(b);
    }

    @Test
    void listenerCanRemoveItselfDuringNotificationWithoutConcurrentModificationException() {
        Register register = RegisterFile.getUserRegister("$t6");
        CountingRegisterAccessListener other = new CountingRegisterAccessListener();
        SelfRemovingRegisterListener selfRemoving = new SelfRemovingRegisterListener();

        register.addListener(selfRemoving);
        register.addListener(other);

        assertDoesNotThrow(() -> register.setValue(100),
                "Self-removing listener during notification should not throw ConcurrentModificationException");

        assertEquals(1, other.callCount,
                "Other listener should still receive notification");

        register.setValue(200);
        assertEquals(1, selfRemoving.callCount,
                "Self-removed listener should NOT receive subsequent notifications");
        assertEquals(2, other.callCount,
                "Other listener should receive subsequent notification");
    }

    @Test
    void addListenerTwiceThenRemoveStillFires() {
        Register register = RegisterFile.getUserRegister("$t7");
        CountingRegisterAccessListener listener = new CountingRegisterAccessListener();

        register.addListener(listener);
        register.addListener(listener);
        register.removeListener(listener);
        register.setValue(50);

        assertEquals(1, listener.callCount,
                "After add twice + remove once, one copy remains and should fire");
    }

    @Test
    void memoryAccessListenerReceivesReadNotification() {
        CountingMemoryAccessListener listener = new CountingMemoryAccessListener();
        safeMemorySet(0x10001000, 0xCAFEBABE);

        Globals.memory.addMemoryAccessListener(listener);
        safeMemoryGet(0x10001000);

        assertEquals(1, listener.callCount);
        assertEquals(AccessNotice.AccessType.READ, listener.lastNotice.getAccessType());
        assertEquals(0x10001000, listener.lastNotice.getAddress());

        Globals.memory.removeMemoryAccessListener(listener);
    }

    @Test
    void memoryAccessListenerReceivesWriteOnSetRawWord() {
        CountingMemoryAccessListener listener = new CountingMemoryAccessListener();
        Globals.memory.addMemoryAccessListener(listener);
        safeMemorySetRawWord(0x10000000, 0xBEEF);

        assertEquals(1, listener.callCount);
        assertEquals(AccessNotice.AccessType.WRITE, listener.lastNotice.getAccessType());
        assertEquals(0x10000000, listener.lastNotice.getAddress());

        Globals.memory.removeMemoryAccessListener(listener);
    }

    private static class CountingRegisterAccessListener implements RegisterAccessListener {
        int callCount;
        RegisterAccessNotice lastNotice;

        @Override
        public void registerAccessed(RegisterAccessNotice notice) {
            callCount++;
            lastNotice = notice;
        }

        void reset() {
            callCount = 0;
            lastNotice = null;
        }
    }

    private static class CountingMemoryAccessListener implements MemoryAccessListener {
        int callCount;
        MemoryAccessNotice lastNotice;

        @Override
        public void memoryAccessed(MemoryAccessNotice notice) {
            callCount++;
            lastNotice = notice;
        }

        void reset() {
            callCount = 0;
            lastNotice = null;
        }
    }

    private static class CountingSimulatorListener implements SimulatorListener {
        final List<Integer> actions = new CopyOnWriteArrayList<>();

        @Override
        public void stateChanged(SimulatorNotice notice) {
            actions.add(notice.getAction());
        }
    }

    private static class CountingSettingsListener implements SettingsListener {
        int callCount;

        @Override
        public void settingChanged() {
            callCount++;
        }
    }

    private static class SelfRemovingRegisterListener implements RegisterAccessListener {
        int callCount;

        @Override
        public void registerAccessed(RegisterAccessNotice notice) {
            callCount++;
            notice.getRegister().removeListener(this);
        }
    }
}
