package scotty.event;

/**
 * EventObserver - gets notified by the {@link EventDispatcher} when events are
 * fired.
 * 
 * @author flo
 * 
 */
public interface EventObserver {

	/**
	 * Gets called by the EventDispatcher
	 * 
	 * @param event
	 *            Event.
	 * @param o
	 *            payload.
	 */
	public void eventReceived(Events event, Object o);
}
