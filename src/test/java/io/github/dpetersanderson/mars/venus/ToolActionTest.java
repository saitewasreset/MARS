package io.github.dpetersanderson.mars.venus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dpetersanderson.mars.tools.MarsTool;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class ToolActionTest {
    @Test
    void logsToolNameAndCauseWhenToolLaunchFails() {
        Logger logger = Logger.getLogger(ToolAction.class.getName());
        Level originalLevel = logger.getLevel();
        RecordingHandler handler = new RecordingHandler();
        logger.setLevel(Level.ALL);
        logger.addHandler(handler);

        try {
            new ToolAction(FailingMarsTool.class, "Failing Tool").actionPerformed(null);
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(originalLevel);
        }

        assertEquals(1, handler.records.size());
        LogRecord record = handler.records.get(0);
        assertEquals(Level.SEVERE, record.getLevel());
        assertTrue(record.getMessage().contains("Failing Tool"));
        assertInstanceOf(IllegalStateException.class, record.getThrown());
    }

    public static class FailingMarsTool implements MarsTool {
        @Override
        public String getName() {
            return "Failing Tool";
        }

        @Override
        public void action() {
            throw new IllegalStateException("tool launch failed");
        }
    }

    private static final class RecordingHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
    }
}
