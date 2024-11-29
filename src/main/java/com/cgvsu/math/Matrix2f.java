package com.cgvsu.math;

public class Matrix2f {
	private float[][] matrix;

	public Matrix2f() {
		this.matrix = new float[2][2];
	}

	public float getCell(int row, int col) {
		return matrix[row][col];
	}

	public void setCell(int row, int col, float f) {
		this.matrix[row][col] = f;
	}

	public float determinant() {
		return this.matrix[0][0] * this.matrix[1][1] - this.matrix[1][0] * this.matrix[0][1];
	}

}
