package scotty.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event Dispatcher, dispatches the events to all registered subscribers
 * {@link EventObserver}.
 * 
 * @author flo
 * 
 */
public class EventDispatcher {

	/**
	 * List of all subscribers
	 */
	private static List<EventObserver> observer = new CopyOnWriteArrayList<EventObserver>();

	/**
	 * Fires Event.
	 * 
	 * @param event
	 *            Event.
	 * @param o
	 *            Object.
	 */
	public static void fireEvent(Events event, Object o) {
		for (EventObserver e : observer) {
			e.eventReceived(event, o);
		}
	}

	/**
	 * Adds Observer.
	 * 
	 * @param eventObserver
	 *            Observer.
	 */
	public static void add(EventObserver eventObserver) {
		observer.add(eventObserver);
	}
}
