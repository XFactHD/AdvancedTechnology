package xfacthd.advtech.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class LogHelper
{
    private static Logger logger;

    private static void log(Level logLevel, String message, Object... data)
    {
        logger.log(logLevel, message, data);
    }

    public static void all(String message, Object... data) { log(Level.ALL, message, data); }

    public static void debug(String message, Object... data) { log(Level.DEBUG, message, data); }

    public static void error(String message, Object... data) { log(Level.ERROR, message, data); }

    public static void fatal(String message, Object... data) { log(Level.FATAL, message, data); }

    public static void info(String message, Object... data) { log(Level.INFO, message, data); }

    public static void off(String message, Object... data) { log(Level.OFF, message, data); }

    public static void trace(String message, Object... data) { log(Level.TRACE, message, data); }

    public static void warn(String message, Object... data) { log(Level.WARN, message, data); }

    public static void setLogger(Logger log) { logger = log; }
}