package com.cgvsu.math;
import com.cgvsu.math.matrix.Matrix3f;
import com.cgvsu.math.vector.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class MathTestMatrix3f {

    @Test
    public void testConstructorAndGet() {
        float[][] initialMatrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        Matrix3f matrix = new Matrix3f(initialMatrix);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(initialMatrix[i][j], matrix.get(i, j));
            }
        }
    }

    @Test
    public void testSet() {
        Matrix3f matrix = Matrix3f.zeroMatrix();
        matrix.set(0, 0, 5.0f);
        assertEquals(5.0f, matrix.get(0, 0));
    }

    @Test
    public void testAdd() {
        float[][] matrixA = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        float[][] matrixB = {
                {9, 8, 7},
                {6, 5, 4},
                {3, 2, 1}
        };
        Matrix3f A = new Matrix3f(matrixA);
        Matrix3f B = new Matrix3f(matrixB);

        Matrix3f result = A.add(B);
        float[][] expected = {
                {10, 10, 10},
                {10, 10, 10},
                {10, 10, 10}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(expected[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testSub() {
        float[][] matrixA = {
                {5, 5, 5},
                {5, 5, 5},
                {5, 5, 5}
        };
        float[][] matrixB = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        Matrix3f A = new Matrix3f(matrixA);
        Matrix3f B = new Matrix3f(matrixB);

        Matrix3f result = A.sub(B);
        float[][] expected = {
                {4, 3, 2},
                {1, 0, -1},
                {-2, -3, -4}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(expected[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testMul() {
        float[][] matrixA = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        float[][] matrixB = {
                {9, 8, 7},
                {6, 5, 4},
                {3, 2, 1}
        };
        Matrix3f A = new Matrix3f(matrixA);
        Matrix3f B = new Matrix3f(matrixB);

        Matrix3f result = A.mul(B);
        float[][] expected = {
                {30, 24, 18},
                {84, 69, 54},
                {138, 114, 90}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(expected[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testTransposition() {
        float[][] matrixA = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        Matrix3f A = new Matrix3f(matrixA);

        Matrix3f result = A.transposition();
        float[][] expected = {
                {1, 4, 7},
                {2, 5, 8},
                {3, 6, 9}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(expected[i][j], result.get(i, j));
            }
        }
    }

    @Test
    public void testZeroMatrix() {
        Matrix3f zeroMatrix = Matrix3f.zeroMatrix();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0.0f, zeroMatrix.get(i, j));
            }
        }
    }

    @Test
    public void testOneMatrix() {
        Matrix3f oneMatrix = Matrix3f.oneMatrix();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        Matrix3f A = new Matrix3f(matrixA);

        Vector3f vector = new Vector3f(1, 2, 3);
        Vector3f result = A.multiplyOnVector(vector);

        Vector3f expected = new Vector3f(14, 32, 50);

        assertEquals(expected.getX(), result.getX());
        assertEquals(expected.getY(), result.getY());
        assertEquals(expected.getZ(), result.getZ());
    }
}