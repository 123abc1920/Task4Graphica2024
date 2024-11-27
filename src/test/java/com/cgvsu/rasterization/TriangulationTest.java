package com.cgvsu.rasterization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.cgvsu.math.vector.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TriangulationTest {
    private final static Random RANDOM = new Random();

    private final static List<Vector2f> TRIANGLE = Arrays.asList(
        new Vector2f(0, 0),
        new Vector2f(3, 0),
        new Vector2f(0, 4)
    );

    private final static List<Vector2f> SELF_INTERSECTING_POLYGON = Arrays.asList(
        new Vector2f(0, 0),
        new Vector2f(0, 2),
        new Vector2f(-1, 1),
        new Vector2f(2, 0)
    );

    @Test
    public void testConvexTriangulation() {
        List<int[]> triangleList = Triangulation.convexPolygonTriangulate(Arrays.asList(0, 1, 2, 3, 4));
        Assertions.assertArrayEquals(triangleList.get(0), new int[]{0, 2, 1});
        Assertions.assertArrayEquals(triangleList.get(1), new int[]{0, 3, 2});
        Assertions.assertArrayEquals(triangleList.get(2), new int[]{0, 4, 3});
    }

    @Test
    public void testIncorrectVertexIndicesCount() {
        try {
            Triangulation.convexPolygonTriangulate(2);
            Assertions.fail();
        } catch (IllegalArgumentException exception) {
            String expectedError = "Not enough vertex indices for a polygon";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testVertexIndexOutsideOfVertices() {
        try {
            Triangulation.earClippingTriangulate(TRIANGLE, Arrays.asList(1, 2, 3));
            Assertions.fail();
        } catch (IllegalArgumentException exception) {
            String expectedError = "Vertex index 3 is outside of vertex list of length 3";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testSelfIntersectingPolygon() {
        try {
            Triangulation.earClippingTriangulate(SELF_INTERSECTING_POLYGON);
            Assertions.fail();
        } catch (TriangulationException exception) {
            String expectedError = "Polygon has self-intersections";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCrossProduct() {
        float cwCrossProduct = Triangulation.crossProduct(
            TRIANGLE.get(1),
            TRIANGLE.get(0),
            TRIANGLE.get(2)
        );

        float ccwCrossProduct = Triangulation.crossProduct(
            TRIANGLE.get(2),
            TRIANGLE.get(0),
            TRIANGLE.get(1)
        );

        float zeroCrossProduct = Triangulation.crossProduct(
            TRIANGLE.get(1),
            TRIANGLE.get(0),
            TRIANGLE.get(1)
        );

        Assertions.assertEquals(-12, cwCrossProduct);
        Assertions.assertEquals(12, ccwCrossProduct);
        // float my beloved
        Assertions.assertEquals(0, Math.abs(zeroCrossProduct));
    }

    @Test
    public void testPointWithinTriangle() {
        Assertions.assertTrue(Triangulation.isPointInTriangle(
            TRIANGLE.get(1),
            TRIANGLE.get(0),
            TRIANGLE.get(2),
            new Vector2f(1, 1)
        ));

        Assertions.assertFalse(Triangulation.isPointInTriangle(
            TRIANGLE.get(1),
            TRIANGLE.get(0),
            TRIANGLE.get(2),
            new Vector2f(10, 10)
        ));
    }

    @Test
    public void testEdgeLength() {
        Assertions.assertEquals(5, Triangulation.edgeLength(
            TRIANGLE.get(1),
            TRIANGLE.get(2)
        ));
    }

    @RepeatedTest(10)
    public void testRandomPolygon() {
        int verticesCount = 10;
        float size = 10;
        List<Vector2f> randomPolygon = new ArrayList<>(10);

        for (int i = 0; i < verticesCount; i++) {
            float randomSize = RANDOM.nextFloat(size, size + size);
            randomPolygon.add(new Vector2f(
                (float) (Math.cos(i * 2 * Math.PI / verticesCount) * randomSize),
                (float) (Math.sin(i * 2 * Math.PI / verticesCount) * randomSize)
            ));
        }

        List<int[]> triangles = Triangulation.earClippingTriangulate(randomPolygon);
        Assertions.assertEquals(triangles.size(), verticesCount - 2);
    }
}
