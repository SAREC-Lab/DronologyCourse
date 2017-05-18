package edu.nd.dronology.ui.cc.main.remote;

import edu.nd.dronology.services.core.api.IBaseServiceProvider;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class ServiceProvider {

	private static IBaseServiceProvider provider;

	private static final ILogger LOGGER = LoggerProvider.getLogger(ServiceProvider.class);

	static {
		initProvider();
		// LaunchActivator.getDefault().getPreferenceStore().addPropertyChangeListener(new
		// IPropertyChangeListener() {
		// @Override
		// public void propertyChange(PropertyChangeEvent event) {
		// initProvider();
		// }
		// });
	}

	public static IBaseServiceProvider getBaseServiceProvider() {
		return provider;
	}

	protected static void initProvider() {
		// LaunchActivator.getDefault().setRMIInterface();
		// IPreferenceStore store =
		// LaunchActivator.getDefault().getPreferenceStore();
		// String serverHost =
		// store.getString(ConfigPreferenceConstants.SERVER_HOST);
		// int serverPort = store.getInt(ConfigPreferenceConstants.SERVER_PORT);
		String serverHost = "localhost";
		int serverPort = 9898;

		provider = new BaseServiceProvider();
		LOGGER.info("Using '" + provider.getClass() + "' as service provider");
		provider.init(serverHost, serverPort);

	}
}