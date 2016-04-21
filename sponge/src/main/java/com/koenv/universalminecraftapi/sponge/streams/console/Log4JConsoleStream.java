package com.koenv.universalminecraftapi.sponge.streams.console;

import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.Optional;

public class Log4JConsoleStream {
    public Log4JConsoleStream(UniversalMinecraftAPIInterface uma) {
        uma.getStreamManager().registerStream("console");

        Logger rootLogger = (Logger) LogManager.getRootLogger();

        Layout<? extends Serializable> layout = Optional.ofNullable(rootLogger.getAppenders().get("Console")).map(Appender::getLayout).orElse(null);

        if (layout == null) {
            layout = PatternLayout.createLayout("[%d{HH:mm:ss} %level] [%logger{1}]: %msg%n", null, null, null, null);
        }

        Filter infoFilter = ThresholdFilter.createFilter("INFO", "ACCEPT", "DENY");
        Filter warnFilter = ThresholdFilter.createFilter("WARN", "ACCEPT", "DENY");

        rootLogger.addAppender(new Log4JConsoleStreamAppender(uma, ThresholdFilter.createFilter("INFO", "ACCEPT", "DENY"), layout));

        Logger minecraftLogger = (Logger) LogManager.getLogger("net.minecraft");

        Layout<? extends Serializable> minecraftLayout = null;

        minecraftLayout = Optional.ofNullable(minecraftLogger.getAppenders().get("Console")).map(Appender::getLayout).orElse(null);

        if (minecraftLayout == null) {
            minecraftLayout = PatternLayout.createLayout("[%d{HH:mm:ss} %level]: %msg%n", null, null, null, null);
        }

        Appender minecraftAppender = new Log4JConsoleStreamAppender(uma, infoFilter, minecraftLayout);

        minecraftLogger.addAppender(minecraftAppender);

        ((Logger) LogManager.getLogger("com.mojang")).addAppender(minecraftAppender);

        Appender warnAppender = new Log4JConsoleStreamAppender(uma, warnFilter, layout);

        ((Logger) LogManager.getLogger("LaunchWrapper")).addAppender(warnAppender);
        ((Logger) LogManager.getLogger("com.zaxxer.hikari")).addAppender(warnAppender);
    }
}
