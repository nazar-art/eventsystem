package net.interviews.eventsystem.impl;


import net.interviews.eventsystem.Event;
import net.interviews.eventsystem.EventListener;
import net.interviews.eventsystem.EventManager;

public class SimpleEventManager implements EventManager {

    private EventListener listener;

    @Override
    public void publishEvent(Event event) {
        if (listener != null) {
            listener.handleEvent(event);
        }
    }

    @Override
    public void registerListener(String listenerKey, EventListener listener) {
        this.listener = listener;
    }

    @Override
    public void unregisterListener(String listenerKey) {
        listener = null;
    }
}
