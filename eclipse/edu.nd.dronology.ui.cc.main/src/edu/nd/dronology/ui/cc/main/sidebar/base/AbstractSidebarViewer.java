package edu.nd.dronology.ui.cc.main.sidebar.base;

import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.themes.ColorUtil;

import edu.nd.dronology.services.core.info.RemoteInfoObject;
import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;
import edu.nd.dronology.ui.cc.main.util.ControlUtil;

public abstract class AbstractSidebarViewer<T> extends Composite {

	private MyShelf shelf;
	protected List<TreeViewer> viewerList = new ArrayList<>();
	private Map<T, MyShelfItem> items = new HashMap<>();
	protected Form form;
	private Composite body;
	protected Image shelfItemIcon = ImageProvider.IMG_ARROW_FIRST_24;
	protected Color gradient1 = null;
	protected Color gradient2 = null;
	protected Color selectedGradient1 = null;
	protected Color selectedGradient2 = null;

	protected String title;
	private Composite toolbar;

	public AbstractSidebarViewer(Composite parent, String title) {
		super(parent, SWT.FLAT);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).applyTo(this);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		this.title = title;
	}

	public void createContents() {

		toolbar = new Composite(this, SWT.BORDER);
		GridLayoutFactory.fillDefaults().numColumns(10).margins(5, 5).applyTo(toolbar);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(toolbar);

		body = new Composite(this, SWT.BORDER);
		GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(body);

		createToolbar(toolbar);

		createShelf();
		ControlUtil.setColor(toolbar, this, body);
		refreshCategories(true);

		// shelf.layout();
		// this.layout();
		shelf.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				// System.out.println("RESIZE");
				// shelf.computeItemHeight();
				// resetSelection();
			}
		});
		// form.reflow(true);
		// form.layout();
	}

	public void refreshFully(final boolean animation) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				refreshCategories(animation);

				for (TreeViewer viewer : viewerList) {
					viewer.refresh();
				}

			}
		});

	}

	private void createShelf() {

		shelf = new MyShelf(body, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(shelf);
		shelf.setLayoutData(new GridData(GridData.FILL_BOTH));
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		MyRenderer renderer = getRenderer();
		shelf.setRenderer(renderer);

		renderer.setFont(StyleProvider.FONT_SMALL);
		renderer.setSelectedFont(StyleProvider.FONT_SMALL);
		renderer.setLineColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

		if (gradient1 != null)
			renderer.setGradient1(gradient1);
		if (gradient2 != null)
			renderer.setGradient2(gradient2);
		if (selectedGradient1 != null)
			renderer.setSelectedGradient1(selectedGradient1);
		if (selectedGradient2 != null)
			renderer.setSelectedGradient2(selectedGradient2);

		shelf.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		body.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

		// shelf = new PShelf(body, SWT.NONE);
		// GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(shelf);
		// shelf.setLayoutData(new GridData(GridData.FILL_BOTH));
		// body.setLayoutData(new GridData(GridData.FILL_BOTH));
		// form.setLayoutData(new GridData(GridData.FILL_BOTH));
		// RedmondShelfRenderer renderer = getRenderer();
		// shelf.setRenderer(renderer);
		//
		// renderer.setFont(StyleProvider.FONT_SMALL);
		// renderer.setSelectedFont(StyleProvider.FONT_SMALL);
		// renderer.setLineColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		//
		// renderer.setGradient1(StyleProvider.TURQUOISE);
		// renderer.setGradient2(StyleProvider.DARK_GRAY);
		// renderer.setSelectedGradient1(StyleProvider.RED_ORANGE);
		// renderer.setSelectedGradient2(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//
		// shelf.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		// body.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		// setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

	}

	// private RedmondShelfRenderer getRenderer() {
	// RedmondShelfRenderer renderer = new RedmondShelfRenderer();
	// renderer.setFont(StyleProvider.FONT_SMALL);
	// renderer.setLineColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	// renderer.setGradient1(Display.getDefault().getSystemColor(SWT.COLOR_RED));
	//
	// return renderer;
	// }

	private MyRenderer getRenderer() {
		MyRenderer renderer = new MyRenderer();
		renderer.setFont(StyleProvider.FONT_SMALL);
		renderer.setLineColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		renderer.setGradient1(Display.getDefault().getSystemColor(SWT.COLOR_RED));

		return renderer;
	}

	// private void createShelfItem(T scen) {
	// PShelfItem shelItem = new PShelfItem(shelf, SWT.NONE);
	// items.add(shelItem);
	// shelItem.setImage(shelfItemIcon);
	// shelItem.getBody().setLayout(new FillLayout());
	//
	// shelItem.setText(getShelfItemName(scen));
	// createViewer(shelItem, scen);
	// shelf.setSelection(shelItem);
	// shelItem.getBody().redraw();
	// }

	private void createShelfItem(T scen, boolean animation) {
		MyShelfItem shelItem = new MyShelfItem(shelf, SWT.NONE);
		items.put(scen, shelItem);
		// firstItem = shelItem;
		shelItem.setImage(shelfItemIcon);
		shelItem.getBody().setLayout(new FillLayout());
		// shelItem.getBody().setLayoutData(new Fill(GridData.FILL_BOTH));
		// shelItem.getBody().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		shelItem.setText(getShelfItemName(scen));
		shelItem.setText2("(" + getItemCount(scen) + ")");
		createViewer(shelItem, scen);
		if (animation) {
			shelf.setSelection(shelItem);
			shelItem.getBody().redraw();
		}
	}

	protected abstract String getItemCount(T scen);

	private void createViewer(MyShelfItem shelItem, T cat) {
		TreeViewer treeViewer = new TreeViewer(shelItem.getBody(), SWT.BORDER | SWT.FULL_SELECTION);
		Tree tree = treeViewer.getTree();

		tree.setLayout(new FillLayout());
		treeViewer.setContentProvider(getContentProvider());

		treeViewer.setLabelProvider(getLabelProvider());

		treeViewer.setInput(cat);

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				doOpen(event.getSelection());

			}
		});
		treeViewer.expandAll();
		ColumnViewerToolTipSupport.enableFor(treeViewer);
		viewerList.add(treeViewer);
		hookContextMenu(treeViewer);
	}

	// private void createViewer(PShelfItem shelItem, T cat) {
	// TreeViewer treeViewer = new TreeViewer(shelItem.getBody(), SWT.BORDER |
	// SWT.FULL_SELECTION);
	// Tree tree = treeViewer.getTree();
	//
	// tree.setLayout(new FillLayout());
	// treeViewer.setContentProvider(getContentProvider());
	//
	// treeViewer.setLabelProvider(getLabelProvider());
	//
	// treeViewer.setInput(cat);
	//
	// treeViewer.addDoubleClickListener(new IDoubleClickListener() {
	//
	// @Override
	// public void doubleClick(DoubleClickEvent event) {
	// doOpen(event.getSelection());
	// }
	// });
	// treeViewer.expandAll();
	// hookContextMenu(treeViewer);
	// viewerList.add(treeViewer);
	// }

	private void hookContextMenu(final TreeViewer viewer) {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (viewer.getSelection() instanceof StructuredSelection) {
					Object elem = ((StructuredSelection) viewer.getSelection()).getFirstElement();
					if (elem instanceof RemoteInfoObject) {
						fillContextMenu((RemoteInfoObject) elem, manager);
					}
				}

			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	protected abstract void fillContextMenu(RemoteInfoObject remoteItem, IMenuManager manager);

	public void refresh() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				updateCount();
				for (TreeViewer viewer : viewerList) {
					viewer.refresh();
				}

			}
		});

	} 

	protected void refreshCategories(boolean animation) {
		if (shelf.isDisposed()) {
			return;
		}
		synchronized (items) {
			for (MyShelfItem item : items.values()) {
				item.dispose();
				// shelf.removeItem(item);
			}
			for (TreeViewer viewer : viewerList) {
				viewer.getTree().dispose();
			}

			items.clear();
			viewerList.clear();

			for (T item : getShelfItems()) {
				createShelfItem(item, animation);
			}
		}
	}

	protected void updateCount() {
		for (Entry<T, MyShelfItem> item : items.entrySet()) {
			item.getValue().setText2("(" + getItemCount(item.getKey()) + ")");
		}
		shelf.redraw();

	}

	protected abstract IBaseLabelProvider getLabelProvider();

	protected abstract IContentProvider getContentProvider();

	protected abstract void createToolbar(Composite toolbar);

	protected abstract String getShelfItemName(T scen);

	// protected abstract void fillContextMenu(Viewer viewer, IMenuManager
	// manager);

	protected abstract Collection<T> getShelfItems();

	protected abstract void doOpen(ISelection selection);

}
