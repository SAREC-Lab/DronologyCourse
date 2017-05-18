package edu.nd.dronology.ui.cc.main.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class ThemeSwitchHandler {
	public static final String THEME_DEFAULT = "net.mv.citeomat.theme";
	public static final String THEME_RED = "net.mv.citeomat.theme.red";

	@Inject
	private IThemeManager manager;

	@Inject
	private EventBroker eventBroker;

	@Execute
	public void switchTheme(@Named("id") String themeId) {
		IThemeEngine engine = manager.getEngineForDisplay(Display.getDefault());
		if (themeId != null) {
			if (engine.getActiveTheme()==null&& themeId.equals(THEME_DEFAULT)) {
				return;
			}
			engine.setTheme(themeId, true);
			//eventBroker.post(EventConstants.THEME_CHANGE, true);
			return;
		}

		if (engine.getActiveTheme() == null || engine.getActiveTheme().getId().equals(THEME_DEFAULT)) {
			engine.setTheme(THEME_RED, true);
		} else if (engine.getActiveTheme().getId().equals(THEME_RED)) {
			engine.setTheme(THEME_DEFAULT, true);
		}
		//eventBroker.post(EventConstants.THEME_CHANGE, true);
	}

}