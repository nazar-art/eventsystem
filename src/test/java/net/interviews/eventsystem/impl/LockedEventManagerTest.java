package net.interviews.eventsystem.impl;

import net.interviews.eventsystem.Event;
import net.interviews.eventsystem.EventListener;
import net.interviews.eventsystem.EventManager;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Nazar Lelyak.
 */
public class LockedEventManagerTest {

    private EventManager eventManager = new LockedEventManager();

    @Test
    public void testRegisterListenerAndPublishEvent() {
        final MySomeEventListener someEventListener = new MySomeEventListener(MySomeEventClass.class);
        eventManager.registerListener("some.key", someEventListener);

        eventManager.publishEvent(new MySomeEventClass());
        eventManager.publishEvent(new MyAnotherEventClass());

        assertEquals(1, someEventListener.count);
    }

    @Test
    public void testUnregisterListener() {
        final MySomeEventListener someEventListener = new MySomeEventListener(MySomeEventClass.class);
        eventManager.registerListener("some.key", someEventListener);
        eventManager.unregisterListener("some.key");

        eventManager.publishEvent(new MySomeEventClass());
        eventManager.publishEvent(new MyAnotherEventClass());
        assertEquals(0, someEventListener.count);
    }

    @Test
    public void testExcluding() {
        final MySomeEventListener someEventListener = new MySomeEventListener(MySomeEventClass.class);
        eventManager.registerListener("some.key", someEventListener);

        eventManager.publishEvent(new MySomeEventClass());
        eventManager.publishEvent(new MyAnotherEventClass());
        assertEquals(1, someEventListener.count);
    }

    @Test
    public void processAll() {
        final MySomeEventListener someEventListener = new MySomeEventListener();
        eventManager.registerListener("some.key", someEventListener);

        eventManager.publishEvent(new MySomeEventClass());
        eventManager.publishEvent(new MyAnotherEventClass());
        eventManager.publishEvent(new MyMoreEventClass());
        assertEquals(3, someEventListener.count);
    }

    @Test
    public void processSome1() {
        final MySomeEventListener someEventListener = new MySomeEventListener(MyMoreEventClass.class, MyAnotherEventClass.class);
        eventManager.registerListener("some.key", someEventListener);

        eventManager.publishEvent(new MySomeEventClass());
        eventManager.publishEvent(new MyAnotherEventClass());
        eventManager.publishEvent(new MyMoreEventClass());
        assertEquals(2, someEventListener.count);
    }

    @Test
    public void processSome2() {
        final MySomeEventListener someEventListener = new MySomeEventListener(MyMoreEventClass.class);
        eventManager.registerListener("some.key", someEventListener);

        eventManager.publishEvent(new MySomeEventClass());
        eventManager.publishEvent(new MyAnotherEventClass());
        eventManager.publishEvent(new MyMoreEventClass());
        assertEquals(1, someEventListener.count);
    }
}

class MySomeEventClass implements Event {
}

class MyAnotherEventClass implements Event {
}

class MyMoreEventClass implements Event {
}

class MySomeEventListener implements EventListener {
    int count;

    private final Class<?>[] processOnly;

    MySomeEventListener(Class<?>... processOnly) {
        this.processOnly = processOnly;
    }

    MySomeEventListener() {
        this.processOnly = new Class[] {};
    }

    @Override
    public void handleEvent(Event event) {
        count++;
    }

    @Override
    public Class[] getHandledEventClasses() {
        return processOnly.clone();
    }
}