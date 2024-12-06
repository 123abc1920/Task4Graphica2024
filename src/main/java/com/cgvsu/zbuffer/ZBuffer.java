package com.cgvsu.zbuffer;

public class ZBuffer {
	private double[][] matrix;

	public ZBuffer(int width, int height) {
		matrix = new double[width][height];

		refill();
	}

	public void refill() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				this.matrix[i][j] = Double.MAX_VALUE;
			}
		}
	}

	public int getWidth() {
		return this.matrix.length;
	}

	public int getHeight() {
		return this.matrix[0].length;
	}

	public void set(int row, int col, double z) {
		matrix[row][col] = z;
	}

	public double get(int row, int col) {
		return matrix[row][col];
	}

	public boolean inZBuffer(int x, int y) {
		return (x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight()) ? true : false;
	}
}
