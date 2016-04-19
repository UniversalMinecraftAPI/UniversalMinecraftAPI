package com.koenv.jsonapi.sponge.streams.console;

import com.koenv.jsonapi.JSONAPIInterface;
import com.koenv.jsonapi.streams.models.ConsoleEvent;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.Serializable;
import java.util.Objects;

@Plugin(name = "JSONAPIConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class Log4JConsoleStreamAppender extends AbstractAppender {
    private JSONAPIInterface jsonapi;

    public Log4JConsoleStreamAppender(JSONAPIInterface jsonapi, Filter filter, Layout<? extends Serializable> layout) {
        super("JSONAPI", filter, layout, false);
        this.jsonapi = jsonapi;
    }

    @Override
    public void append(LogEvent event) {
        String normalMessage = getLayout().toSerializable(event).toString();
        //noinspection deprecation This is necessary because we are not stripping using this serializing, not converting to something with this serializer
        String strippedMessage = TextSerializers.LEGACY_FORMATTING_CODE.stripCodes(normalMessage); // strip formatting codes
        try {
            jsonapi.getStreamManager().send("console", subscription -> {
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
