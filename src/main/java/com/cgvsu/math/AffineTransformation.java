package com.cgvsu.math;

public class AffineTransformation {
    /* Метод для масштабирование вектора(веришины)
vector3f - вектор(вершины), который необходимо масштабировать, как итог новый вектор после масштабирования*/
    public static Vector3f scale(double x, double y, double z, Vector3f vector3f) {
        Vector4f vector4f = vector3f.translationToVector4f();
        double[][] s = new double[][]{
                {x, 0, 0, 0},
                {0, y, 0, 0},
                {0, 0, z, 0},
                {0, 0, 0, 1}};
        Matrix4f after = new Matrix4f(s);

        return after.multiplyVector(vector4f).translationVector3f();
    }

    // Получение матрицы масштабирования
    public static Matrix4f getScaleMatrix(double x, double y, double z) {
        double[][] s = new double[][]{{x, 0, 0, 0}, {0, y, 0, 0}, {0, 0, z, 0}, {0, 0, 0, 1}};
        return new Matrix4f(s);
    }

    public static Vector3f rotate(int a, int b, int c, Vector3f vector3f) {
        Vector4f vector4f = vector3f.translationToVector4f();
        double[][] rX = new double[][]{
                {1, 0, 0, 0},
                {0, (double) Math.cos(Math.toRadians(a)), (double) Math.sin(Math.toRadians(a)), 0},
                {0, (double) -Math.sin(Math.toRadians(a)), (double) Math.cos(Math.toRadians(a)), 0},
                {0, 0, 0, 1}};
        double[][] rY = new double[][]{
                {(double) Math.cos(Math.toRadians(b)), 0, (double) Math.sin(Math.toRadians(b)), 0},
                {0, 1, 0, 0},
                {(double) -Math.sin(Math.toRadians(b)), 0, (double) Math.cos(Math.toRadians(b)), 0},
                {0, 0, 0, 1}};

        double[][] rZ = new double[][]{
                {(double) Math.cos(Math.toRadians(c)), (double) Math.sin(Math.toRadians(c)), 0, 0},
                {(double) -Math.sin(Math.toRadians(c)), (double) Math.cos(Math.toRadians(c)), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};
        Matrix4f RXMatrix = new Matrix4f(rX);
        Matrix4f RYMatrix = new Matrix4f(rY);
        Matrix4f RZMatrix = new Matrix4f(rZ);

        Matrix4f RotateXYZ = RXMatrix.multiplyMatrix(RYMatrix).multiplyMatrix(RZMatrix);
        return RotateXYZ.multiplyVector(vector4f).translationVector3f();
    }
    public static Matrix4f getRotateMatrix(int a, int b, int c) {
        double[][] rX = new double[][]{
                {1, 0, 0, 0},
                {0, (double) Math.cos(Math.toRadians(a)), (double) Math.sin(Math.toRadians(a)), 0},
                {0, (double) -Math.sin(Math.toRadians(a)), (double) Math.cos(Math.toRadians(a)), 0},
                {0, 0, 0, 1}};
        double[][] rY = new double[][]{
                {(double) Math.cos(Math.toRadians(b)), 0, (double) Math.sin(Math.toRadians(b)), 0},
                {0, 1, 0, 0},
                {(double) -Math.sin(Math.toRadians(b)), 0, (double) Math.cos(Math.toRadians(b)), 0},
                {0, 0, 0, 1}};

        double[][] rZ = new double[][]{
                {(double) Math.cos(Math.toRadians(c)), (double) Math.sin(Math.toRadians(c)), 0, 0},
                {(double) -Math.sin(Math.toRadians(c)), (double) Math.cos(Math.toRadians(c)), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};

        Matrix4f RXMatrix = new Matrix4f(rX);
        Matrix4f RYMatrix = new Matrix4f(rY);
        Matrix4f RZMatrix = new Matrix4f(rZ);
        Matrix4f RotateXYZ = RXMatrix.multiplyMatrix(RYMatrix).multiplyMatrix(RZMatrix);
        return RotateXYZ;
    }
    public static Vector3f translate(double x, double y, double z, Vector3f vector3f) {
        Vector4f vector4f = vector3f.translationToVector4f();
        double[][] t = new double[][]{
                {1,0,0,x},
                {0,1,0,y},
                {0,0,1,z},
                {0,0,0,1}
        };
        Matrix4f matrix4f = new Matrix4f(t);
        return matrix4f.multiplyVector(vector4f).translationVector3f();
    }
    public static Matrix4f getTranslateMatrix(double x, double y, double z) {
        double[][] t = new double[][]{
                {1,0,0,x},
                {0,1,0,y},
                {0,0,1,z},
                {0,0,0,1}
        };
        return new Matrix4f(t);
    }
}

