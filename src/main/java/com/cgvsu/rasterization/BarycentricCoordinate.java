package com.cgvsu.rasterization;

public class BarycentricCoordinate {
	public static double determinator(double[][] arr) {
		return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] + arr[0][1] * arr[1][2] * arr[2][0]
				- arr[0][2] * arr[1][1] * arr[2][0] - arr[0][0] * arr[1][2] * arr[2][1]
				- arr[0][1] * arr[1][0] * arr[2][2];
	}

	public static double[] barizentricCalculator(double x, double y, double[] arrX, double[] arrY) {
		final double generalDeterminant = determinator(new double[][] { arrX, arrY, new double[] { 1, 1, 1 } });
		double coordinate0 = Math.abs(determinator(new double[][] { new double[] { x, arrX[1], arrX[2] },
				new double[] { y, arrY[1], arrY[2] }, new double[] { 1, 1, 1 } }) / generalDeterminant);
		double coordinate1 = Math.abs(determinator(new double[][] { new double[] { arrX[0], x, arrX[2] },
				new double[] { arrY[0], y, arrY[2] }, new double[] { 1, 1, 1 } }) / generalDeterminant);
		double coordinate2 = Math.abs(determinator(new double[][] { new double[] { arrX[0], arrX[1], x },
				new double[] { arrY[0], arrY[1], y }, new double[] { 1, 1, 1 } }) / generalDeterminant);

		return new double[] { coordinate0, coordinate1, coordinate2 };
	}
}
