package edu.nd.dronology.ui.cc.main.editor.base;

import edu.nd.dronology.services.core.items.IPersistableItem;

public interface IUAVEditorPage<ITEM extends IPersistableItem> {

	void setDirty();
	ITEM getItem();
	AbstractItemEditor<ITEM> getEditor();

}
