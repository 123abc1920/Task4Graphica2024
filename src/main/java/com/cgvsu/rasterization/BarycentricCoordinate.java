package com.cgvsu.rasterization;

import com.cgvsu.math.Matrix3f;
import com.cgvsu.math.Vector3f;

public class BarycentricCoordinate {

	public static double[] barizentricCalculator(double x, double y, Vector3f[] vertices) {
		Matrix3f generalMatrix = new Matrix3f(
				new double[][] { { vertices[0].getX(), vertices[1].getX(), vertices[2].getX() },
						{ vertices[0].getY(), vertices[1].getY(), vertices[2].getY() }, { 1, 1, 1 } });
		final double generalDeterminant = generalMatrix.determinate();

		double coordinate0 = Math.abs(new Matrix3f(new double[][] { { x, vertices[1].getX(), vertices[2].getX() },
				{ y, vertices[1].getY(), vertices[2].getY() }, { 1, 1, 1 } }).determinate() / generalDeterminant);
		double coordinate1 = Math.abs(new Matrix3f(new double[][] { { vertices[0].getX(), x, vertices[2].getX() },
				{ vertices[0].getY(), y, vertices[2].getY() }, { 1, 1, 1 } }).determinate() / generalDeterminant);
		double coordinate2 = Math.abs(new Matrix3f(new double[][] { { vertices[0].getX(), vertices[1].getX(), x },
				{ vertices[0].getY(), vertices[1].getY(), y }, { 1, 1, 1 } }).determinate() / generalDeterminant);

		return new double[] { coordinate0, coordinate1, coordinate2 };
	}
}
