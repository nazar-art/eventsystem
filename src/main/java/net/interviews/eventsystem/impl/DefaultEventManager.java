package net.interviews.eventsystem.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.interviews.eventsystem.Event;
import net.interviews.eventsystem.EventListener;
import net.interviews.eventsystem.EventManager;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Manages the firing and receiving of events.
 *
 * <p>Any event passed to {@link #publishEvent} will be passed through to "interested" listeners.
 *
 * <p>Event listeners can register to receive events via
 * {@link #registerListener(String, EventListener)}
 */
public class DefaultEventManager implements EventManager {
    @Getter
    private Map<String, EventListener> listeners = Maps.newHashMap();
    private Map<Class<?>, List<EventListener>> listenersByClass = Maps.newHashMap();


    @Override
    public void publishEvent(Event event) {
        if (event == null) {
            System.err.println("Null event fired?");
            return;
        }

        List<EventListener> listenersToProcess = listenersByClass.get(event.getClass());

        if (listenersToProcess == null || listenersToProcess.size() == 0)
            return;

        listenersToProcess.forEach(l -> l.handleEvent(event));
    }

    @Override
    public void registerListener(String listenerKey, EventListener listener) {
        if (listenerKey == null || listenerKey.isEmpty())
            throw new IllegalArgumentException("Key for the listener must not be null: " + listenerKey);

        if (listener == null)
            throw new IllegalArgumentException("The listener must not be null");

        if (listeners.containsKey(listenerKey))
            unregisterListener(listenerKey);


        Class[] classes = listener.getHandledEventClasses();
        for (Class aClass : classes) {
            listenersByClass.putIfAbsent(aClass, Lists.newArrayList());
            listenersByClass.get(aClass).add(listener);
        }

        listeners.put(listenerKey, listener);
    }

    @Override
    public void unregisterListener(String listenerKey) {
        EventListener listener = listeners.get(listenerKey);

        listenersByClass.values().forEach(l -> l.remove(listener));
        listeners.remove(listenerKey);
    }
}
