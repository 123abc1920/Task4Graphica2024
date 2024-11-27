package com.cgvsu.math.matrix;

import com.cgvsu.math.Global;
import com.cgvsu.math.vector.Vector3f;

public class Matrix3f {
    private float[][] matrix;

    public Matrix3f(float[][] matrix) {
        if (matrix.length != 3 || matrix[0].length != 3) {
            throw new IllegalArgumentException("Matrix length should be 3x3!");
        }
        this.matrix = new float[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, 3);
        }
    }

    public float[][] matrix() {
        float[][] copy = new float[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, 3);
        }
        return copy;
    }
    public float get(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new IndexOutOfBoundsException("Indices must be between 0 and 2!");
        }
        return matrix[row][col];
    }
    public void set(int row, int col, float value) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new IndexOutOfBoundsException("Indices must be between 0 and 2!");
        }
        matrix[row][col] = value;
    }

    public boolean equals(Matrix3f other) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (Math.abs(matrix[i][j] - other.matrix[i][j]) >= Global.eps) {
                    return false;
                }
            }
        }
        return true;
    }

    public Matrix3f add(Matrix3f other) {
        float[][] res = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                res[i][j] = this.matrix[i][j] + other.matrix[i][j];
            }
        }
        return new Matrix3f(res);
    }

    public Matrix3f sub(Matrix3f other) {
        float[][] res = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                res[i][j] = this.matrix[i][j] - other.matrix[i][j];
            }
        }
        return new Matrix3f(res);
    }

    public Matrix3f mul(Matrix3f other) {
        float[][] res = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    res[i][j] += this.matrix[i][k] * other.matrix[k][j];
                }
            }
        }
        return new Matrix3f(res);
    }

    public Matrix3f transposition() {
        float[][] res = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                res[j][i] = this.matrix[i][j];
            }
        }
        return new Matrix3f(res);
    }

    public static Matrix3f zeroMatrix() {
        float[][] res = new float[3][3];
        return new Matrix3f(res);
    }

    public static Matrix3f oneMatrix() {
        float[][] res = new float[3][3];
        for (int i = 0; i < 3; i++) {
            res[i][i] = 1;
        }
        return new Matrix3f(res);
    }

    public Vector3f multiplyOnVector(Vector3f vector) {
        float[] res = new float[3];
        for (int i = 0; i < 3; i++) {
            res[i] = this.matrix[i][0] * vector.getX() + this.matrix[i][1] * vector.getY() + this.matrix[i][2] * vector.getZ();
        }
        return new Vector3f(res[0], res[1], res[2]);
    }
}