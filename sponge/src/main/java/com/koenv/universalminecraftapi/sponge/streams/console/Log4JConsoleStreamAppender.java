package com.koenv.universalminecraftapi.sponge.streams.console;

import com.koenv.universalminecraftapi.UniversalMinecraftAPIInterface;
import com.koenv.universalminecraftapi.streams.models.ConsoleEvent;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.Serializable;
import java.util.Objects;

@Plugin(name = "UMAConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class Log4JConsoleStreamAppender extends AbstractAppender {
    private UniversalMinecraftAPIInterface uma;

    public Log4JConsoleStreamAppender(UniversalMinecraftAPIInterface uma, Filter filter, Layout<? extends Serializable> layout) {
        super("UniversalMinecraftAPI", filter, layout, false);
        this.uma = uma;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void append(LogEvent event) {
        String normalMessage = getLayout().toSerializable(event).toString();

        String strippedMessage = TextSerializers.LEGACY_FORMATTING_CODE.stripCodes(normalMessage); // strip formatting codes
        try {
            uma.getStreamManager().send("console", subscription -> {
                String message = strippedMessage;
                if (Objects.equals(subscription.getParameter("nostrip"), "true")) {
                    message = normalMessage;
                }
                return new ConsoleEvent(message, event.getLevel().name(), event.getMillis());
            });
        } catch (Exception e) {
            // doesn't really matter
            e.printStackTrace();
        }
    }

    @Override
    public boolean isStarted() {
        return true;
    }
}
