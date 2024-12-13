package com.cgvsu.normals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cgvsu.math.Matrix3f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

public class FindNormals {
	public static ArrayList<Vector3f> findNormals(Model m) {
		List<Polygon> polygons = m.getPolygons();
		List<Vector3f> vertices = m.getVertices();
		/*List<Polygon> polygons = m.polygons;
		List<Vector3f> vertices = m.vertices;*/

		ArrayList<Vector3f> temporaryNormals = new ArrayList<>();
		ArrayList<Vector3f> normals = new ArrayList<>();

		for (Polygon p : polygons) {
			temporaryNormals.add(FindNormals.findPolygonsNormals(vertices.get(p.getVertexIndices().get(0)),
					vertices.get(p.getVertexIndices().get(1)), vertices.get(p.getVertexIndices().get(2))));
		}

		Map<Integer, Set<Vector3f>> vertexPolygonsMap = new HashMap<>();
		for (int j = 0; j < polygons.size(); j++) {
			List<Integer> vertexIndices = polygons.get(j).getVertexIndices();
			Vector3f vec = temporaryNormals.get(j);
			for (Integer index : vertexIndices) {
		        vertexPolygonsMap.computeIfAbsent(index, k -> new HashSet<>()).add(vec);
		    }
		}

		for (int i = 0; i < vertices.size(); i++) {
			normals.add(findVertexNormals(vertexPolygonsMap.get(i)));
		}

		return normals;
	}

	public static Vector3f findPolygonsNormals(Vector3f... vs) {
		Vector3f a = vs[0].sub(vs[1]);
		Vector3f b = vs[0].sub(vs[2]);

		Vector3f c = a.vectorMultiply(b);
		if (determinant(a, b, c) < 0) {
			c = b.vectorMultiply(a);
		}

		return c.normalize();
	}

	public static Vector3f findVertexNormals(Set<Vector3f> vs) {
		Vector3f result = new Vector3f();

		for (Vector3f f : vs) {
			result = result.add(f);
		}
		result = result.divScalar(vs.size());

		return result.normalize();
	}

	public static double determinant(Vector3f a, Vector3f b, Vector3f c) {
		return new Matrix3f(new double[][] { { a.getX(), a.getY(), a.getZ() }, { b.getX(), b.getY(), b.getZ() },
				{ c.getX(), c.getY(), c.getZ() } }).determinate();
	}
}
