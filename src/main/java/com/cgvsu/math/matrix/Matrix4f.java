package com.cgvsu.math.matrix;

import com.cgvsu.math.Global;
import com.cgvsu.math.vector.Vector4f;

public class Matrix4f {
    private float[][] matrix;

    public Matrix4f(float[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Matrix length should be 4x4!");
        }
        this.matrix = new float[4][4];
    }

    public float[][] matrix() {
        float[][] copy = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, 4);
        }
        return copy;
    }

    public float get(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new IndexOutOfBoundsException("Indices must be between 0 and 3!");
        }
        return matrix[row][col];
    }

    public void set(int row, int col, float value) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new IndexOutOfBoundsException("Indices must be between 0 and 3!");
        }
        this.matrix[row][col] = value;
    }

    public boolean equals(Matrix4f other) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (Math.abs(this.matrix[i][j] - other.matrix[i][j]) >= Global.eps) {
                    return false;
                }
            }
        }
        return true;
    }

    public Matrix4f add(Matrix4f other) {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res[i][j] = this.matrix[i][j] + other.matrix[i][j];
            }
        }
        return new Matrix4f(res);
    }

    public Matrix4f sub(Matrix4f other) {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res[i][j] = this.matrix[i][j] - other.matrix[i][j];
            }
        }
        return new Matrix4f(res);
    }

    public Matrix4f mul(Matrix4f other) {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    res[i][j] += this.matrix[i][k] * other.matrix[k][j];
                }
            }
        }
        return new Matrix4f(res);
    }

    public Matrix4f transposition() {
        float[][] res = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res[j][i] = this.matrix[i][j];
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

    public Vector4f multiplyOnVector(Vector4f vector) {
        float[] res = new float[4];
        for (int i = 0; i < 4; i++) {
            res[i] = this.matrix[i][0] * vector.getX()
                    + this.matrix[i][1] * vector.getY()
                    + this.matrix[i][2] * vector.getZ()
                    + this.matrix[i][3] * vector.getW();
        }
        return new Vector4f(res[0], res[1], res[2], res[3]);
    }
}
