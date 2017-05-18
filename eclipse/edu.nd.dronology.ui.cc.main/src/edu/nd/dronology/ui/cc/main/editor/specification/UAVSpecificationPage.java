package edu.nd.dronology.ui.cc.main.editor.specification;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.items.IDroneSpecification;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractMandatoryItemComposite;
import edu.nd.dronology.ui.cc.main.editor.base.AbstractUAVEditorPage;
import edu.nd.dronology.ui.cc.main.editor.base.IUAVEditorPage;
import edu.nd.dronology.ui.cc.util.controls.ControlCreationHelper;
import edu.nd.dronology.ui.cc.util.managedcontrol.ManagedText;

public class UAVSpecificationPage extends AbstractUAVEditorPage<IDroneSpecification>
		implements IUAVEditorPage<IDroneSpecification> {

	private ManagedText txtDesc;

	public UAVSpecificationPage(SpecificationEditor editor, String id, String title) {
		super(editor, id, title);

	}

	@Override
	protected void doCreateLabel(Composite parent) {
		headerText.setText("Specification Details");
		headerIcon.setImage(ImageProvider.IMG_SPECIFICATION_24);
	}

	@Override
	protected void doSetValues() {
		txtDesc.setValue(editor.getItem().getDescription());
	}

	@Override
	protected AbstractMandatoryItemComposite doCreateMandatoryComposite(Composite parent) {
		return new MandatorySpecificationDataComposite(parent, this);
	}

	@Override
	protected void doCreateOptionalParts(Composite container) {

		txtDesc = ControlCreationHelper.createMultiLineTextWitLabel(container, "Description", 2, SWT.BORDER, null);

		txtDesc.addApplyListener((value) -> {
			getItem().setDescription(value);
		});

	}

	@Override
	protected void doCreateAdditionalParts(Composite container) {
	}

	@Override
	public void layout() {
		container.layout();

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

}
