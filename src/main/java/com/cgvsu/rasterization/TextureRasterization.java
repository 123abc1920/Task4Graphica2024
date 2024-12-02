package com.cgvsu.rasterization;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import com.cgvsu.math.Matrix3f;
import com.cgvsu.math.Vector2f;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class TextureRasterization {
	public static void fillTriangle(final GraphicsContext graphicsContext, final float[] arrX, final float[] arrY,
			final float[] arrZ, final Vector2f[] texturePoints, ZBuffer zBuffer, Image image) {

		final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

		sort(arrX, arrY, arrZ, texturePoints);

		if (arrX.length < 3 || arrY.length < 3 || arrZ.length < 3 || texturePoints.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		for (float y = arrY[1]; y < arrY[2]; y++) {
			final float x1 = (arrY[2] - arrY[1] == 0) ? arrX[1]
					: (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
			final float x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (float x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
				float[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
				float z = (float) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
						+ barizenticCoordinate[2] * arrZ[2]);
				if (x >= 0 && x < zBuffer.getWidth() && y >= 0 && y < zBuffer.getHeight()) {
					if (z < zBuffer.get((int) x, (int) y)) {
						pixelWriter.setColor((int) x, (int) y, getColor(barizenticCoordinate, texturePoints, image));
						zBuffer.set((int) x, (int) y, z);
					}
				}
			}
		}

		for (float y = arrY[1]; y >= arrY[0]; y--) {
			final float x1 = (arrY[1] - arrY[0] == 0) ? arrX[0]
					: (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
			final float x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (float x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
				float[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
				float z = (float) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
						+ barizenticCoordinate[2] * arrZ[2]);
				if (x >= 0 && x < zBuffer.getWidth() && y >= 0 && y < zBuffer.getHeight()) {
					if (z < zBuffer.get((int) x, (int) y)) {
						pixelWriter.setColor((int) x, (int) y, getColor(barizenticCoordinate, texturePoints, image));
						zBuffer.set((int) x, (int) y, z);
					}
				}
			}
		}
	}

	private static float[] barizentricCalculator(float x, float y, float[] arrX, float[] arrY) {
		final float generalDeterminant = new Matrix3f(new float[][] { new float[] { arrX[0], arrX[1], arrX[2] },
				new float[] { arrY[0], arrY[1], arrY[2] }, new float[] { 1, 1, 1 } }).determinate();
		float coordinate0 = new Matrix3f(new float[][] { new float[] { x, arrX[1], arrX[2] },
				new float[] { y, arrY[1], arrY[2] }, new float[] { 1, 1, 1 } }).determinate() / generalDeterminant;
		float coordinate1 = new Matrix3f(new float[][] { new float[] { arrX[0], x, arrX[2] },
				new float[] { arrY[0], y, arrY[2] }, new float[] { 1, 1, 1 } }).determinate() / generalDeterminant;
		float coordinate2 = new Matrix3f(new float[][] { new float[] { arrX[0], arrX[1], x },
				new float[] { arrY[0], arrY[1], y }, new float[] { 1, 1, 1 } }).determinate() / generalDeterminant;

		return new float[] { coordinate0, coordinate1, coordinate2 };
	}

	private static Color getColor(float[] barycentricCoords, Vector2f[] texturePoints, Image image) {
		float x = texturePoints[0].getX() * barycentricCoords[0] + texturePoints[1].getX() * barycentricCoords[1]
				+ texturePoints[2].getX() * barycentricCoords[2];
		float y = texturePoints[0].getY() * barycentricCoords[0] + texturePoints[1].getY() * barycentricCoords[1]
				+ texturePoints[2].getY() * barycentricCoords[2];

		PixelReader reader = image.getPixelReader();
		x *= image.getWidth();
		y *= image.getHeight();

		return reader.getColor((int) x, (int) y);
	}

	private static void sort(float[] x, float[] y, float[] z, Vector2f[] v) {
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

	private static void swap(float[] x, float[] y, float[] z, Vector2f[] texturePoints, int i, int j) {
		float tempY = y[i];
		float tempX = x[i];
		float tempZ = z[i];
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

	private static float[] microSwap(float a, float b) {
		float tempA = a;
		a = b;
		b = tempA;
		return new float[] { b, a };
	}
}