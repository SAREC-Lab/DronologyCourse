package edu.nd.dronology.ui.cc.main.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ControlUtil {
	

	public static void paintCustomBorder(final Control control, final int width, final int linestyle) {
		control.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				int h = control.getBounds().height;
				int w = control.getBounds().width;

				e.gc.setLineWidth(width);
				e.gc.setLineStyle(linestyle);

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

	public static void underline(boolean underline, StyledText text) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.underline = underline;
		styleRange.length = text.getText().length();
		text.setStyleRange(styleRange);
	}

	public static void paintCustomBorder(Control control) {
		paintCustomBorder(control, 1, SWT.LINE_DOT);

	}

	public static void setColor(Control... controls) {
		for (Control c : controls) {
			c.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	public static int CalculateFontWidth(Label l) {
		GC gc = new GC(l);
		gc.setFont(l.getFont());

		int stringWidth = gc.stringExtent(l.getText()).x;

		return stringWidth;

	}

	public static double getScaleFactor() {
		final AtomicInteger xscale = new AtomicInteger(0);
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				xscale.set(Display.getDefault().getDPI().x);

			}
		});
		double scale = 100 / 96d * xscale.get() / 100;
		// if(true){
		// return 1;
		// }
		return 1 + (scale - 1) * 0.8;
	}

	public static int scale(int position) {
		return (int) (getScaleFactor() * position);
	}

	public static int normalize(int position) {
		return (int) (position / (100 * getScaleFactor()) * 100);

	}

}
