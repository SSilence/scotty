package scotty.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import scotty.event.EventDispatcher;
import scotty.event.Events;
import scotty.util.Messages;

/**
 * Manages the systray.
 * 
 * @author flo
 * 
 */
public class SystrayManager {

	private Logger log = Logger.getLogger(SystrayManager.class);

	// private SystemTray tray;
	private TrayIcon icon;
	private Messages msgs = new Messages();

	private String defaultImage = "/systray-default.gif";

	private String runningImage = "/systray-loader.gif";



	public SystrayManager() {
		if (isSupported()) {
			PopupMenu menu = new PopupMenu();
			SystemTray tray = SystemTray.getSystemTray();

			icon = new TrayIcon(createImage(defaultImage), "scotty", menu);

			MenuItem close = new MenuItem(msgs.exit());
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					EventDispatcher.fireEvent(Events.EXIT, null);
					System.exit(0);
				};
			});
			menu.add(close);

			try {
				tray.add(icon);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void setRunning(boolean runs) {
		updateTrayIconIfSupported(runs);
	}

	private void updateTrayIconIfSupported(boolean runs) {
		if (!isSupported()) {
			return;
		}
		if (runs) {
			icon.setImage(createImage(runningImage));
		} else {
			icon.setImage(createImage(defaultImage));
		}
	}

	public boolean isSupported() {
		boolean avail = false;
		try {
			avail = SystemTray.isSupported();
		} catch (Exception e) {
			avail = false;
			log.warn("Systray is not available", e);
		}

		return avail;
	}

	public void setTooltip(String tooltip) {
		if (isSupported()) {
			icon.setToolTip(tooltip);
		}
	}

	public TrayIcon getIcon() {
		return icon;
	}

	public void setIcon(TrayIcon icon) {
		this.icon = icon;
	}

	private static Image createImage(String path) {
		java.net.URL imgURL = SystrayManager.class.getResource(path);
		if (imgURL != null) {
			Image img = Toolkit.getDefaultToolkit().createImage(imgURL);
			return img;
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
