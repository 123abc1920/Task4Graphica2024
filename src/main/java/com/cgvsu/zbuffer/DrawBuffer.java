package com.cgvsu.zbuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class DrawBuffer {
	private Color[][] matrix;
	private PixelWriter px;

	public DrawBuffer(int width, int height, GraphicsContext context) {
		matrix = new Color[width][height];
		if (context != null) {
			this.px = context.getPixelWriter();
		}

		refill();
	}

	public void refill() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				this.matrix[i][j] = null;
			}
		}
	}

	public int getWidth() {
		return this.matrix.length;
	}

	public int getHeight() {
		if (matrix.length == 0) {
			return 0;
		}
		return this.matrix[0].length;
	}

	public void set(int row, int col, Color z) {
		if (!inDrawBuffer(row, col)) {
			return;
		}
		matrix[row][col] = z;
	}

	public Color get(int row, int col) {
		if (!inDrawBuffer(row, col)) {
			return null;
		}
		return matrix[row][col];
	}

	public boolean inDrawBuffer(int x, int y) {
		return (x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight()) ? true : false;
	}

	public void draw() {
		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j < this.getHeight(); j++) {
				Color c = get(i, j);
				if (c != null) {
					this.px.setColor(i, j, c);
				}
			}
		}
	}

}
