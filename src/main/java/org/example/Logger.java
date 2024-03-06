package org.example;

import java.util.logging.Level;

public class Logger {
    public static void log(Level l, String msg) {
        java.util.logging.Logger.getAnonymousLogger().log(l, msg);
    }
}
