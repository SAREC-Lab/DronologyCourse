package edu.nd.dronology.ui.cc.main.launcher;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import edu.nd.dronology.ui.cc.images.ImageProvider;
import edu.nd.dronology.ui.cc.images.StyleProvider;

public class Pushable extends Composite {

	private final Font FONT_BUTTON_SELECTED_BIG;
	private final Font FONT_BUTTON_BIG;

	private final Font FONT_BUTTON_SELECTED_SMALL;
	private final Font FONT_BUTTON_SMALL;

	private final Font FONT_SEGOE_BIG;
	private final Font FONT_SEGOE_SMALL;

	private static final Color COLOR_DISABLED = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	private String text;
	private Image enabled;
	private Image disabled;
	private Image hover;
	private Image currImage;
	private int indent;
	private Label lblImage;
	private boolean selected = false;
	private boolean currentState = false;
	private boolean isHover = false;
	private final boolean serviceAvailable;
	private Color backgroundColor;
	public static int TILE_HEIGHT = 220;
	private Canvas canvas;
	private String infoText = StringUtils.EMPTY;

	public Pushable(Composite parent, int indent) {
		// create an empty pushable
		super(parent, SWT.BORDER);
		this.indent = indent;
		this.serviceAvailable = false;

		setLayout(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	public Pushable(Composite parent, int indent, int style) {
		super(parent, style);
		this.indent = indent;
		this.serviceAvailable = false;

		setLayout(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	public Pushable(Composite parent, String text, Image enabled, Image hover, Image disabled, int indent,
			Color backgroundColor, boolean serviceAvailable) {
		super(parent, SWT.FLAT | SWT.INHERIT_FORCE);
		this.text = text;
		this.enabled = enabled;
		this.hover = hover;
		this.currImage = this.disabled = disabled;
		this.indent = indent;
		this.backgroundColor = backgroundColor;
		this.serviceAvailable = serviceAvailable;
		this.setBackgroundMode(SWT.INHERIT_DEFAULT);
		createContents();
	}

	public Pushable(Composite parent, String text, Image enabled, Image hover, Image disabled, int indent,
			Color backgroundColor) {
		this(parent, text, enabled, hover, disabled, indent, backgroundColor, true);
	}

	private void createContents() {
		setLayout(COLOR_DISABLED);
		String[] strings = StringUtils.split(text, "/");

		createLabels(strings);
		addListener();

	}

	private void setLayout(Color color) {
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).indent(0, indent)
				.hint((int) (TILE_HEIGHT * 1.08), TILE_HEIGHT).grab(false, false).applyTo(this);
		this.setBackground(color);
		GridLayoutFactory.fillDefaults().spacing(5, 5).extendedMargins(5, 5, 3, 3).applyTo(this);
	}

	private void addListener() {

		MouseListener listener = new MouseAdapter() {

			private boolean doubleClick;

			@Override
			public void mouseUp(MouseEvent e) {
				if (doubleClick) {
					return;
				}
				doMouseUp(e);
			}

			@Override
			public void mouseDown(MouseEvent e) {
				doubleClick = false;
				doMouseDown();

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				doubleClick = true;
			}

		};

		MouseTrackAdapter mouseTrackListener = new MouseTrackAdapter() {

			@Override
			public void mouseHover(MouseEvent e) {

				// if (!currentState || e.getSource() != canvas)
				// return;
				// isHover = true;
				// hoverInfo();
			}

			@Override
			public void mouseEnter(MouseEvent e) {
				super.mouseEnter(e);
				doMouseEnter();
			}

			@Override
			public void mouseExit(MouseEvent e) {
				super.mouseExit(e);
				doMouseExit(e);
			}

		};

		this.addMouseTrackListener(mouseTrackListener);
		lblImage.addMouseTrackListener(mouseTrackListener);
		canvas.addMouseTrackListener(mouseTrackListener);

		this.addMouseListener(listener);
		lblImage.addMouseListener(listener);
		canvas.addMouseListener(listener);

		this.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(Display.getDefault().getSystemColor(selected ? SWT.COLOR_BLACK : SWT.COLOR_BLACK));
				int h = Pushable.this.getBounds().height;
				int w = Pushable.this.getBounds().width;

				e.gc.setLineWidth(selected ? 3 : 1);
				e.gc.setLineStyle(SWT.LINE_SOLID);

				// top
				e.gc.drawLine(1, 1, 1, h - 1);
				// right
				e.gc.drawLine(w - 1, 1, w - 1, h - 1);
				// bottom
				e.gc.drawLine(1, 1, w - 1, 1);
				// left
				e.gc.drawLine(1, h - 1, w - 1, h - 1);
			}
		});

	}

	protected void hoverInfo() {
		this.setBackground(StyleProvider.COLOR_DARK_GREEN);
		setCurrentImage(null);

	}

	private void createLabels(final String[] strings) {
		lblImage = new Label(this, SWT.FLAT | SWT.TRANSPARENT);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).grab(true, true).indent(0, 0).applyTo(lblImage);

		setCurrentImage(disabled);

		canvas = new Canvas(this, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).hint(50, 65).applyTo(canvas);

		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				GridData gd = (GridData) Pushable.this.getLayoutData();
				int pushableHeight = gd.heightHint;
				int minSize = Math.min(gd.widthHint, gd.heightHint);

				// set the font
				if (selected) {
					e.gc.setFont(minSize > TILE_HEIGHT + 10 ? FONT_BUTTON_SELECTED_BIG
							: minSize < TILE_HEIGHT - 20 ? FONT_BUTTON_SELECTED_SMALL
									: StyleProvider.FONT_BUTTON_SELECTED);
				} else {
					e.gc.setFont(minSize > TILE_HEIGHT + 10 ? FONT_BUTTON_BIG
							: minSize < TILE_HEIGHT - 20 ? FONT_BUTTON_SMALL : StyleProvider.FONT_BUTTON);
				}

				// draw the text
				if (isHover) {
					GridDataFactory.fillDefaults().grab(true, false).hint(50, pushableHeight - 30).applyTo(canvas);
					Pushable.this.layout();
					int canvasWidth = canvas.getSize().x;
					int canvasHeight = canvas.getSize().y;

					e.gc.setFont(minSize > TILE_HEIGHT + 10 ? FONT_SEGOE_BIG
							: minSize < TILE_HEIGHT - 20 ? FONT_SEGOE_SMALL : StyleProvider.FONT_SEGOE_10);

					e.gc.setForeground(StyleProvider.COLOR_LIGHT_ORANGE);
					Image img = ImageProvider.IMG_LAUNCHER_INFO;
					// calculate the position to get the img. and text in the
					// middle
					Point txtSize = e.gc.textExtent(infoText);
					int xImg = canvasWidth / 2 - img.getImageData().width / 2;
					int xTxt = canvasWidth / 2 - txtSize.x / 2;
					int y = canvasHeight / 2 - (txtSize.y + img.getImageData().height) / 2;

					e.gc.drawImage(img, xImg, y);
					y += img.getImageData().height;
					e.gc.drawText(infoText, xTxt, y);

				} else {
					GridDataFactory.fillDefaults().grab(true, false).hint(50, (int) (pushableHeight / 3.5))
							.applyTo(canvas);
					Pushable.this.layout();
					int canvasWidth = canvas.getSize().x;
					// canvas.setBackground(COLOR_DISABLED);
					int fontHeight = e.gc.getFontMetrics().getHeight();
					int fontDescender = e.gc.getFontMetrics().getDescent();
					int textWidth1 = e.gc.stringExtent(strings[0]).x;
					int textWidth2 = e.gc.stringExtent(strings.length > 1 ? strings[1] : "").x;
					int yText = fontDescender + (selected ? 3 : -2);
					e.gc.drawText(strings[0], canvasWidth / 2 - textWidth1 / 2, yText);
					yText += fontHeight;
					e.gc.drawText(strings.length > 1 ? strings[1] : "", canvasWidth / 2 - textWidth2 / 2, yText);
				}

			}
		});

	}

	protected void doMouseDown() {
		isHover = false;
		if (!currentState)
			return;
		this.setBackground(StyleProvider.COLOR_CLICK);
		this.layout();
	}

	protected void doMouseExit(MouseEvent event) {
		isHover = false;
		if (!currentState)
			return;
		canvas.redraw();
		this.setBackground(backgroundColor);
		setCurrentImage(enabled);
		selected = false;
		this.layout();
	}

	protected void doMouseUp(MouseEvent e) {
		isHover = false;
		if (!currentState)
			return;
		this.setBackground(backgroundColor);
		this.layout();

		if (e.getSource() != this) {
			Event event = new Event();
			event.type = SWT.MouseUp;
			event.widget = this;
			this.notifyListeners(SWT.MouseUp, event);
		}
	}

	protected void doMouseEnter() {
		isHover = false;
		if (!currentState)
			return;
		this.setBackground(StyleProvider.COLOR_BUTTON_SELECTED);
		setCurrentImage(hover);
		selected = true;
		this.layout();
	}

	public void setState(boolean state) {
		if (state == currentState || !serviceAvailable) {
			return;
		}
		currentState = state;
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				setCurrentImage(currentState ? enabled : disabled);
				Pushable.this.setBackground(currentState ? backgroundColor : COLOR_DISABLED);
				Pushable.this.setEnabled(currentState);
				Pushable.this.layout();
			}
		});

	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public void setDefaultSize() {

		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).indent(0, indent)
				.hint((int) (TILE_HEIGHT * 1.08), TILE_HEIGHT).grab(false, false).applyTo(this);
		if (lblImage == null) {
			return;
		}
		lblImage.setImage(resizeImage(currImage, currImage.getImageData().width, currImage.getImageData().height));
	}

	public void resize(int width, int height) {
		// set a min. size
		if (width < TILE_HEIGHT / 1.5)
			width = (int) (TILE_HEIGHT / 1.5);
		if (height < TILE_HEIGHT / 1.5)
			height = (int) (TILE_HEIGHT / 1.5);

		// adjust the pushable component
		GridData d = (GridData) this.getLayoutData();
		if (d == null)
			return;

		d.heightHint = height;
		d.widthHint = width;

		// adjust image size
		if (lblImage != null) {
			if (currImage != null) {
				double imgHeight, imgWidth;

				// image should be max. 1/2 of the components smallest side
				if (height < width) {
					imgHeight = height / 2;
					double imageWidthFactor = currImage.getImageData().width / currImage.getImageData().height;
					imgWidth = imgHeight * imageWidthFactor;
				} else {
					imgWidth = width / 2;
					double imageHeightFactor = currImage.getImageData().height / currImage.getImageData().width;
					imgHeight = imgWidth * imageHeightFactor;
				}

				// if the hover image is set, make the image bigger!
				if (currImage == hover) {
					imgWidth *= 1.2;
					imgHeight *= 1.2;
				}
				// dispose the old image
				if (lblImage.getImage() != null)
					lblImage.getImage().dispose();
				lblImage.setImage(resizeImage(currImage, (int) imgWidth, (int) imgHeight));
			} else {
				lblImage.setImage(null);
			}
		}
		// text size will be adjusted automatically on a repaint

	}

	private Image resizeImage(Image oldImage, int newWidth, int newHeight) {
		// System.out.println("resize");
		// ImageData id = oldImage.getImageData();
		// oldImage.getImageData().height=newHeight;
		// oldImage.getImageData().width=newWidth;
		Image scaled = new Image(Display.getDefault(), newWidth, newHeight);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		// gc.setAlpha(0);
		// gc.setBackground(this.getBackground());
		gc.drawImage(oldImage, 0, 0, oldImage.getImageData().width, oldImage.getImageData().height, 0, 0, newWidth,
				newHeight);
		gc.dispose();

		ImageData id = scaled.getImageData();
		scaled.dispose();
		final Image newImage = new Image(getDisplay(), id, id); // 3rd parameter
																// is
																// transparency
																// mask

		// //new
		// ImageData data = scaled.getImageData();
		//
		// if(backgroundColor == null){
		//
		// backgroundColor = this.getBackground();
		//
		// }
		//
		// data.transparentPixel = data.palette.getPixel(new RGB(255,255,255));
		// //dispose the intransparent scaled image
		// scaled.dispose();
		// Image scaledTransparent=new Image(Display.getCurrent(),data);

		return newImage;

		// Canvas c = new Canvas (getShell(), SWT.TRANSPARENT);
		// c.addPaintListener(
		// new PaintListener(){
		// @Override
		// public void paintControl(PaintEvent e)
		// {
		// e.gc.drawImage(newImage, 0, 0);
		// }
		// }
		// );

		// //the image has been created, with transparent regions. Now set the
		// active region
		// //so that mouse click (enter, exit etc) events only fire when they
		// occur over
		// //visible pixels. If you're not worried about this ignore the code
		// that follows
		// Region region = new Region();
		// Rectangle pixel = new Rectangle(0, 0, 1, 1);
		// for (int y = 0; y < id.height; y++)
		// {
		// for (int x = 0; x < id.width; x++)
		// {
		// if (id.getAlpha(x,y) > 0)
		// {
		// pixel.x = id.x + x;
		// pixel.y = id.y + y;
		// region.add(pixel);
		// }
		// }
		// }
		// c.setRegion(region);

		// return newImage;

		// // return new Image(getDisplay(),
		// image.getImageData().scaledTo(width, height));

		// Image scaled = new Image(Display.getDefault(), newWidth, newHeight);
		// GC gc = new GC(scaled);
		// gc.setAntialias(SWT.ON);
		// gc.setInterpolation(SWT.HIGH);
		// // gc.setAlpha(0);
		// gc.setBackground(this.getBackground());
		// gc.drawImage(oldImage, 0, 0,
		// oldImage.getBounds().width, oldImage.getBounds().height,
		// 0, 0, newWidth, newHeight);
		// gc.dispose();
		// //new
		// ImageData data = scaled.getImageData();
		//
		// if(backgroundColor == null){
		//
		// backgroundColor = this.getBackground();
		//
		// }
		//
		// data.transparentPixel = data.palette.getPixel(new RGB(255,255,255));
		// //dispose the intransparent scaled image
		// scaled.dispose();
		// Image scaledTransparent=new Image(Display.getCurrent(),data);
		// return scaledTransparent;
		// // return scaled;
	}

	public void setPushableSize(int width, int height) {
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).indent(0, indent)
				.hint((int) (height * 1.08), height).grab(false, false).applyTo(this);
	}

	private void setCurrentImage(Image img) {
		currImage = img;
		// redraw the component
		GridData d = (GridData) this.getLayoutData();
		int height = d.heightHint;
		int width = d.widthHint;

		resize(width, height);
	}

	// Create the fonts needed for painting the text
	{
		FontData[] fdBSB = StyleProvider.FONT_BUTTON_SELECTED.getFontData();
		FontData[] fdBB = StyleProvider.FONT_BUTTON.getFontData();

		// create the big fonts
		for (int i = 0; i < fdBSB.length; ++i) {
			fdBSB[i].setHeight(18);
		}
		for (int i = 0; i < fdBB.length; ++i) {
			fdBB[i].setHeight(18);
		}
		FONT_BUTTON_SELECTED_BIG = new Font(getDisplay(), fdBSB);
		FONT_BUTTON_BIG = new Font(getDisplay(), fdBB);

		// create the small fonts
		for (int i = 0; i < fdBSB.length; ++i) {
			fdBSB[i].setHeight(10);
		}
		for (int i = 0; i < fdBB.length; ++i) {
			fdBB[i].setHeight(10);
		}
		FONT_BUTTON_SELECTED_SMALL = new Font(getDisplay(), fdBSB);
		FONT_BUTTON_SMALL = new Font(getDisplay(), fdBB);

		// create the tooltip fonts
		FontData[] fdSTT = StyleProvider.FONT_SEGOE_10.getFontData();

		for (int i = 0; i < fdSTT.length; ++i) {
			fdSTT[i].setHeight(7);
		}
		FONT_SEGOE_SMALL = new Font(getDisplay(), fdSTT);

		for (int i = 0; i < fdSTT.length; ++i) {
			fdSTT[i].setHeight(12);
		}
		FONT_SEGOE_BIG = new Font(getDisplay(), fdSTT);

		// dispose them if necessary
		this.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				FONT_BUTTON_SELECTED_BIG.dispose();
				FONT_BUTTON_BIG.dispose();
				FONT_BUTTON_SMALL.dispose();
				FONT_BUTTON_SELECTED_SMALL.dispose();
				FONT_SEGOE_BIG.dispose();
				FONT_SEGOE_SMALL.dispose();
			}
		});
	}
}
