package com.cgvsu.rasterization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.cgvsu.math.Global;
import com.cgvsu.math.vector.Vector2f;


public final class Triangulation {

	private Triangulation() {
		throw new UnsupportedOperationException("Cannot be instantiated.");
	}

	private static void checkVertexIndicesCount(int n) {
		if (n < 3) {
			throw new IllegalArgumentException("Not enough vertex indices for a polygon");
		}
	}

	public static List<int[]> convexPolygonTriangulate(List<Integer> vertexIndices) {
		int vertexIndicesCount = vertexIndices.size();
		checkVertexIndicesCount(vertexIndicesCount);

		List<int[]> triangles = new ArrayList<>(vertexIndicesCount - 2);

		for (int i = 2; i < vertexIndicesCount; i++) {
			triangles.add(new int[] { 0, vertexIndices.get(i), vertexIndices.get(i - 1) });
		}

		return triangles;
	}

	public static List<int[]> convexPolygonTriangulate(int n) {
		List<Integer> vertexIndices = IntStream.rangeClosed(0, n - 1).boxed().toList();

		return convexPolygonTriangulate(vertexIndices);
	}

	public static <T extends Vector2f> List<int[]> earClippingTriangulate(List<T> vertices) {
		List<Integer> vertexIndices = IntStream.rangeClosed(0, vertices.size() - 1).boxed().toList();

		return earClippingTriangulate(vertices, vertexIndices);
	}

	public static <T extends Vector2f> List<int[]> earClippingTriangulate(List<T> vertices,
			List<Integer> vertexIndices) {
		int vertexIndicesCount = vertexIndices.size();
		checkVertexIndicesCount(vertexIndicesCount);

		int vertexCount = vertices.size();
		for (Integer vertexIndex : vertexIndices) {
			if (vertexIndex >= vertexCount) {
				throw new IllegalArgumentException(String
						.format("Vertex index %d is outside of vertex list of length %d", vertexIndex, vertexCount));
			}
		}

		List<int[]> triangles = new ArrayList<>(vertexIndicesCount - 2);
		List<Integer> potentialEars = new ArrayList<>(vertexIndices);
		int potentialEarsCount = vertexIndicesCount;

		boolean isCCW = isCounterClockwise(vertices, vertexIndices);

		for (boolean hasClippedEars = true; hasClippedEars;) {
			hasClippedEars = false;
			for (int i = 1; i < potentialEarsCount - 1; i++) {
				int prevVertexIndex = potentialEars.get(i - 1);
				int curVertexIndex = potentialEars.get(i);
				int nextVertexIndex = potentialEars.get(i + 1);

				Vector2f prevVertex = vertices.get(prevVertexIndex);
				Vector2f curVertex = vertices.get(curVertexIndex);
				Vector2f nextVertex = vertices.get(nextVertexIndex);

				float crossProduct = crossProduct(prevVertex, curVertex, nextVertex);
				if ((isCCW ? crossProduct : -crossProduct) < Global.eps) {
					continue;
				}

				boolean isEar = true;

				for (int j = 0; j < vertexIndicesCount; j++) {
					int checkedVertexIndex = vertexIndices.get(j);
					if (checkedVertexIndex == prevVertexIndex || checkedVertexIndex == curVertexIndex
							|| checkedVertexIndex == nextVertexIndex) {
						continue;
					}

					Vector2f checkedVertex = vertices.get(checkedVertexIndex);
					if (isPointInTriangle(prevVertex, curVertex, nextVertex, checkedVertex)) {
						isEar = false;
						break;
					}
				}

				if (isEar) {
					triangles.add(new int[] { prevVertexIndex, curVertexIndex, nextVertexIndex });
					potentialEars.remove(i);
					potentialEarsCount--;
					i--;
					hasClippedEars = true;
				}
			}
		}

		if (triangles.size() != vertexIndicesCount - 2) {
			throw new TriangulationException("Polygon has self-intersections");
		}

		return triangles;
	}

	private static <T extends Vector2f> boolean isCounterClockwise(List<T> vertices, List<Integer> vertexIndices) {
		float area = 0;
		int vertexIndicesCount = vertexIndices.size();

		Vector2f prevVertex = vertices.get(vertexIndices.get(0));
		for (int i = 1; i <= vertexIndicesCount; i++) {
			Vector2f currentVertex = vertices.get(vertexIndices.get(i % vertexIndicesCount));

			area += (currentVertex.getX() - prevVertex.getX()) * (currentVertex.getY() + prevVertex.getY());
			prevVertex = currentVertex;
		}

		return area < 0;
	}

	static float crossProduct(Vector2f a, Vector2f b, Vector2f c) {
		float dx1 = b.getX() - a.getX();
		float dy1 = b.getY() - a.getY();
		float dx2 = c.getX() - a.getX();
		float dy2 = c.getY() - a.getY();

		return dx1 * dy2 - dx2 * dy1;
	}

	static boolean isPointInTriangle(Vector2f a, Vector2f b, Vector2f c, Vector2f p) {
		float check1 = crossProduct(a, b, p);
		float check2 = crossProduct(p, b, c);
		float check3 = crossProduct(p, c, a);

		return (check1 >= -Global.eps && check2 >= -Global.eps && check3 >= -Global.eps)
				|| (check1 <= Global.eps && check2 <= Global.eps && check3 <= Global.eps);
	}

	static float edgeLength(Vector2f a, Vector2f b) {
		float dx = a.getX() - b.getX();
		float dy = a.getY() - b.getY();
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
}