package com.cgvsu.math;

import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector4f;

public class Matrix4f {
    private static final float esp = 1e-4f;
    private float[][] matrix;

    public Matrix4f() {
        this.matrix = new float[4][4];
    }

    public Matrix4f(boolean isUnitMatrix) {
        if (isUnitMatrix) {
            this.matrix = new float[][]{
                    {1, 0, 0, 0},
                    {0, 1, 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
            };
        } else {
            this.matrix = new float[4][4];
        }
    }

    public Matrix4f(float[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Предоставленная матрица должна быть матрицей 4 на 4");
        }
        this.matrix = matrix;
    }

    public float[][] getMatrix() {
        return matrix;
    }

    public float getCell(int row, int col) {
        return matrix[row][col];
    }

    public void printMatrix() {
        System.out.println("Matrix: ");
        for (float[] floats : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(floats[j] + " ");
            }
            System.out.println();
        }
    }

    public Matrix4f sum(Matrix4f matrix4f) {
        if (matrix4f.getMatrix().length != 4 || matrix4f.getMatrix()[0].length != 4) {
            throw new IllegalArgumentException();
        }
        float[][] values = new float[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                values[i][j] = matrix[i][j] + matrix4f.getCell(i, j);
            }
        }
        return new Matrix4f(values);
    }

    public Matrix4f sub(Matrix4f matrix4f) {
        if (matrix4f.getMatrix().length != 4 || matrix4f.getMatrix()[0].length != 4) {
            throw new IllegalArgumentException("Предоставленная матрица должна быть матрицей 4 на 4.");
        }
        float[][] values = new float[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                values[i][j] = matrix[i][j] - matrix4f.getCell(i, j);
            }
        }
        return new Matrix4f(values);
    }

    public Vector4f multiplyVector(Vector4f vectorCol) {
        if (vectorCol == null) {
            throw new NullPointerException("Предоставленный вектор не может быть нулевым");
        }

        float[] values = new float[4];
        for (int i = 0; i < matrix.length; i++) {
            values[i] = 0;
            for (int j = 0; j < matrix[0].length; j++) {
                values[i] += matrix[i][j] * vectorCol.get(j);
            }
        }
        return new Vector4f(values[0], values[1], values[2], values[3]);
    }

    public Vector3f multiplyVectorDivW(Vector3f vectorCol3f) {
        if (vectorCol3f == null) {
            throw new NullPointerException("Предоставленный вектор не может быть нулевым");
        }
        Vector4f vector4fCol = vectorCol3f.translationToVector4f();

        float[] values = new float[4];
        for (int i = 0; i < matrix.length; i++) {
            values[i] = 0;
            for (int j = 0; j < matrix[0].length; j++) {
                values[i] += matrix[i][j] * vector4fCol.get(j);
            }
        }
        return new Vector3f(values[0] / values[3], values[1] / values[3], values[2] / values[3]);
    }

    public Matrix4f multiplyMatrix(Matrix4f matrix4f) {
        if (matrix4f.getMatrix().length != 4 || matrix4f.getMatrix()[0].length != 4) {
            throw new IllegalArgumentException("Предоставленная матрица должна быть матрицей 4 на 4.");
        }
        float[][] values = new float[4][4];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                values[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    values[i][j] += matrix[i][k] * matrix4f.getCell(k, j);
                }
            }
        }
        return new Matrix4f(values);
    }

    public Matrix4f transpose() {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Предоставленная матрица должна быть матрицей 4 на 4.");
        }
        float[][] transposed = new float[4][4];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return new Matrix4f(transposed);
    }

    public float determinant() {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Предоставленная матрица должна быть матрицей 4 на 4.");
        }

        return matrix[0][0] * (matrix[1][1] * (matrix[2][2] * matrix[3][3] - matrix[2][3] * matrix[3][2])
                - matrix[1][2] * (matrix[2][1] * matrix[3][3] - matrix[2][3] * matrix[3][1])
                + matrix[1][3] * (matrix[2][1] * matrix[3][2] - matrix[2][2] * matrix[3][1]))
                - matrix[0][1] * (matrix[1][0] * (matrix[2][2] * matrix[3][3] - matrix[2][3] * matrix[3][2])
                - matrix[1][2] * (matrix[2][0] * matrix[3][3] - matrix[2][3] * matrix[3][0])
                + matrix[1][3] * (matrix[2][0] * matrix[3][2] - matrix[2][2] * matrix[3][0]))
                + matrix[0][2] * (matrix[1][0] * (matrix[2][1] * matrix[3][3] - matrix[2][3] * matrix[3][1])
                - matrix[1][1] * (matrix[2][0] * matrix[3][3] - matrix[2][3] * matrix[3][0])
                + matrix[1][3] * (matrix[2][0] * matrix[3][1] - matrix[2][1] * matrix[3][0]))
                - matrix[0][3] * (matrix[1][0] * (matrix[2][1] * matrix[3][2] - matrix[2][2] * matrix[3][1])
                - matrix[1][1] * (matrix[2][0] * matrix[3][2] - matrix[2][2] * matrix[3][0])
                + matrix[1][2] * (matrix[2][0] * matrix[3][1] - matrix[2][1] * matrix[3][0]));
    }

    public boolean equalsAns(Matrix4f matrix4f) {
        boolean flag = false;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == matrix4f.getCell(i, j)) {
                    flag = true;
                } else return false;
            }
        }
        return flag;
    }
}