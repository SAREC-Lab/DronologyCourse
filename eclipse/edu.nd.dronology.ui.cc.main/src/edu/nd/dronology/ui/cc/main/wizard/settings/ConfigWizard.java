package edu.nd.dronology.ui.cc.main.wizard.settings;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.FontData;
import org.osgi.service.prefs.BackingStoreException;

import edu.nd.dronology.ui.cc.application.constants.CommandConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.config.ConfigPreferenceConstants;
import edu.nd.dronology.ui.cc.main.wizard.base.AbstractUAVWizard;
import edu.nd.dronology.ui.cc.main.wizard.base.UAVWizardDialog;

public class ConfigWizard extends AbstractUAVWizard {

	private String port;

	private ConfigWizardPage page;
	private String originalPort;

	private boolean needsRestart;

	private int fontHeight;

	private int fontStyle;

	private String fontName;

	

	private String theme;

	private ECommandService commandService;

	private EHandlerService handlerService;

	private String perspective;


	public void setPort(String port) {
		this.port = port;
	}

	

	public String getPort() {
		return port;
	}


	public ConfigWizard(ECommandService commandService, EHandlerService handlerService) {
		setWindowTitle("Settings");
		this.commandService = commandService;
		this.handlerService = handlerService;
		setDefaultPageImageDescriptor(ImageDescriptor.createFromImage(ImageProvider.IMG_LAUNCHER_SETTINGS));
		loadPreference();
		//StyleProvider.setSelectedFont(new FontData(fontName, fontHeight, fontStyle));
	}

	private void loadPreference() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ConfigPreferenceConstants.PREFERENCE_NODE);
		originalPort = port = preferences.get(ConfigPreferenceConstants.SERVER_PORT,
				ConfigPreferenceConstants.DEFAULT_PORT);

		

	}

	@Override
	public void addPages() {
		page = new ConfigWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		if (page.getErrorMessage() != null) {
			return false;
		}

		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ConfigPreferenceConstants.PREFERENCE_NODE);
		//preferences.put(ConfigPreferenceConstants.PREFERENCE_PATH, path);
	
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		if (!originalPort.equals(port)) {
			// CiteModelManager.getInstance().doSave();
			needsRestart = true;
		}
		setTheme();

		return true;
	}

	private void setTheme() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", theme);
		ParameterizedCommand cmd = commandService.createCommand(CommandConstants.SWTICH_THEME_COMMAND, parameters);
		handlerService.executeHandler(cmd);

	}

	public boolean needsRestart() {
		return needsRestart;
	}

	public String getFontName() {
		return fontName;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public int getFontHeight() {
		return fontHeight;
	}

	public void setFontHeight(int fontHeight) {
		this.fontHeight = fontHeight;

	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;

	}

	public void setFontName(String fontName) {
		this.fontName = fontName;

	}

	

	@Override
	public int open() {
		try {
			UAVWizardDialog dialog = new UAVWizardDialog(this);
			dialog.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.RESIZE | SWT.TITLE);

			return dialog.open();

		} catch (SWTException ex) {
			// catch swt exception when dirty closing the wizard...
			// LOGGER.warn(ex.getMessage());
			return SWT.ABORT;
		}
	}

	
	public void setTheme(String theme) {
		this.theme = theme;

	}

	public String getTheme() {
		return theme;
	}

	public String getPerspective() {
		return perspective;
	}

	public void setPerspective(String perspective) {
		this.perspective = perspective;

	}

	
}
