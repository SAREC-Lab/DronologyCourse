package edu.nd.dronology.ui.cc.main.editor.base;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import edu.nd.dronology.services.core.items.IPersistableItem;
import edu.nd.dronology.ui.cc.application.constants.CommandConstants;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.DronologyMainActivator;
import edu.nd.dronology.ui.cc.main.editor.specification.SpecificationEditor;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public abstract class AbstractUAVEditorPage<ITEM extends IPersistableItem> extends Composite implements IUAVEditorPage<ITEM> {

	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractUAVEditorPage.class);

	protected AbstractItemEditor<ITEM > editor;	

	private boolean init;
	protected AbstractMandatoryItemComposite<ITEM> mandatoryComposite;

	private Composite header;
	protected Label headerIcon;
	protected Label headerText;
	protected Composite container;

	public AbstractUAVEditorPage(AbstractItemEditor<ITEM> editor, String id, String title) {
		super(editor, SWT.FLAT);
		this.editor = editor;

		GridLayoutFactory.fillDefaults().applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		createFormContent(this);

	}

	protected void createFormContent(Composite parent) {

		createHeader(parent);
		doCreateLabel(parent);
		createSaveButton(parent);

		container = new Composite(parent, SWT.BORDER);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createMandatoryPart(container);

		createOptionalPart();

		doCreateAdditionalParts(container);

		// GridLayout layout = new GridLayout();
		// layout.marginTop = 5;
		// layout.marginBottom = 5;
		// layout.marginLeft = 10;
		// layout.marginRight = 10;
		// layout.horizontalSpacing = 10;
		// layout.verticalSpacing = 10;
		// form.getBody().setLayout(layout);

	}

	protected void createOptionalPart() {
		Group group = new Group(container, SWT.FLAT);
		group.setText("Optional Item Attributes");
		GridLayout gl_group = new GridLayout();
		gl_group.numColumns = 1;
		group.setLayout(gl_group);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		doCreateOptionalParts(group);

	}

	private void createHeader(Composite parent) {
		header = new Composite(parent, SWT.FLAT);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(5, 5).applyTo(header);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(header);
		headerIcon = new Label(header, SWT.FLAT);
		headerText = new Label(header, SWT.FLAT);

		headerText.setFont(StyleProvider.FONT_LUCID);

		ControlUtil.paintCustomBorder(header);

	}

	private void createSaveButton(Composite parent) {
		Button saveIcon = new Button(header, SWT.PUSH);
		saveIcon.setToolTipText("Save and Transmit to Server");
		saveIcon.setImage(ImageProvider.IMG_TRANSMIT_32);
		GridDataFactory.fillDefaults().grab(true, true).hint(60, SWT.DEFAULT).align(SWT.END, SWT.FILL).applyTo(saveIcon);
		saveIcon.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Map<String, Object> parameters = new HashMap<>();
				parameters.put("id", editor.getItem().getId());
				ParameterizedCommand command = DronologyMainActivator.getDefault().getCommandService()
						.createCommand(CommandConstants.TRANSMIT_COMMAND, parameters);
				DronologyMainActivator.getDefault().getHandlerService().executeHandler(command);

				editor.save();
			}
		});
	}

	protected abstract void doCreateLabel(Composite parent);

	private void createMandatoryPart(Composite container) {
		mandatoryComposite = doCreateMandatoryComposite(container);

	}

	@Override
	public void setDirty() {
		if (init) {
			return;
		}
		editor.setDirty(true);
	}

	private void setValues() {
		init = true;
		if (mandatoryComposite != null) {
			mandatoryComposite.setValues();
		}
		doSetValues();

		init = false;
	}

	@Override
	public AbstractItemEditor<ITEM> getEditor() {
		return editor;
	}

	@Override
	public ITEM getItem() {
		return editor.getItem();
	}

	public void notifyInputChange() {
		setValues();

	}
	
	
	public abstract void refresh();
	
	protected abstract void doSetValues();

	protected abstract AbstractMandatoryItemComposite doCreateMandatoryComposite(Composite container);

	protected abstract void doCreateOptionalParts(Composite container);

	protected abstract void doCreateAdditionalParts(Composite container);


}
