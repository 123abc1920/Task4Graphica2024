package com.cgvsu.rasterization;

import com.cgvsu.light.Light;
import com.cgvsu.math.Vector3f;
import com.cgvsu.zbuffer.ZBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class TriangleRasterization {
	public static boolean fillTriangle(final GraphicsContext graphicsContext, final double[] arrX, final double[] arrY,
			final double[] arrZ, final Color[] colors, Vector3f[] normals, Vector3f polyNormal, ZBuffer zBuffer,
			Vector3f cameraPos, boolean isLight) {

		final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

		sort(arrX, arrY, arrZ, normals);

		if ((arrX[0] * (arrY[1] - arrY[2]) + arrX[1] * (arrY[2] - arrY[0]) + arrX[2] * (arrY[0] - arrY[1])) == 0) {
			return false;
		}

		if (arrX.length < 3 || arrY.length < 3 || arrZ.length < 3 || colors.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		for (int y = (int) Math.ceil(arrY[1]); y <= (int) Math.floor(arrY[2]); y++) {
			final double x1 = (arrY[2] - arrY[1] == 0) ? arrX[1]
					: (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				putPxl(x, y, arrX, arrY, arrZ, colors[0], normals, cameraPos, polyNormal, pixelWriter);
			}
		}

		for (int y = (int) Math.floor(arrY[1]); y >= Math.ceil(arrY[0]); y--) {
			final double x1 = (arrY[1] - arrY[0] == 0) ? arrX[0]
					: (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				putPxl(x, y, arrX, arrY, arrZ, colors[0], normals, cameraPos, polyNormal, pixelWriter);
			}
		}

		return true;
	}

	private static void putPxl(int x, int y, double[] arrX, double[] arrY, double[] arrZ, Color c, Vector3f[] normals,
			Vector3f cameraPos, Vector3f polyNormal, PixelWriter pixelWriter) {
		double[] barizenticCoordinate = BarycentricCoordinate.barizentricCalculator(x, y, arrX, arrY);
		double z = (double) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
				+ barizenticCoordinate[2] * arrZ[2]);
		if (RasterizationParams.zBuffer.inZBuffer(x, y)) {
			if (z < RasterizationParams.zBuffer.get((int) x, (int) y)) {
				Color dotColor = getDotColor(c, normals, barizenticCoordinate, cameraPos.sub(new Vector3f(x, y, z)),
						polyNormal, RasterizationParams.getIsLight());
				pixelWriter.setColor((int) x, (int) y, dotColor);
				RasterizationParams.zBuffer.set((int) x, (int) y, z);
				RasterizationParams.drawBuffer.set(x, y, c);
			}
		}
	}

	private static Color getDotColor(Color c, Vector3f[] normals, double[] barizenticCoordinate, Vector3f toCamera,
			Vector3f polyNormal, boolean isLight) {
		if (!isLight) {
			return c;
		}

		Vector3f dotNormal = new Vector3f();
		dotNormal.setX(normals[0].getX() * barizenticCoordinate[0] + normals[1].getX() * barizenticCoordinate[1]
				+ normals[2].getX() * barizenticCoordinate[2]);
		dotNormal.setY(normals[0].getY() * barizenticCoordinate[0] + normals[1].getY() * barizenticCoordinate[1]
				+ normals[2].getY() * barizenticCoordinate[2]);
		dotNormal.setZ(normals[0].getZ() * barizenticCoordinate[0] + normals[1].getZ() * barizenticCoordinate[1]
				+ normals[2].getZ() * barizenticCoordinate[2]);
		dotNormal = dotNormal.normalize();
		return Light.newRgb(c, dotNormal, toCamera, polyNormal);
	}

	private static void sort(double[] x, double[] y, double[] z, Vector3f[] n) {
		if (y[0] > y[1]) {
			swap(x, y, z, n, 0, 1);
		}
		if (y[1] > y[2]) {
			swap(x, y, z, n, 1, 2);
		}
		if (y[0] > y[1]) {
			swap(x, y, z, n, 0, 1);
		}
	}

	private static void swap(double[] x, double[] y, double[] z, Vector3f[] n, int i, int j) {
		double tempY = y[i];
		double tempX = x[i];
		double tempZ = z[i];
		Vector3f tempN = n[i];
		x[i] = x[j];
		y[i] = y[j];
		z[i] = z[j];
		n[i] = n[j];
		x[j] = tempX;
		y[j] = tempY;
		z[j] = tempZ;
		n[j] = tempN;
	}
}