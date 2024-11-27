package com.cgvsu.math;

import com.cgvsu.math.matrix.Matrix4f;
import com.cgvsu.math.vector.Vector4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathTestMatrix4f {

    @Test
    public void testConstructorAndGet() {
        float[][] initialMatrix = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Matrix4f matrix = new Matrix4f(initialMatrix);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(initialMatrix[i][j], matrix.get(i, j));
            }
        }
    }

    @Test
    public void testSet() {
        Matrix4f matrix = Matrix4f.zeroMatrix();
        matrix.set(0, 0, 5.0f);
        assertEquals(5.0f, matrix.get(0, 0));
    }

    @Test
    public void testAdd() {
        float[][] matrixA = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        float[][] matrixB = {
                {16, 15, 14, 13},
                {12, 11, 10, 9},
                {8, 7, 6, 5},
                {4, 3, 2, 1}
        };
        Matrix4f A = new Matrix4f(matrixA);
        Matrix4f B = new Matrix4f(matrixB);

        Matrix4f result = A.add(B);
        float[][] expected = {
                {17, 17, 17, 17},
                {17, 17, 17, 17},
                {17, 17, 17, 17},
                {17, 17, 17, 17}
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(expected[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testSub() {
        float[][] matrixA = {
                {10, 10, 10, 10},
                {10, 10, 10, 10},
                {10, 10, 10, 10},
                {10, 10, 10, 10}
        };
        float[][] matrixB = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Matrix4f A = new Matrix4f(matrixA);
        Matrix4f B = new Matrix4f(matrixB);

        Matrix4f result = A.sub(B);
        float[][] expected = {
                {9, 8, 7, 6},
                {5, 4, 3, 2},
                {1, 0, -1, -2},
                {-3, -4, -5, -6}
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(expected[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testMul() {
        float[][] matrixA = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        float[][] matrixB = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        Matrix4f A = new Matrix4f(matrixA);
        Matrix4f B = new Matrix4f(matrixB);

        Matrix4f result = A.mul(B);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(matrixA[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testTransposition() {
        float[][] matrixA = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Matrix4f A = new Matrix4f(matrixA);

        Matrix4f result = A.transposition();
        float[][] expected = {
                {1, 5, 9, 13},
                {2, 6, 10, 14},
                {3, 7, 11, 15},
                {4, 8, 12, 16}
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(expected[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testZeroMatrix() {
        Matrix4f zeroMatrix = Matrix4f.zeroMatrix();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0f, zeroMatrix.get(i, j));
            }
        }
    }

    @Test
    public void testOneMatrix() {
        Matrix4f oneMatrix = Matrix4f.oneMatrix();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    assertEquals(1.0f, oneMatrix.get(i, j));
                } else {
                    assertEquals(0.0f, oneMatrix.get(i, j));
                }
            }
        }
    }

    @Test
    public void testMultiplyOnVector() {
        float[][] matrixA = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Matrix4f A = new Matrix4f(matrixA);

        Vector4f vector = new Vector4f(1, 2, 3, 4);
        Vector4f result = A.multiplyOnVector(vector);

        Vector4f expected = new Vector4f(30, 70, 110, 150);

        assertEquals(expected.getX(), result.getX());
        assertEquals(expected.getY(), result.getY());
        assertEquals(expected.getZ(), result.getZ());
        assertEquals(expected.getW(), result.getW());
    }
}
