package com.cgvsu.rasterization;

import com.cgvsu.math.Global;
import com.cgvsu.zbuffer.ZBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;

public class Line {

	public static void drawLine(GraphicsContext gc, double x00, double y00, double x11, double y11, double z00,
			double z11, ZBuffer zBuffer) {
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
				putPxl(x, y, x00, x11, z00, z11, zBuffer, pw);
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
				putPxl(x, y, x00, x11, z00, z11, zBuffer, pw);
				error += deltaerr;

				if (error >= (deltay + 1)) {
					x += dirx;
					error -= (deltay + 1);
				}
			}
		}
	}

	private static void putPxl(int x, int y, double x00, double x11, double z00, double z11, ZBuffer zBuffer,
			PixelWriter pw) {
		double z = findZ(x, x00, x11, z00, z11) - 200 * Global.eps;
		if (RasterizationParams.zBuffer.inZBuffer(x, y)) {
			if (z - zBuffer.get(x, y) <= Global.eps) {
				pw.setColor(x, y, RasterizationParams.getLineColor());
				zBuffer.set(x, y, z);
			}
		}
	}

	private static double findZ(double x, double x0, double x1, double z0, double z1) {
		return (x - x0) * (z1 - z0) / (x1 - x0) + z0;
	}
}
