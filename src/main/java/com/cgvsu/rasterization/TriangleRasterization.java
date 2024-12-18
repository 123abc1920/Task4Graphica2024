package com.cgvsu.rasterization;

import com.cgvsu.light.Light;
import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class TriangleRasterization {
	public static boolean fillTriangle(Vector3f[] vertices, Color color, Vector3f[] normals,
			Vector3f polyNormal, Vector3f[] vecsToCamera) {

		sort(vertices, normals);

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
				putPxl(x, y, vertices, color, normals, polyNormal, vecsToCamera);
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
				putPxl(x, y, vertices, color, normals, polyNormal, vecsToCamera);
			}
		}

		return true;
	}

	private static void putPxl(int x, int y, Vector3f[] vertices, Color c, Vector3f[] normals, Vector3f polyNormal,
			Vector3f[] vecsToCamera) {
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
				Color dotColor = getDotColor(c, normals, barizenticCoordinate, vecToCamera, polyNormal,
						RasterizationParams.getIsLight());
				RasterizationParams.zBuffer.set((int) x, (int) y, z);
				RasterizationParams.drawBuffer.set(x, y, dotColor);
			}
		}
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

	private static void sort(Vector3f[] v, Vector3f[] n) {
		if (v[0].getY() > v[1].getY()) {
			swap(v, n, 0, 1);
		}
		if (v[1].getY() > v[2].getY()) {
			swap(v, n, 1, 2);
		}
		if (v[0].getY() > v[1].getY()) {
			swap(v, n, 0, 1);
		}
	}

	private static void swap(Vector3f[] v, Vector3f[] n, int i, int j) {
		Vector3f tempV = v[i];
		Vector3f tempN = n[i];
		v[i] = v[j];
		n[i] = n[j];
		v[j] = tempV;
		n[j] = tempN;
	}
}