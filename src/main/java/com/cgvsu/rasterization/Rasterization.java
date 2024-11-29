package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Rasterization {
	public static void fillTriangle(final GraphicsContext graphicsContext, final float[] arrX, final float[] arrY,
			final Color[] colors) {

		final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

		sort(arrX, arrY, colors);

		for (float y = arrY[1]; y <= arrY[2]; y++) {
			final float x1 = (arrY[2] - arrY[1] == 0) ? arrX[1]
					: (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
			final float x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (float x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
				double[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
				pixelWriter.setColor((int) x, (int) y, getColor(barizenticCoordinate, colors));
			}
		}

		for (float y = arrY[1]; y >= arrY[0]; y--) {
			final float x1 = (arrY[1] - arrY[0] == 0) ? arrX[0]
					: (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
			final float x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (float x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
				double[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
				pixelWriter.setColor((int) x, (int) y, getColor(barizenticCoordinate, colors));
			}
		}
	}

	private static double determinator(float[][] arr) {
		return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] + arr[0][1] * arr[1][2] * arr[2][0]
				- arr[0][2] * arr[1][1] * arr[2][0] - arr[0][0] * arr[1][2] * arr[2][1]
				- arr[0][1] * arr[1][0] * arr[2][2];
	}

	private static double[] barizentricCalculator(float x, float y, float[] arrX, float[] arrY) {
		final double generalDeterminant = determinator(new float[][] { arrX, arrY, new float[] { 1, 1, 1 } });
		final double coordinate0 = Math.abs(determinator(new float[][] { new float[] { x, arrX[1], arrX[2] },
				new float[] { y, arrY[1], arrY[2] }, new float[] { 1, 1, 1 } }) / generalDeterminant);
		final double coordinate1 = Math.abs(determinator(new float[][] { new float[] { arrX[0], x, arrX[2] },
				new float[] { arrY[0], y, arrY[2] }, new float[] { 1, 1, 1 } }) / generalDeterminant);
		final double coordinate2 = Math.abs(determinator(new float[][] { new float[] { arrX[0], arrX[1], x },
				new float[] { arrY[0], arrY[1], y }, new float[] { 1, 1, 1 } }) / generalDeterminant);
		return new double[] { coordinate0, coordinate1, coordinate2 };
	}

	private static Color getColor(double[] barycentricCoords, Color[] colors) {

		final double red = barycentricCoords[0] * colors[0].getRed() + barycentricCoords[1] * colors[1].getRed()
				+ barycentricCoords[2] * colors[2].getRed();
		final double green = barycentricCoords[0] * colors[0].getGreen() + barycentricCoords[1] * colors[1].getGreen()
				+ barycentricCoords[2] * colors[2].getGreen();
		final double blue = barycentricCoords[0] * colors[0].getBlue() + barycentricCoords[1] * colors[1].getBlue()
				+ barycentricCoords[2] * colors[2].getBlue();

		return new Color(Math.max(0, Math.min(1, red)), Math.max(0, Math.min(1, green)), Math.max(0, Math.min(1, blue)),
				1);
	}

	private static void sort(float[] x, float[] y, Color[] c) {
		if (y[0] > y[1]) {
			swap(x, y, c, 0, 1);
		}
		if (y[1] > y[2]) {
			swap(x, y, c, 1, 2);
		}
		if (y[0] > y[1]) {
			swap(x, y, c, 0, 1);
		}
	}

	private static void swap(float[] x, float[] y, Color[] c, int i, int j) {
		float tempY = y[i];
		float tempX = x[i];
		Color tempC = c[i];
		x[i] = x[j];
		y[i] = y[j];
		c[i] = c[j];
		x[j] = tempX;
		y[j] = tempY;
		c[j] = tempC;
	}
}