package scotty.ui;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;


/**
 * This plugin sets the state of the systray icon. (Wether there is a current
 * transmission or not.)
 * 
 * @author flo
 * 
 */
public class SystrayIndicatorProxyPlugin extends ProxyPlugin {

	private Logger log = Logger.getLogger(getPluginName());

	private SystrayManager tray;

	private boolean running;

	private Date last;

	/**
	 * Shows the default systray icon after 5000ms of no transmission.
	 */
	private int showDefaultIconAfter = 5000;

	/**
	 * The systray is checked every 5000ms, wether a transmission is in progress
	 * or not.
	 */
	private int checkLastTransferInterval = 5000;

	public SystrayIndicatorProxyPlugin(final SystrayManager tray) {
		this.tray = tray;

		Timer t = new Timer("Update systray icon timer", true);
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Date now = new Date();
				if (last != null
						&& now.getTime() > (last.getTime() + showDefaultIconAfter)) {
					running = false;
					tray.setRunning(false);
				}
			}
		};
		t.schedule(task, new Date(), checkLastTransferInterval);
	}

	@Override
	public String getPluginName() {
		return "IndicatingProxyPlugin";
	}

	public HTTPClient getProxyPlugin(HTTPClient in) {
		return new Plugin(in);
	}

	private class Plugin implements HTTPClient {

		private HTTPClient in;

		public Plugin(HTTPClient in) {
			this.in = in;
		}

		@Override
		public Response fetchResponse(Request request) throws IOException {
			if (!running) {
				running = true;
				tray.setRunning(true);
			}
			last = new Date();

			return in.fetchResponse(request);

		}
	}
}
