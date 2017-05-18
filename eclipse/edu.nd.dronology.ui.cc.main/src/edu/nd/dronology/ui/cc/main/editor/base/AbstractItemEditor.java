package edu.nd.dronology.ui.cc.main.editor.base;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.services.core.items.IPersistableItem;
import edu.nd.dronology.services.core.persistence.AbstractItemPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public abstract class AbstractItemEditor<T extends IPersistableItem> extends Composite {

	private final AbstractItemPersistenceProvider<T> PERSISTOR;
	private T input;
	private IFile file;
	private AbstractUAVEditorPage<T> page;
	private boolean dirty;

	public AbstractItemEditor(Composite parent) {
		super(parent, SWT.FLAT);
		PERSISTOR = getPersistor();
		createContents();
		ControlUtil.setColor(this);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
		setVisible(false);

	}

	public abstract AbstractItemPersistenceProvider<T> getPersistor();
	
	public abstract AbstractUAVEditorPage<T> createPage();

	private void createContents() {
		page = createPage();

	}

	

	public void setInput(RemoteInfoObject elem) {
		retrieveFile(elem);
		loadContent();

		setVisible(true);
		page.notifyInputChange();
	}

	public abstract void retrieveFile(RemoteInfoObject elem);

	private void loadContent() {
		try {
			input = PERSISTOR.loadItem(file.getContents());
		} catch (CoreException | PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void close() {
		save();
		setVisible(false);
	}

	public void save() {
		try {
			PERSISTOR.saveItem(input, file.getRawLocation().toOSString());
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doTransmit();
	}

	public abstract void doTransmit();

	public void setDirty(boolean isDirty) {
		this.dirty = isDirty;

	}

	public T getItem() {
		return input;
	}

	public void setInputFile(IFile file) {
		this.file = file;

	}

	public IFile getInputFile() {
		return file;
	}

	public byte[] getItemAsByteArray() {
		InputStream stream = null;
		try {
			file.getParent().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			stream = file.getContents();
			return IOUtils.toByteArray(stream);
		} catch (IOException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
