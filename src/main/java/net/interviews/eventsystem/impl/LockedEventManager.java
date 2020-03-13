package net.interviews.eventsystem.impl;

import net.interviews.eventsystem.Event;
import net.interviews.eventsystem.EventListener;
import net.interviews.eventsystem.EventManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Nazar Lelyak.
 */
public class LockedEventManager implements EventManager {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rLock = lock.readLock();
    private final Lock wLock = lock.writeLock();

    private final Map<String, EventListener> listeners = new HashMap<>();

    @Override
    public void publishEvent(final Event event) {
        final Class<?> eventClazz = event.getClass();
        final Collection<EventListener> eventListenersCollection;
        try {
            rLock.lock();
            eventListenersCollection = listeners.values();
        } finally {
            rLock.unlock();
        }

        for (EventListener listener : eventListenersCollection) {

            @SuppressWarnings("rawtypes")
            final Class[] handlingClasses = listener.getHandledEventClasses();

            if (handlingClasses.length == 0 || Arrays.asList(handlingClasses).contains(eventClazz))
                listener.handleEvent(event);
        }
    }

    @Override
    public void registerListener(String listenerKey, EventListener listener) {
        try {
            wLock.lock();
            listeners.put(listenerKey, listener);
        } finally {
            wLock.unlock();
        }
    }

    @Override
    public void unregisterListener(String listenerKey) {
        try {
            wLock.lock();
            listeners.remove(listenerKey);
        } finally {
            wLock.unlock();
        }
    }

}
