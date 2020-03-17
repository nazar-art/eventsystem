package net.interviews.eventsystem.impl;

import net.interviews.eventsystem.EventManager;
import net.interviews.eventsystem.mock.SpecificTestEvent;
import net.interviews.eventsystem.mock.BaseTestEvent;
import net.interviews.eventsystem.mock.MockEventListener;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void testListenerWithMatchingBaseEventClass() {
        MockEventListener mockEventListener = new MockEventListener(new Class[]{BaseTestEvent.class});

        eventManager.registerListener("some.key", mockEventListener);
        eventManager.publishEvent(new SpecificTestEvent());

        assertTrue(mockEventListener.isCalled());
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

    /**
     * Tests from interview process.
     */
    @Test
    public void testRegisterListenerAndPublishAllEvents() {
        MockEventListener mockEventListener = new MockEventListener(new Class[] {});

        eventManager.registerListener("some.key", mockEventListener);
        eventManager.publishEvent(new SpecificTestEvent());

        assertTrue(mockEventListener.isCalled());
    }

    @Test
    public void testRegisterListenerWithParentAndPublishSubEvent() {
        MockEventListener mockEventListener = new MockEventListener(new Class[] {BaseTestEvent.class});

        eventManager.registerListener("some.key", mockEventListener);
        eventManager.publishEvent(new SpecificTestEvent());

        assertTrue(mockEventListener.isCalled());
    }
}
