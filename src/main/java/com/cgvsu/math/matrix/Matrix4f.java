package com.cgvsu.math.matrix;

import com.cgvsu.math.Global;
import com.cgvsu.math.vector.Vector4f;

public class Matrix4f {
    private final float[][] matrix;

    public Matrix4f(float[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Matrix length should be 4x4!");
        }
        this.matrix = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, 4);
        }
    }

    public float[][] getMatrix() {
        float[][] copy = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, 4);
        }
        return copy;
    }

    public boolean equals(Matrix4f other) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (Math.abs(matrix[i][j] - other.matrix[i][j]) >= Global.eps) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Matrix4f addition(Matrix4f A, Matrix4f B) {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res[i][j] = A.matrix[i][j] + B.matrix[i][j];
            }
        }
        return new Matrix4f(res);
    }

    public static Matrix4f subtraction(Matrix4f A, Matrix4f B) {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res[i][j] = A.matrix[i][j] - B.matrix[i][j];
            }
        }
        return new Matrix4f(res);
    }

    public static Matrix4f multiplication(Matrix4f A, Matrix4f B) {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    res[i][j] += A.matrix[i][k] * B.matrix[k][j];
                }
            }
        }
        return new Matrix4f(res);
    }

    public static Matrix4f transposition(Matrix4f A) {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res[j][i] = A.matrix[i][j];
            }
        }
        return new Matrix4f(res);
    }

    public static Matrix4f zeroMatrix() {
        float[][] res = new float[4][4];
        return new Matrix4f(res);
    }

    public static Matrix4f oneMatrix() {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            res[i][i] = 1;
        }
        return new Matrix4f(res);
    }

    public static Vector4f multiplyOnVector(Matrix4f A, Vector4f B) {
        float[] res = new float[4];
        res[0] = A.matrix[0][0] * B.getX() + A.matrix[0][1] * B.getY() + A.matrix[0][2] * B.getZ() + A.matrix[0][3] * B.getW();
        res[1] = A.matrix[1][0] * B.getX() + A.matrix[1][1] * B.getY() + A.matrix[1][2] * B.getZ() + A.matrix[1][3] * B.getW();
        res[2] = A.matrix[2][0] * B.getX() + A.matrix[2][1] * B.getY() + A.matrix[2][2] * B.getZ() + A.matrix[2][3] * B.getW();
        res[3] = A.matrix[3][0] * B.getX() + A.matrix[3][1] * B.getY() + A.matrix[3][2] * B.getZ() + A.matrix[3][3] * B.getW();
        return new Vector4f(res[0], res[1], res[2], res[3]);
    }
}