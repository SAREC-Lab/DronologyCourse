package edu.nd.dronology.ui.cc.main.unused;
//package edu.nd.dronology.ui.cc.main.flightplan;
//
//import org.eclipse.jface.layout.GridDataFactory;
//import org.eclipse.jface.layout.GridLayoutFactory;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.browser.Browser;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//
//import net.mv.logging.ILogger;
//import net.mv.logging.LoggerProvider;
//
//public class BrowserViewer extends Composite {
//
//	private static final ILogger LOGGER = LoggerProvider.getLogger(BrowserViewer.class);
//
//	private Browser webBrowser;
//	public static final String DEFAULT_URL = "about:blank";
//
//	public BrowserViewer(Composite parent) {
//		super(parent, SWT.FLAT);
//		GridLayoutFactory.fillDefaults().applyTo(this);
//		GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
//
//		createContents();
//
//	}
//
//	private void createContents() {
//		createViewer(this);
//
//	}
//
//	private void createViewer(Composite parent) {
//		webBrowser = new Browser(parent, SWT.FLAT);
//		GridData grid = new GridData(GridData.FILL_BOTH);
//		webBrowser.setLayoutData(grid);
//		webBrowser.setUrl(DEFAULT_URL);
//
//	}
//
//	@Override
//	public void setVisible(boolean visible) {
//		webBrowser.setUrl(DEFAULT_URL);
//		super.setVisible(visible);
//		((GridData) getLayoutData()).exclude = !visible;
//		getParent().layout();
//
//	}
//
//	public void setInput(String browserString) {
//		
//		String txt =	"<html><body><iframe src=\" %s\"  width=\"1300\" height=\"900\"></iframe></body></html>";
//
//		
//		Display.getDefault().asyncExec(()->{
//			try{
//				
//				webBrowser.setText(String.format(txt, browserString));
//				
//			//webBrowser.setUrl(browserString);
//			}catch(Throwable t){
//				LOGGER.error(t);
//			}
//		});
//		
//		
//	}
//}
