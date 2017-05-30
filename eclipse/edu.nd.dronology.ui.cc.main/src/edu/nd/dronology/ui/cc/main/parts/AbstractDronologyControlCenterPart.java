package edu.nd.dronology.ui.cc.main.parts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import edu.nd.dronology.ui.cc.application.constants.CommandConstants;
import edu.nd.dronology.ui.cc.application.constants.PerspectiveConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.api.ICommandExecutor;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;
import edu.nd.dronology.ui.cc.util.controls.ControlCreationHelper;
import net.mv.logging.LoggerProvider;

@SuppressWarnings("restriction")
public abstract class AbstractDronologyControlCenterPart implements ICommandExecutor {
	private static final net.mv.logging.ILogger LOGGER = LoggerProvider.getLogger(AbstractDronologyControlCenterPart.class);

	@Inject
	protected ECommandService commandService;
	@Inject
	protected EHandlerService handlerService;

	@Inject
	MWindow window;
	@Inject
	EPartService partService;
	@Inject
	EModelService modelService;
	@Inject
	MApplication application;
	
	
	@Inject
	protected ESelectionService selectionService;
	protected EMenuService menuService;
	//private IEntryChangeListener listener;

	private Composite toolbar;

	// private static IPartListener partListener;
	// private static Menu mainMenu;

	@PostConstruct
	public void createContents(Composite parent, EMenuService menuService) {
		this.menuService = menuService;



		GridLayoutFactory.fillDefaults().extendedMargins(10, 10, 5, 5).applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);

		createToolBar(parent);
		doCreateAdditionalToolItems(toolbar);


		ControlUtil.setColor(toolbar, parent);
		doCreateContents(parent);
		toolbar.setBackground(StyleProvider.COLOR_DISABLED);

//		Button btnAbout = new Button(toolbar, SWT.PUSH);
//		btnAbout.setImage(ImageProvider.IMG_MENU_ABOUT_24);
//		btnAbout.setToolTipText("About");
//		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(btnAbout);
//		btnAbout.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Map<String, Object> parameters = new HashMap<String, Object>();
//				ParameterizedCommand cmd = commandService.createCommand(CommandConstants.SHOW_ABOUT_COMMAND,
//						parameters);
//				handlerService.executeHandler(cmd);
//			}
//		});

	}

	private void createConsistencyMenu(Composite toolbar) {
		Button btnConsistency = new Button(toolbar, SWT.MENU);
		btnConsistency.setText("Consistency");
		btnConsistency.setToolTipText("Check Consistency of Publication Entries");

		btnConsistency.setImage(ImageProvider.IMG_ARROW_DOWN_24);
		btnConsistency.setFont(StyleProvider.getSelectedFont(btnConsistency));
		btnConsistency.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unused")
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);

				Menu menu = new Menu(btnConsistency.getShell(), SWT.POP_UP);
				menu.setData(StyleProvider.CSS_TAG, "Menu");

//				MenuCreationHelper.createMenuEntry(menu, "Author Name Consistency (Crtl+Shift+A)",
//						ImageProvider.IMG_CHECK_AUTHOR_24, () -> checkAuthorNameConsistency());


				Point loc = btnConsistency.getLocation();
				Rectangle rect = btnConsistency.getBounds();
				Point mLoc = new Point(loc.x - 1, loc.y + rect.height);
				menu.setLocation(btnConsistency.getDisplay().map(btnConsistency.getParent(), null, mLoc));
				menu.setVisible(true);
			}
		});

	}

	private void createToolBar(Composite parent) {
		toolbar = new Composite(parent, SWT.FLAT);
		toolbar.setData(StyleProvider.CSS_TAG, "MainMenu");
		ControlUtil.paintCustomBorder(toolbar);
		GridLayoutFactory.fillDefaults().numColumns(8).extendedMargins(10, 10, 10, 10).applyTo(toolbar);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(toolbar);

		ControlCreationHelper.createButton(toolbar, null, "Main Menu", ImageProvider.IMG_MENU_HOME_24,
				() -> homeCommand());


	}


	private void homeCommand() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("id", PerspectiveConstants.LAUNCHER_PERSPECTIVE);

		ParameterizedCommand cmd = commandService.createCommand(CommandConstants.SWTICH_PERSPECTIVE_COMMAND,
				parameters);
		handlerService.executeHandler(cmd);
	}

	protected void doCreateAdditionalToolItems(Composite toolbar) {
	}

	protected Composite createComposite(Composite parent, int style) {
		Composite comp = new Composite(parent, style);
		GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(1, 1, 1, 1).applyTo(comp);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(comp);
		return comp;
	}

	protected Composite createComposite(Composite parent) {
		return createComposite(parent, SWT.FLAT);
	}

	

	

	@Override
	public void switchPerspective(String perspectiveId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", perspectiveId);
		ParameterizedCommand cmd = commandService.createCommand(CommandConstants.SWTICH_PERSPECTIVE_COMMAND,
				parameters);
		handlerService.executeHandler(cmd);
	}






	protected abstract void doCreateContents(Composite parent);

	@PreDestroy
	public void preDestroy() {
		//LOGGER.info("View '" + getClass() + "' disposed");

	}

}
