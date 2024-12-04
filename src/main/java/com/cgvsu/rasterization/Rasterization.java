package com.cgvsu.rasterization;

import com.cgvsu.math.Global;
import com.cgvsu.math.Matrix3f;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Rasterization {
	public static void fillTriangle(final GraphicsContext graphicsContext, final double[] arrX, final double[] arrY,
			final double[] arrZ, final Color[] colors, ZBuffer zBuffer) {

		final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

		sort(arrX, arrY, arrZ, colors);

		if (arrX.length < 3 || arrY.length < 3 || arrZ.length < 3 || colors.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		for (int y = (int) Math.ceil(arrY[1]); y < (int) Math.floor(arrY[2]); y++) {
			final double x1 = (arrY[2] - arrY[1] == 0) ? arrX[1]
					: (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				double[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
				double z = (double) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
						+ barizenticCoordinate[2] * arrZ[2]);
				if (x >= 0 && x < zBuffer.getWidth() && y >= 0 && y < zBuffer.getHeight()) {
					if (z < zBuffer.get((int) x, (int) y)) {
						pixelWriter.setColor((int) x, (int) y, getColor(barizenticCoordinate, colors));
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
				double[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
				double z = (double) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
						+ barizenticCoordinate[2] * arrZ[2]);
				if (x >= 0 && x < zBuffer.getWidth() && y >= 0 && y < zBuffer.getHeight()) {
					if (z < zBuffer.get((int) x, (int) y)) {
						pixelWriter.setColor((int) x, (int) y, getColor(barizenticCoordinate, colors));
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
		double coordinate0 = Math.abs(determinator(new double[][] { new double[] { x, arrX[1], arrX[2] },
				new double[] { y, arrY[1], arrY[2] }, new double[] { 1, 1, 1 } }) / generalDeterminant);
		double coordinate1 = Math.abs(determinator(new double[][] { new double[] { arrX[0], x, arrX[2] },
				new double[] { arrY[0], y, arrY[2] }, new double[] { 1, 1, 1 } }) / generalDeterminant);
		double coordinate2 = Math.abs(determinator(new double[][] { new double[] { arrX[0], arrX[1], x },
				new double[] { arrY[0], arrY[1], y }, new double[] { 1, 1, 1 } }) / generalDeterminant);

		return new double[] { coordinate0, coordinate1, coordinate2 };
	}

	private static Color getColor(double[] barycentricCoords, Color[] colors) {
		final double red = (barycentricCoords[0] * (double) colors[0].getRed()
				+ barycentricCoords[1] * (double) colors[1].getRed()
				+ barycentricCoords[2] * (double) colors[2].getRed());
		final double green = (barycentricCoords[0] * (double) colors[0].getGreen()
				+ barycentricCoords[1] * (double) colors[1].getGreen()
				+ barycentricCoords[2] * (double) colors[2].getGreen());
		final double blue = (barycentricCoords[0] * (double) colors[0].getBlue()
				+ barycentricCoords[1] * (double) colors[1].getBlue()
				+ barycentricCoords[2] * (double) colors[2].getBlue());

		return new Color(Math.max(0, Math.min(1, red)), Math.max(0, Math.min(1, green)), Math.max(0, Math.min(1, blue)),
				1);
	}

	private static void sort(double[] x, double[] y, double[] z, Color[] c) {
		if (y[0] > y[1]) {
			swap(x, y, z, c, 0, 1);
		}
		if (y[1] > y[2]) {
			swap(x, y, z, c, 1, 2);
		}
		if (y[0] > y[1]) {
			swap(x, y, z, c, 0, 1);
		}
	}

	private static void swap(double[] x, double[] y, double[] z, Color[] c, int i, int j) {
		double tempY = y[i];
		double tempX = x[i];
		double tempZ = z[i];
		Color tempC = c[i];
		x[i] = x[j];
		y[i] = y[j];
		z[i] = z[j];
		x[j] = tempX;
		y[j] = tempY;
		z[j] = tempZ;
	}

	private static double[] microSwap(double a, double b) {
		double tempA = a;
		a = b;
		b = tempA;
		return new double[] { b, a };
	}
}