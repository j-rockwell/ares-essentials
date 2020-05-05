package com.playares.essentials.broadcast;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.playares.essentials.EssentialsService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Queue;

public final class BroadcastManager {
    @Getter public final EssentialsService essentials;
    @Getter public final BroadcastHandler handler;
    @Getter @Setter public int interval;
    @Getter @Setter public String prefix;
    @Getter @Setter public List<String> messages;
    @Getter @Setter public Queue<String> queue;
    @Getter @Setter public BukkitTask task;

    public BroadcastManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new BroadcastHandler(this);
        this.messages = Lists.newArrayList();
        this.queue = Queues.newConcurrentLinkedQueue();
    }

    /**
     * Pulls a new message from the broadcast queue
     * @return Message
     */
    protected String pullMessage() {
        if (queue.isEmpty()) {
            if (messages.isEmpty()) {

            }
            queue.addAll(messages);
        }

        return queue.remove();
    }
}
