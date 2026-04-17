package com.pedestriamc.strings.paper.chat;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.common.chat.MessageProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaperMessageProcessor {

    private final StringsPlatform strings;
    private final Map<Channel, MessageProcessor> processors = new ConcurrentHashMap<>();

    public PaperMessageProcessor(StringsPlatform strings) {
        this.strings = strings;
    }

    public MessageProcessor getProcessor(Channel channel) {
        return processors.computeIfAbsent(channel, c -> new MessageProcessor(strings, c));
    }
}
