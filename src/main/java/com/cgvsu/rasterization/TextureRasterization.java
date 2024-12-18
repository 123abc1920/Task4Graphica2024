package com.cgvsu.rasterization;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import com.cgvsu.light.Light;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.zbuffer.ZBuffer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class TextureRasterization {
	public static boolean fillTriangle(final double[] arrX, final double[] arrY,
			final double[] arrZ, final Vector2f[] texturePoints, ZBuffer zBuffer, Image image, Vector3f polyNormal,
			Vector3f[] normals, Vector3f[] vecsToCamera) {

		sort(arrX, arrY, arrZ, texturePoints, normals);

		if ((arrX[0] * (arrY[1] - arrY[2]) + arrX[1] * (arrY[2] - arrY[0]) + arrX[2] * (arrY[0] - arrY[1])) == 0) {
			return false;
		}

		if (arrX.length < 3 || arrY.length < 3 || arrZ.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		for (int y = (int) Math.ceil(arrY[1]); y <= (int) Math.floor(arrY[2]); y++) {
			final double x1 = (arrY[2] - arrY[1] == 0) ? arrX[1]
					: (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				putPxl(x, y, arrX, arrY, arrZ, texturePoints, image, vecsToCamera, normals, polyNormal);
			}
		}

		for (int y = (int) Math.floor(arrY[1]); y >= Math.ceil(arrY[0]); y--) {
			final double x1 = (arrY[1] - arrY[0] == 0) ? arrX[0]
					: (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
			final double x2 = (arrY[0] - arrY[2] == 0) ? arrX[2]
					: (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
			for (int x = (int) Math.floor(Math.min(x1, x2)); x < Math.ceil(Math.max(x1, x2)); x++) {
				putPxl(x, y, arrX, arrY, arrZ, texturePoints, image, vecsToCamera, normals, polyNormal);
			}
		}

		return true;
	}

	private static void putPxl(int x, int y, double[] arrX, double[] arrY, double[] arrZ, Vector2f[] texturePoints,
			Image img, Vector3f[] vecsToCamera, Vector3f[] normals, Vector3f polyNormal) {
		double[] barizenticCoordinate = BarycentricCoordinate.barizentricCalculator(x, y, arrX, arrY);
		double z = (double) (barizenticCoordinate[0] * arrZ[0] + barizenticCoordinate[1] * arrZ[1]
				+ barizenticCoordinate[2] * arrZ[2]);

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

	private static void sort(double[] x, double[] y, double[] z, Vector2f[] v, Vector3f[] n) {
		if (y[0] > y[1]) {
			swap(x, y, z, v, n, 0, 1);
		}
		if (y[1] > y[2]) {
			swap(x, y, z, v, n, 1, 2);
		}
		if (y[0] > y[1]) {
			swap(x, y, z, v, n, 0, 1);
		}
	}

	private static void swap(double[] x, double[] y, double[] z, Vector2f[] texturePoints, Vector3f[] n, int i, int j) {
		double tempY = y[i];
		double tempX = x[i];
		double tempZ = z[i];
		Vector2f tempV = texturePoints[i];
		Vector3f tempN = n[i];
		x[i] = x[j];
		y[i] = y[j];
		z[i] = z[j];
		n[i] = n[j];
		texturePoints[i] = texturePoints[j];
		x[j] = tempX;
		y[j] = tempY;
		z[j] = tempZ;
		texturePoints[j] = tempV;
		n[j] = tempN;
	}
}