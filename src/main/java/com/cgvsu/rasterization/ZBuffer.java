package com.cgvsu.rasterization;

public class ZBuffer {
	private float[][] matrix;

	public ZBuffer(int width, int height) {
		matrix = new float[width][height];

		fill();
	}

	private void fill() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				this.matrix[i][j] = Integer.MAX_VALUE;
			}
		}
	}

	public int getWidth() {
		return this.matrix.length;
	}

	public int getHeight() {
		return this.matrix[0].length;
	}

	public void set(int row, int col, float z) {
		matrix[row][col] = z;
	}

	public float get(int row, int col) {
		return matrix[row][col];
	}
}
