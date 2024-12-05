package com.cgvsu.triangulation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.cgvsu.math.Global;
import com.cgvsu.math.Matrix2f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

public final class Triangulation {

	private Triangulation() {
		throw new UnsupportedOperationException("Cannot be instantiated.");
	}

	private static void checkVertexIndicesCount(int n) {
		if (n < 3) {
			throw new IllegalArgumentException("Not enough vertex indices for a polygon");
		}
	}

	public static void triangulateModel(Model m) {
		ArrayList<Polygon> ps = new ArrayList<>();
		for (Polygon p : m.polygons) {
			List<int[]> l = Triangulation.convexPolygonTriangulate(p.getVertexIndices());
			List<int[]> l1 = Triangulation.convexPolygonTriangulate(p.getTextureVertexIndices());
			for (int i = 0; i < l.size(); i++) {
				ArrayList<Integer> newVertices = new ArrayList<>();
				ArrayList<Integer> newTVertices = new ArrayList<>();
				for (int j = 0; j < l.get(i).length; j++) {
					newVertices.add(l.get(i)[j]);
					newTVertices.add(l1.get(i)[j]);
				}
				Polygon newP = new Polygon();
				newP.setVertexIndices(newVertices);
				newP.setTextureVertexIndices(newTVertices);
				ps.add(newP);
			}
		}

		m.polygons = ps;
	}

	public static List<int[]> convexPolygonTriangulate(List<Integer> vertexIndices) {
		int vertexIndicesCount = vertexIndices.size();
		checkVertexIndicesCount(vertexIndicesCount);

		List<int[]> triangles = new ArrayList<>(vertexIndicesCount - 2);

		for (int i = 2; i < vertexIndicesCount; i++) {
			triangles.add(new int[] { vertexIndices.get(0), vertexIndices.get(i), vertexIndices.get(i - 1) });
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

				double crossProduct = direction(prevVertex, curVertex, nextVertex);
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
		double area = 0;
		int vertexIndicesCount = vertexIndices.size();

		Vector2f prevVertex = vertices.get(vertexIndices.get(0));
		for (int i = 1; i <= vertexIndicesCount; i++) {
			Vector2f currentVertex = vertices.get(vertexIndices.get(i % vertexIndicesCount));

			area += (currentVertex.getX() - prevVertex.getX()) * (currentVertex.getY() + prevVertex.getY());
			prevVertex = currentVertex;
		}

		return area < 0;
	}

	public static double direction(Vector2f a, Vector2f b, Vector2f c) {
		double dx1 = b.getX() - a.getX();
		double dy1 = b.getY() - a.getY();
		double dx2 = c.getX() - a.getX();
		double dy2 = c.getY() - a.getY();

		Matrix2f matrix = new Matrix2f();
		matrix.setCell(0, 0, dx1);
		matrix.setCell(0, 1, dy1);
		matrix.setCell(1, 0, dx2);
		matrix.setCell(1, 1, dy2);

		return matrix.determinant();
	}

	public static boolean isPointInTriangle(Vector2f a, Vector2f b, Vector2f c, Vector2f p) {
		double check1 = direction(a, b, p);
		double check2 = direction(p, b, c);
		double check3 = direction(p, c, a);

		return (check1 >= -Global.eps && check2 >= -Global.eps && check3 >= -Global.eps)
				|| (check1 <= Global.eps && check2 <= Global.eps && check3 <= Global.eps);
	}

	public static double edgeLength(Vector2f a, Vector2f b) {
		double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		return (double) Math.sqrt(dx * dx + dy * dy);
	}
}