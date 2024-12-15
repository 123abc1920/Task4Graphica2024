package com.cgvsu.rasterization;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import com.cgvsu.math.Vector2f;
import com.cgvsu.zbuffer.ZBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TextureRasterization {
	public static boolean fillTriangle(final GraphicsContext graphicsContext, final double[] arrX, final double[] arrY,
			final double[] arrZ, final Vector2f[] texturePoints, ZBuffer zBuffer, Image image) {

		sort(arrX, arrY, arrZ, texturePoints);

		if ((arrX[0] * (arrY[1] - arrY[2]) + arrX[1] * (arrY[2] - arrY[0]) + arrX[2] * (arrY[0] - arrY[1])) == 0) {
			return false;
		}

		if (arrX.length < 3 || arrY.length < 3 || arrZ.length < 3 || texturePoints.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		for (int y = (int) Math.ceil(arrY[1]); y <= (int) Math.floor(arrY[2]); y++) {
			final double x1 = (arrY[2] - arrY[1] == 0) ? arrX[1]
					: (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				double[] barizenticCoordinate = BarycentricCoordinate.barizentricCalculator(x, y, arrX, arrY);
				double z = (double) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
						+ barizenticCoordinate[2] * arrZ[2]);
				if (x >= 0 && x < zBuffer.getWidth() && y >= 0 && y < zBuffer.getHeight()) {
					if (z < zBuffer.get((int) x, (int) y)) {
						RasterizationParams.drawBuffer.set((int) x, (int) y,
								getColor(barizenticCoordinate, texturePoints, image));
						zBuffer.set((int) x, (int) y, z);
					}
				}
			}
		}

		for (int y = (int) Math.floor(arrY[1]); y >= Math.ceil(arrY[0]); y--) {
			final double x1 = (arrY[1] - arrY[0] == 0) ? arrX[0]
					: (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				double[] barizenticCoordinate = BarycentricCoordinate.barizentricCalculator(x, y, arrX, arrY);
				double z = (double) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
						+ barizenticCoordinate[2] * arrZ[2]);
				if (x >= 0 && x < zBuffer.getWidth() && y >= 0 && y < zBuffer.getHeight()) {
					if (z < zBuffer.get((int) x, (int) y)) {
						RasterizationParams.drawBuffer.set((int) x, (int) y,
								getColor(barizenticCoordinate, texturePoints, image));
						zBuffer.set((int) x, (int) y, z);
					}
				}
			}
		}

		return true;
	}

	private static Color getColor(double[] barycentricCoords, Vector2f[] texturePoints, Image image) {
		double u = texturePoints[0].getX() * barycentricCoords[0] + texturePoints[1].getX() * barycentricCoords[1]
				+ texturePoints[2].getX() * barycentricCoords[2];
		double v = texturePoints[0].getY() * barycentricCoords[0] + texturePoints[1].getY() * barycentricCoords[1]
				+ texturePoints[2].getY() * barycentricCoords[2];

		PixelReader reader = image.getPixelReader();
		int x = (int) Math.ceil(u * (image.getWidth() - 1));
		int y = (int) Math.ceil(v * (image.getHeight() - 1));

		x = (int) Math.max(0, Math.min(x, image.getWidth() - 1));
		y = (int) Math.max(0, Math.min(y, image.getHeight() - 1));

		return reader.getColor(x, y);
	}

	private static void sort(double[] x, double[] y, double[] z, Vector2f[] v) {
		if (y[0] > y[1]) {
			swap(x, y, z, v, 0, 1);
		}
		if (y[1] > y[2]) {
			swap(x, y, z, v, 1, 2);
		}
		if (y[0] > y[1]) {
			swap(x, y, z, v, 0, 1);
		}
	}

	private static void swap(double[] x, double[] y, double[] z, Vector2f[] texturePoints, int i, int j) {
		double tempY = y[i];
		double tempX = x[i];
		double tempZ = z[i];
		Vector2f tempV = texturePoints[i];
		x[i] = x[j];
		y[i] = y[j];
		z[i] = z[j];
		texturePoints[i] = texturePoints[j];
		x[j] = tempX;
		y[j] = tempY;
		z[j] = tempZ;
		texturePoints[j] = tempV;
	}
}