package com.cgvsu.math;

import com.cgvsu.math.Vector3f;

public class Matrix3f {
	private static final float esp = 1e-4f;
	private float[][] matrix;

	public Matrix3f() {
		this.matrix = new float[3][3];
	}

	public Matrix3f(boolean isUnitMatrix) {
		if (isUnitMatrix) {
			this.matrix = new float[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
		} else {
			this.matrix = new float[3][3];
		}
	}

	public Matrix3f(float[][] matrix) {
		if (matrix.length != 3 || matrix[0].length != 3) {
			throw new IllegalArgumentException("Matrix should have 3 rows and 3 columns");
		}
		this.matrix = matrix;
	}

	public float[][] getMatrix() {
		return matrix;
	}

	public float getCell(int row, int col) {
		return matrix[row][col];
	}

	public void setCell(int row, int col, float f) {
		this.matrix[row][col] = f;
	}

	public void printMatrix() {
		System.out.println("Matrix:");
		for (float[] floats : matrix) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(floats[j] + " ");
			}
			System.out.println();
		}
	}

	public Matrix3f add(Matrix3f matrix3f) {
		if (matrix3f.getMatrix().length != 3 || matrix3f.getMatrix()[0].length != 3) {
			throw new IllegalArgumentException("Matrix should have 3 rows and 3 columns");
		}
		float[][] values = new float[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				values[i][j] = matrix[i][j] + matrix3f.getCell(i, j);
			}
		}
		return new Matrix3f(values);
	}

	public Matrix3f sub(Matrix3f matrix3f) {
		if (matrix3f.getMatrix().length != 3 || matrix3f.getMatrix()[0].length != 3) {
			throw new IllegalArgumentException("Matrix should have 3 rows and 3 columns");
		}
		float[][] values = new float[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				values[i][j] = matrix[i][j] - matrix3f.getCell(i, j);
			}
		}
		return new Matrix3f(values);
	}

	public Vector3f multiplyVector(Vector3f vectorCol) {
		if (vectorCol == null) {
			throw new IllegalArgumentException("Vector should not be null");
		}
		float[] values = new float[3];
		for (int i = 0; i < matrix.length; i++) {
			values[0] = 0;
			for (int j = 0; j < matrix[0].length; j++) {
				values[i] += matrix[i][j] * vectorCol.get(j);
			}
		}
		return new Vector3f(values[0], values[1], values[2]);
	}

	public Matrix3f multiplyMatrix(Matrix3f matrix3f) {
		if (matrix3f.getMatrix().length != 3 || matrix3f.getMatrix()[0].length != 3) {
			throw new IllegalArgumentException("Matrix should have 3 rows and 3 columns");
		}
		float[][] values = new float[3][3];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				values[i][j] = 0;
				for (int k = 0; k < 3; k++) {
					values[i][j] += matrix[i][k] * matrix3f.getCell(k, j);
				}
			}
		}
		return new Matrix3f(values);
	}

	public Matrix3f transpose() {
		if (matrix.length != 3 || matrix[0].length != 3) {
			throw new IllegalArgumentException("Предоставленная матрица должна быть матрицей 3 на 3.");
		}
		float[][] transposed = new float[3][3];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				transposed[j][i] = matrix[i][j];
			}
		}
		return new Matrix3f(transposed);
	}

	public float determinate() {
		if (matrix.length != 3 || matrix[0].length != 3) {
			throw new IllegalArgumentException("Предоставленная матрица должна быть матрицей 3 на 3.");
		}
		return (matrix[0][0] * matrix[1][1] * matrix[2][2] - (matrix[0][2] * matrix[1][1] * matrix[2][0])
				+ matrix[0][1] * matrix[1][2] * matrix[2][0] - (matrix[0][1] * matrix[1][0] * matrix[2][2])
				+ matrix[0][2] * matrix[1][0] * matrix[2][1] - (matrix[0][0] * matrix[1][2] * matrix[2][1]));
	}

	public boolean equalsAns(Matrix3f matrix3f) {
		boolean flag = false;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] == matrix3f.getCell(i, j)) {
					flag = true;
				} else
					return false;
			}
		}
		return flag;
	}
}