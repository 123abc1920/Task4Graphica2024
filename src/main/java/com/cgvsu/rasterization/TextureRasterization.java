package com.cgvsu.rasterization;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.cgvsu.math.Vector2f;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class TextureRasterization {
	public static void fillTriangle(final GraphicsContext graphicsContext, final double[] arrX, final double[] arrY,
			final double[] arrZ, final Vector2f[] texturePoints, ZBuffer zBuffer, Image image) {

		final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

		sort(arrX, arrY, arrZ, texturePoints);

		if (arrX.length < 3 || arrY.length < 3 || arrZ.length < 3 || texturePoints.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		for (double y = arrY[1]; y <= arrY[2]; y++) {
			final double x1 = (arrY[2] - arrY[1] == 0) ? arrX[1]
					: (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (double x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
				double[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
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

		for (double y = arrY[1]; y >= arrY[0]; y--) {
			final double x1 = (arrY[1] - arrY[0] == 0) ? arrX[0]
					: (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (double x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
				double[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
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

	private static double determinator(double[][] arr) {
		return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] + arr[0][1] * arr[1][2] * arr[2][0]
				- arr[0][2] * arr[1][1] * arr[2][0] - arr[0][0] * arr[1][2] * arr[2][1]
				- arr[0][1] * arr[1][0] * arr[2][2];
	}

	private static double[] barizentricCalculator(double x, double y, double[] arrX, double[] arrY) {
		final double generalDeterminant = determinator(new double[][] { arrX, arrY, new double[] { 1, 1, 1 } });
		final double coordinate0 = Math.abs(determinator(new double[][] { new double[] { x, arrX[1], arrX[2] },
				new double[] { y, arrY[1], arrY[2] }, new double[] { 1, 1, 1 } }) / generalDeterminant);
		final double coordinate1 = Math.abs(determinator(new double[][] { new double[] { arrX[0], x, arrX[2] },
				new double[] { arrY[0], y, arrY[2] }, new double[] { 1, 1, 1 } }) / generalDeterminant);
		final double coordinate2 = Math.abs(determinator(new double[][] { new double[] { arrX[0], arrX[1], x },
				new double[] { arrY[0], arrY[1], y }, new double[] { 1, 1, 1 } }) / generalDeterminant);
		return new double[] { coordinate0, coordinate1, coordinate2 };
	}

	private static Color getColor(double[] barycentricCoords, Vector2f[] texturePoints, Image image) {

		double x = texturePoints[0].getX() * barycentricCoords[0] + texturePoints[1].getX() * barycentricCoords[1]
				+ texturePoints[2].getX() * barycentricCoords[2];
		double y = texturePoints[0].getY() * barycentricCoords[0] + texturePoints[1].getY() * barycentricCoords[1]
				+ texturePoints[2].getY() * barycentricCoords[2];

		PixelReader reader = image.getPixelReader();
		x *= image.getWidth();
		y *= image.getHeight();

		return reader.getColor((int) x, (int) y);
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

	private static double[] microSwap(double a, double b) {
		double tempA = a;
		a = b;
		b = tempA;
		return new double[] { b, a };
	}
}