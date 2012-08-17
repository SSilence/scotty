/***********************************************************************
 *
 * $CVSHeader$
 *
 * This file is part of WebScarab, an Open Web Application Security
 * Project utility. For details, please see http://www.owasp.org/
 *
 * Copyright (c) 2002 - 2004 Rogan Dawes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Getting Source
 * ==============
 *
 * Source for this application is maintained at Sourceforge.net, a
 * repository for free software projects.
 *
 * For details, please see http://www.sourceforge.net/projects/owasp
 *
 */

/*
 * Framework.java
 *
 * Created on June 16, 2004, 8:57 AM
 */

package org.owasp.webscarab.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.owasp.webscarab.httpclient.HTTPClientFactory;
import org.owasp.webscarab.model.Preferences;

/**
 * creates a class that contains and controls the plugins.
 * 
 * @author knoppix
 */
public class Framework {

	private ArrayList<Plugin> _plugins = new ArrayList<Plugin>();

	private Logger _logger = Logger.getLogger(getClass().getName());

	// private ScriptManager _scriptManager;
	private CredentialManager _credentialManager;

	/**
	 * Creates a new instance of Framework
	 */
	public Framework() {
		_credentialManager = new CredentialManager();
		configureHTTPClient();
	}

	public CredentialManager getCredentialManager() {
		return _credentialManager;
	}

	/**
	 * adds a new plugin into the framework
	 * 
	 * @param plugin
	 *            the plugin to add
	 */
	public void addPlugin(Plugin plugin) {
		_plugins.add(plugin);
	}

	/**
	 * retrieves the named plugin, if it exists
	 * 
	 * @param name
	 *            the name of the plugin
	 * @return the plugin if it exists, or null
	 */
	public Plugin getPlugin(String name) {
		Plugin plugin = null;
		Iterator<Plugin> it = _plugins.iterator();
		while (it.hasNext()) {
			plugin = it.next();
			if (plugin.getPluginName().equals(name))
				return plugin;
		}
		return null;
	}

	/**
	 * starts all the plugins in the framework
	 */
	public void startPlugins() {
		HTTPClientFactory.getInstance().getSSLContextManager()
				.invalidateSessions();
		Iterator<Plugin> it = _plugins.iterator();
		while (it.hasNext()) {
			Plugin plugin = it.next();
			if (!plugin.isRunning()) {
				Thread t = new Thread(plugin, plugin.getPluginName());
				t.setDaemon(true);
				t.start();
			} else {
				_logger.warning(plugin.getPluginName() + " was already running");
			}
		}
	}

	public boolean isBusy() {
		Iterator<Plugin> it = _plugins.iterator();
		while (it.hasNext()) {
			Plugin plugin = it.next();
			if (plugin.isBusy())
				return true;
		}
		return false;
	}

	public boolean isRunning() {
		Iterator<Plugin> it = _plugins.iterator();
		while (it.hasNext()) {
			Plugin plugin = it.next();
			if (plugin.isRunning())
				return true;
		}
		return false;
	}

	/**
	 * stops all the plugins in the framework
	 */
	public boolean stopPlugins() {
		if (isBusy())
			return false;
		Iterator<Plugin> it = _plugins.iterator();
		while (it.hasNext()) {
			Plugin plugin = it.next();
			if (plugin.isRunning()) {
				// _logger.info("Stopping " + plugin.getPluginName());
				plugin.stop();
				// _logger.info("Done");
			} else {
				_logger.warning(plugin.getPluginName() + " was not running");
			}
		}
		return true;
	}

	private void configureHTTPClient() {
		HTTPClientFactory factory = HTTPClientFactory.getInstance();
		String prop = null;
		String value;
		int colon;
		try {
			// FIXME for some reason, we get "" instead of null for value,
			// and do not use our default value???
			prop = "WebScarab.httpProxy";
			value = Preferences.getPreference(prop);
			if (value == null || value.equals(""))
				value = ":3128";
			colon = value.indexOf(":");
			factory.setHttpProxy(value.substring(0, colon),
					Integer.parseInt(value.substring(colon + 1).trim()));

			prop = "WebScarab.httpsProxy";
			value = Preferences.getPreference(prop);
			if (value == null || value.equals(""))
				value = ":3128";
			colon = value.indexOf(":");
			factory.setHttpsProxy(value.substring(0, colon),
					Integer.parseInt(value.substring(colon + 1).trim()));

			prop = "WebScarab.noProxy";
			value = Preferences.getPreference(prop, "");
			if (value == null)
				value = "";
			factory.setNoProxy(value.split(" *, *"));

			int connectTimeout = 30000;
			prop = "HttpClient.connectTimeout";
			value = Preferences.getPreference(prop, "");
			if (value != null && !value.equals("")) {
				try {
					connectTimeout = Integer.parseInt(value);
				} catch (NumberFormatException nfe) {
				}
			}
			int readTimeout = 0;
			prop = "HttpClient.readTimeout";
			value = Preferences.getPreference(prop, "");
			if (value != null && !value.equals("")) {
				try {
					readTimeout = Integer.parseInt(value);
				} catch (NumberFormatException nfe) {
				}
			}
			factory.setTimeouts(connectTimeout, readTimeout);

		} catch (NumberFormatException nfe) {
			_logger.warning("Error parsing property " + prop + ": " + nfe);
		} catch (Exception e) {
			_logger.warning("Error configuring the HTTPClient property " + prop
					+ ": " + e);
		}
		factory.setAuthenticator(_credentialManager);
	}

}
