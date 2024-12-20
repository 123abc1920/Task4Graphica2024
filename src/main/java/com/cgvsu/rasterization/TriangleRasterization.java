package com.cgvsu.rasterization;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cgvsu.light.Light;
import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class TriangleRasterization {

	static class VertexObject {
		Vector3f vertice;
		Vector3f normal;
		Vector3f vecToCamera;

		public VertexObject(Vector3f vertice, Vector3f normal, Vector3f vecToCamera) {
			this.vertice = vertice;
			this.normal = normal;
			this.vecToCamera = vecToCamera;
		}

		public double getY() {
			return vertice.getY();
		}

		public double getX() {
			return vertice.getX();
		}
	}

	public static boolean fillTriangle(Vector3f[] vertices, Color color, Vector3f[] normals, Vector3f polyNormal,
			Vector3f[] vecsToCamera) {

		if (vertices.length < 3) {
			throw new IllegalArgumentException("Массивы должны содержать как минимум 3 элемента.");
		}

		if ((vertices[0].getX() * (vertices[1].getY() - vertices[2].getY())
				+ vertices[1].getX() * (vertices[2].getY() - vertices[0].getY())
				+ vertices[2].getX() * (vertices[0].getY() - vertices[1].getY())) == 0) {
			return false;
		}

		VertexObject vo1 = new VertexObject(vertices[0], normals[0], vecsToCamera[0]);
		VertexObject vo2 = new VertexObject(vertices[1], normals[1], vecsToCamera[1]);
		VertexObject vo3 = new VertexObject(vertices[2], normals[2], vecsToCamera[2]);
		List<VertexObject> vertexList = Arrays.asList(vo1, vo2, vo3);

		Collections.sort(vertexList, new Comparator<VertexObject>() {

			@Override
			public int compare(VertexObject o1, VertexObject o2) {
				return (o1.getY() <= o2.getY()) ? -1 : 1;
			}

		});

		int startY = (int) Math.max(0, Math.ceil(vertexList.get(1).getY()));
		int finY = (int) Math.min(RasterizationParams.drawBuffer.getHeight(), Math.floor(vertexList.get(2).getY()));
		for (int y = startY; y <= finY; y++) {
			final double x1 = (vertexList.get(2).getY() - vertexList.get(1).getY() == 0) ? vertexList.get(1).getX()
					: (y - vertexList.get(1).getY()) * (vertexList.get(2).getX() - vertexList.get(1).getX())
							/ (vertexList.get(2).getY() - vertexList.get(1).getY()) + vertexList.get(1).getX();
			final double x2 = (vertexList.get(0).getY() - vertexList.get(2).getY() == 0) ? vertexList.get(2).getX()
					: (y - vertexList.get(2).getY()) * (vertexList.get(0).getX() - vertexList.get(2).getX())
							/ (vertexList.get(0).getY() - vertexList.get(2).getY()) + vertexList.get(2).getX();
			int startX = (int) Math.max(0, Math.floor(Math.min(x1, x2)));
			int finX = (int) Math.min(RasterizationParams.drawBuffer.getWidth(), Math.ceil(Math.max(x1, x2)));
			for (int x = startX; x < (finX + startX) / 2 + 1; x++) {
				putPxl(x, y, color, polyNormal, vertexList);
				putPxl(startX + finX - x, y, color, polyNormal, vertexList);
			}
		}

		startY = (int) Math.min(RasterizationParams.drawBuffer.getHeight(), Math.floor(vertexList.get(1).getY()));
		finY = (int) Math.max(0, Math.ceil(vertexList.get(0).getY()));
		for (int y = startY; y >= finY; y--) {
			final double x1 = (vertexList.get(1).getY() - vertexList.get(0).getY() == 0) ? vertexList.get(0).getX()
					: (y - vertexList.get(0).getY()) * (vertexList.get(1).getX() - vertexList.get(0).getX())
							/ (vertexList.get(1).getY() - vertexList.get(0).getY()) + vertexList.get(0).getX();
			final double x2 = (vertexList.get(0).getY() - vertexList.get(2).getY() == 0) ? vertexList.get(2).getX()
					: (y - vertexList.get(2).getY()) * (vertexList.get(0).getX() - vertexList.get(2).getX())
							/ (vertexList.get(0).getY() - vertexList.get(2).getY()) + vertexList.get(2).getX();
			int startX = (int) Math.max(0, Math.floor(Math.min(x1, x2)));
			int finX = (int) Math.min(RasterizationParams.drawBuffer.getWidth(), Math.ceil(Math.max(x1, x2)));
			for (int x = startX; x < (finX + startX) / 2 + 1; x++) {
				putPxl(x, y, color, polyNormal, vertexList);
				putPxl(startX + finX - x, y, color, polyNormal, vertexList);
			}
		}

		return true;
	}

	private static void putPxl(int x, int y, Color c, Vector3f polyNormal, List<VertexObject> list) {
		double[] barizenticCoordinate = BarycentricCoordinate.barizentricCalculator(x, y,
				new Vector3f[] { list.get(0).vertice, list.get(1).vertice, list.get(2).vertice });

		if (barizenticCoordinate[0] < 0 || barizenticCoordinate[1] < 0 || barizenticCoordinate[2] < 0) {
			return;
		}

		double z = (double) (barizenticCoordinate[0] * list.get(0).vertice.getZ()
				+ barizenticCoordinate[1] * list.get(1).vertice.getZ()
				+ barizenticCoordinate[2] * list.get(2).vertice.getZ());

		if (RasterizationParams.zBuffer.inZBuffer(x, y)) {
			if (z < RasterizationParams.zBuffer.get((int) x, (int) y)) {
				double toCameraX = list.get(0).vecToCamera.getX() * barizenticCoordinate[0]
						+ list.get(1).vecToCamera.getX() * barizenticCoordinate[1]
						+ list.get(2).vecToCamera.getX() * barizenticCoordinate[2];
				double toCameraY = list.get(0).vecToCamera.getY() * barizenticCoordinate[0]
						+ list.get(1).vecToCamera.getY() * barizenticCoordinate[1]
						+ list.get(2).vecToCamera.getY() * barizenticCoordinate[2];
				double toCameraZ = list.get(0).vecToCamera.getZ() * barizenticCoordinate[0]
						+ list.get(1).vecToCamera.getZ() * barizenticCoordinate[1]
						+ list.get(2).vecToCamera.getZ() * barizenticCoordinate[2];
				Vector3f vecToCamera = new Vector3f(toCameraX, toCameraY, toCameraZ);

				Color dotColor = getDotColor(c,
						new Vector3f[] { list.get(0).normal, list.get(1).normal, list.get(2).normal },
						barizenticCoordinate, vecToCamera, polyNormal, RasterizationParams.getIsLight());
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

}