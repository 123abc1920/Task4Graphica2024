package com.cgvsu.rasterization;

import com.cgvsu.math.Global;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Line {

	public static void drawLine(GraphicsContext gc, double x00, double y00, double x11, double y11) {
		PixelWriter pw = gc.getPixelWriter();

		int x0 = (int) Math.round(x00);
		int x1 = (int) Math.round(x11);
		int y0 = (int) Math.round(y00);
		int y1 = (int) Math.round(y11);

		int deltax = Math.abs(x1 - x0);
		int deltay = Math.abs(y1 - y0);

		int error = 0;
		int deltaerr;
		
		int dirx = (x0 < x1) ? 1 : -1;
		int diry = (y0 < y1) ? 1 : -1;

		if (deltax >= deltay) {
			int y = y0;
			deltaerr = (deltay + 1);

			for (int x = x0; x != x1; x += dirx) {
				pw.setColor(x, y, Color.DARKSLATEBLUE);
				error += deltaerr;

				if (error >= (deltax + 1)) {
					y += diry;
					error -= (deltax + 1);
				}
			}
		} else {
			int x = x0;
			deltaerr = (deltax + 1);

			for (int y = y0; y != y1; y += diry) {
				pw.setColor(x, y, Color.DARKSLATEBLUE);
				error += deltaerr;

				if (error >= (deltay + 1)) {
					x += dirx;
					error -= (deltay + 1);
				}
			}
		}
	}
}
