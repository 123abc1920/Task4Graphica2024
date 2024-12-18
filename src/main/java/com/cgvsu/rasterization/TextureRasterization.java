package com.cgvsu.rasterization;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import com.cgvsu.light.Light;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class TextureRasterization {
	public static boolean fillTriangle(Vector3f[] vertices, final Vector2f[] texturePoints,
			Image image, Vector3f polyNormal, Vector3f[] normals, Vector3f[] vecsToCamera) {

		sort(vertices, texturePoints, normals);

		if ((vertices[0].getX() * (vertices[1].getY() - vertices[2].getY())
				+ vertices[1].getX() * (vertices[2].getY() - vertices[0].getY())
				+ vertices[2].getX() * (vertices[0].getY() - vertices[1].getY())) == 0) {
			return false;
		}

		if (vertices.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		for (int y = (int) Math.ceil(vertices[1].getY()); y <= (int) Math.floor(vertices[2].getY()); y++) {
			final double x1 = (vertices[2].getY() - vertices[1].getY() == 0) ? vertices[1].getX()
					: (y - vertices[1].getY()) * (vertices[2].getX() - vertices[1].getX())
							/ (vertices[2].getY() - vertices[1].getY()) + vertices[1].getX();
			final double x2 = (vertices[0].getY() - vertices[2].getY() == 0) ? vertices[2].getX()
					: (y - vertices[2].getY()) * (vertices[0].getX() - vertices[2].getX())
							/ (vertices[0].getY() - vertices[2].getY()) + vertices[2].getX();
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				putPxl(x, y, vertices, texturePoints, image, vecsToCamera, normals, polyNormal);
			}
		}

		for (int y = (int) Math.floor(vertices[1].getY()); y >= Math.ceil(vertices[0].getY()); y--) {
			final double x1 = (vertices[1].getY() - vertices[0].getY() == 0) ? vertices[0].getX()
					: (y - vertices[0].getY()) * (vertices[1].getX() - vertices[0].getX())
							/ (vertices[1].getY() - vertices[0].getY()) + vertices[0].getX();
			final double x2 = (vertices[0].getY() - vertices[2].getY() == 0) ? vertices[2].getX()
					: (y - vertices[2].getY()) * (vertices[0].getX() - vertices[2].getX())
							/ (vertices[0].getY() - vertices[2].getY()) + vertices[2].getX();
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				putPxl(x, y, vertices, texturePoints, image, vecsToCamera, normals, polyNormal);
			}
		}

		return true;
	}

	private static void putPxl(int x, int y, Vector3f[] vertices, Vector2f[] texturePoints,
			Image img, Vector3f[] vecsToCamera, Vector3f[] normals, Vector3f polyNormal) {
		double[] arrX = { vertices[0].getX(), vertices[1].getX(), vertices[2].getX() };
		double[] arrY = { vertices[0].getY(), vertices[1].getY(), vertices[2].getY() };
		double[] barizenticCoordinate = BarycentricCoordinate.barizentricCalculator(x, y, arrX, arrY);
		double z = (double) (barizenticCoordinate[0] * vertices[0].getZ() + barizenticCoordinate[1] * vertices[1].getZ()
				+ barizenticCoordinate[2] * vertices[2].getZ());

		double toCameraX = vecsToCamera[0].getX() * barizenticCoordinate[0]
				+ vecsToCamera[1].getX() * barizenticCoordinate[1] + vecsToCamera[2].getX() * barizenticCoordinate[2];
		double toCameraY = vecsToCamera[0].getY() * barizenticCoordinate[0]
				+ vecsToCamera[1].getY() * barizenticCoordinate[1] + vecsToCamera[2].getY() * barizenticCoordinate[2];
		double toCameraZ = vecsToCamera[0].getZ() * barizenticCoordinate[0]
				+ vecsToCamera[1].getZ() * barizenticCoordinate[1] + vecsToCamera[2].getZ() * barizenticCoordinate[2];
		Vector3f vecToCamera = new Vector3f(toCameraX, toCameraY, toCameraZ);

		if (RasterizationParams.zBuffer.inZBuffer(x, y)) {
			if (z < RasterizationParams.zBuffer.get((int) x, (int) y)) {
				Color dotColor = getColor(barizenticCoordinate, texturePoints, img, normals, vecToCamera, polyNormal);
				RasterizationParams.zBuffer.set((int) x, (int) y, z);
				RasterizationParams.drawBuffer.set(x, y, dotColor);
			}
		}
	}

	private static Color getColor(double[] barycentricCoords, Vector2f[] texturePoints, Image image, Vector3f[] normals,
			Vector3f toCamera, Vector3f polyNormal) {
		double u = texturePoints[0].getX() * barycentricCoords[0] + texturePoints[1].getX() * barycentricCoords[1]
				+ texturePoints[2].getX() * barycentricCoords[2];
		double v = texturePoints[0].getY() * barycentricCoords[0] + texturePoints[1].getY() * barycentricCoords[1]
				+ texturePoints[2].getY() * barycentricCoords[2];

		PixelReader reader = image.getPixelReader();
		int x = (int) Math.ceil(u * (image.getWidth()));
		int y = (int) Math.ceil(v * (image.getHeight()));

		x = (int) Math.max(0, Math.min(x, image.getWidth() - 1));
		y = (int) Math.max(0, Math.min(y, image.getHeight() - 1));

		return getDotColor(reader.getColor(x, y), normals, barycentricCoords, toCamera, polyNormal,
				RasterizationParams.getIsLight());
	}

	private static Color getDotColor(Color c, Vector3f[] normals, double[] barizenticCoordinate, Vector3f toCamera,
			Vector3f polyNormal, boolean isLight) {
		if (!isLight) {
			return c;
		}

		Vector3f dotNormal = new Vector3f();
		dotNormal.setX(normals[0].getX() * barizenticCoordinate[0] + normals[1].getX() * barizenticCoordinate[1]
				+ normals[2].getX() * barizenticCoordinate[2]);
		dotNormal.setY(normals[0].getY() * barizenticCoordinate[0] + normals[1].getY() * barizenticCoordinate[1]
				+ normals[2].getY() * barizenticCoordinate[2]);
		dotNormal.setZ(normals[0].getZ() * barizenticCoordinate[0] + normals[1].getZ() * barizenticCoordinate[1]
				+ normals[2].getZ() * barizenticCoordinate[2]);
		dotNormal = dotNormal.normalize();

		return Light.newRgb(c, dotNormal, toCamera, polyNormal);
	}

	private static void sort(Vector3f[] y, Vector2f[] v, Vector3f[] n) {
		if (y[0].getY() > y[1].getY()) {
			swap(y, v, n, 0, 1);
		}
		if (y[1].getY() > y[2].getY()) {
			swap(y, v, n, 1, 2);
		}
		if (y[0].getY() > y[1].getY()) {
			swap(y, v, n, 0, 1);
		}
	}

	private static void swap(Vector3f[] y, Vector2f[] texturePoints, Vector3f[] n, int i, int j) {
		Vector3f tempY = y[i];
		Vector2f tempV = texturePoints[i];
		Vector3f tempN = n[i];
		y[i] = y[j];
		n[i] = n[j];
		texturePoints[i] = texturePoints[j];
		y[j] = tempY;
		texturePoints[j] = tempV;
		n[j] = tempN;
	}
}