package net.interviews.eventsystem.impl;

import net.interviews.eventsystem.Event;
import net.interviews.eventsystem.EventListener;
import net.interviews.eventsystem.EventManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Nazar Lelyak.
 */
public class DefaultEventManagerTest {

    private EventManager eventManager = new DefaultEventManager();

    @Test
    public void testPublishNullEvent() {
        eventManager.publishEvent(null);
    }

    @Test
    public void testRegisterListenerAndPublishEvent() {
        MockEventListener mockEventListener = new MockEventListener(new Class[]{SpecificTestEvent.class});
        eventManager.registerListener("some.key", mockEventListener);
        eventManager.publishEvent(new SpecificTestEvent());
        assertTrue(mockEventListener.isCalled());
    }

    @Test
    public void testRegisterListenerAndPublishEventWithoutFiltering() {
        System.out.println("New Method Test");
        MockEventListener mockEventListener = new MockEventListener(new Class[]{SpecificTestEvent.class, NewSpecificTestEvent.class});
        eventManager.registerListener("some.key", mockEventListener);

        eventManager.publishEvent(new NewSpecificTestEvent());

        assertTrue(mockEventListener.isCalled());
    }

    @Test
    public void testListenerWithoutMatchingEventClass() {
        MockEventListener mockEventListener = new MockEventListener(new Class[]{BaseTestEvent.class});
        eventManager.registerListener("some.key", mockEventListener);
        eventManager.publishEvent(new SpecificTestEvent());
        assertFalse(mockEventListener.isCalled());
    }

    @Test
    public void testUnregisterListener() {
        MockEventListener mockEventListener = new MockEventListener(new Class[]{SpecificTestEvent.class});
        MockEventListener mockEventListener2 = new MockEventListener(new Class[]{SpecificTestEvent.class});

        eventManager.registerListener("some.key", mockEventListener);
        eventManager.registerListener("another.key", mockEventListener2);
        eventManager.unregisterListener("some.key");

        eventManager.publishEvent(new SpecificTestEvent());
        assertFalse(mockEventListener.isCalled());
        assertTrue(mockEventListener2.isCalled());
    }


    /**
     * Check that registering and unregistering listeners behaves properly.
     */
    @Test
    public void testRemoveNonexistentListener() {
        DefaultEventManager dem = (DefaultEventManager) eventManager;
        assertEquals(0, dem.getListeners().size());
        eventManager.registerListener("some.key", new MockEventListener(new Class[]{SpecificTestEvent.class}));
        assertEquals(1, dem.getListeners().size());
        eventManager.unregisterListener("this.key.is.not.registered");
        assertEquals(1, dem.getListeners().size());
        eventManager.unregisterListener("some.key");
        assertEquals(0, dem.getListeners().size());
    }

    /**
     * Registering duplicate keys on different listeners should only fire the most recently added.
     */
    @Test
    public void testDuplicateKeysForListeners() {
        MockEventListener mockEventListener = new MockEventListener(new Class[]{SpecificTestEvent.class});
        MockEventListener mockEventListener2 = new MockEventListener(new Class[]{SpecificTestEvent.class});

        eventManager.registerListener("some.key", mockEventListener);
        eventManager.registerListener("some.key", mockEventListener2);

        eventManager.publishEvent(new SpecificTestEvent());

        assertTrue(mockEventListener2.isCalled());
        assertFalse(mockEventListener.isCalled());

        mockEventListener.resetCalled();
        mockEventListener2.resetCalled();

        eventManager.unregisterListener("some.key");
        eventManager.publishEvent(new SpecificTestEvent());

        assertFalse(mockEventListener2.isCalled());
        assertFalse(mockEventListener.isCalled());
    }

    /**
     * Attempting to register a null with a valid key should result in an illegal argument exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddValidKeyWithNullListener() {
        eventManager.registerListener("bogus.key", null);
    }
}

/**
 * An implementation of EventListener used for tests.
 */
class MockEventListener implements EventListener {
    private int count;
    private boolean called;
    private final Class[] handledClasses;

    public MockEventListener(Class[] handledClasses) {
        this.handledClasses = handledClasses;
    }

    @Override
    public void handleEvent(Event event) {
        called = true;
        count++;
    }

    public void resetCalled() {
        called = false;
    }

    public boolean isCalled() {
        return called;
    }

    @Override
    public Class[] getHandledEventClasses() {
        return handledClasses;
    }
}

class BaseTestEvent implements Event {
}

class NewSpecificTestEvent extends BaseTestEvent implements Event {
    public NewSpecificTestEvent() {
        System.out.println("New Me");
    }
}

class SpecificTestEvent extends BaseTestEvent implements Event {
    public SpecificTestEvent() {
        System.out.println(" Me");
    }
}