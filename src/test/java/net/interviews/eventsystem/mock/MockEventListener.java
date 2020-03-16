package net.interviews.eventsystem.mock;

import net.interviews.eventsystem.Event;
import net.interviews.eventsystem.EventListener;

/**
 * An implementation of EventListener used for tests.
 */
public class MockEventListener implements EventListener {

    public int count;
    private boolean called;
    private final Class[] handledClasses;

    public MockEventListener(Class[] handledClasses) {
        this.handledClasses = handledClasses;
    }

    @Override
    public Class[] getHandledEventClasses() {
        return handledClasses;
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

}