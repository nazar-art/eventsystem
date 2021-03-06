package net.interviews.eventsystem.impl;

import lombok.Getter;
import net.interviews.eventsystem.Event;
import net.interviews.eventsystem.EventListener;
import net.interviews.eventsystem.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, EventListener> listeners = new HashMap<>();
    private Map<Class<?>, List<EventListener>> listenersByClass = new HashMap<>();

    @Override
    public void publishEvent(Event event) {
        if (event == null) {
            System.err.println("Null event fired?");
            return;
        }

        sendEventTo(event, calculateListeners(event.getClass()));

        // if we have no registered listeners by class - we should handle all events passed at this case
        if (listenersByClass.isEmpty() && !listeners.isEmpty()) {
            processAnyEvent(event);
        }
    }

    private void processAnyEvent(Event event) {
//        listeners.values().stream()
//            .filter(l -> l.getHandledEventClasses().length == 0)
//            .forEach(l -> l.handleEvent(event));

            for (EventListener listener : listeners.values()) {
                if (listener.getHandledEventClasses().length == 0) {
                    listener.handleEvent(event);
                }
            }
    }

    @Override
    public void registerListener(String listenerKey, EventListener listener) {
        if (listenerKey == null || listenerKey.equals(""))
            throw new IllegalArgumentException("Key for the listener must not be null: " + listenerKey);

        if (listener == null)
            throw new IllegalArgumentException("The listener must not be null");

        if (listeners.containsKey(listenerKey))
            unregisterListener(listenerKey);

        Class[] classes = listener.getHandledEventClasses();

        for (final Class aClass : classes) {
            addToListenerList(aClass, listener);
        }

        listeners.put(listenerKey, listener);
    }

    @Override
    public void unregisterListener(String listenerKey) {
        EventListener listener = listeners.get(listenerKey);

        for (List<EventListener> list : listenersByClass.values()) {
            list.remove(listener);
        }

        listeners.remove(listenerKey);
    }


    private Collection<EventListener> calculateListeners(Class eventClass) {
        // get all parents for event and check listeners by class
        return listenersByClass.containsKey(eventClass.getSuperclass())
                ? listenersByClass.get(eventClass.getSuperclass())
                : listenersByClass.get(eventClass);
    }

    private void sendEventTo(Event event, Collection<EventListener> listeners) {
        if (listeners == null || listeners.size() == 0)
            return;

        for (EventListener eventListener : listeners) {
            eventListener.handleEvent(event);
        }
    }

    private void addToListenerList(Class aClass, EventListener listener) {
        if (!listenersByClass.containsKey(aClass))
            listenersByClass.put(aClass, new ArrayList<>());

        listenersByClass.get(aClass).add(listener);
    }
}
