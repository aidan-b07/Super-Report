package dev.aidanbooth.superreport.discord;

import dev.aidanbooth.superreport.Main;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

// Sends JDA's logs to the plugin's logger. Without it JDA prints them itself, which old servers split over several lines.
// Found through META-INF/services, and only by our relocated copy of SLF4J, so other plugins' logging isn't affected
public class JdaLogProvider implements SLF4JServiceProvider {
    private final ILoggerFactory loggerFactory = PluginLogger::new;
    private final IMarkerFactory markerFactory = new BasicMarkerFactory();
    private final MDCAdapter mdcAdapter = new NOPMDCAdapter();

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0.99";
    }

    @Override
    public void initialize() {
    }

    static class PluginLogger extends LegacyAbstractLogger {
        PluginLogger(String name) {
            this.name = name;
        }

        // Debug and trace are skipped, JDA logs a lot at those levels
        @Override public boolean isTraceEnabled() { return false; }
        @Override public boolean isDebugEnabled() { return false; }
        @Override public boolean isInfoEnabled() { return true; }
        @Override public boolean isWarnEnabled() { return true; }
        @Override public boolean isErrorEnabled() { return true; }

        @Override
        protected String getFullyQualifiedCallerName() {
            return null;
        }

        @Override
        protected void handleNormalizedLoggingCall(Level level, Marker marker, String message, Object[] arguments, Throwable throwable) {
            java.util.logging.Level julLevel;
            switch (level) {
                case ERROR: julLevel = java.util.logging.Level.SEVERE; break;
                case WARN: julLevel = java.util.logging.Level.WARNING; break;
                case INFO: julLevel = java.util.logging.Level.INFO; break;
                default: return;
            }
            String text = MessageFormatter.basicArrayFormat(message, arguments);
            Main.getPlugin(Main.class).getLogger().log(julLevel, "[Discord] " + text, throwable);
        }
    }
}
