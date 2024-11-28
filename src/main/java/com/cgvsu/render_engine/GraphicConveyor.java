package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import javax.vecmath.Point2f;

public class GraphicConveyor {

    public static Matrix4f translateRotateScale() {
        float[][] matrix = new float[][]{{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        return new Matrix4f(matrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX;
        Vector3f resultY;
        Vector3f resultZ;

        resultZ = target.sub(eye);
        resultX = up.vectorMultiply(resultZ);
        resultY = resultZ.vectorMultiply(resultX);

        resultX = resultX.normalize();
        resultY = resultY.normalize();
        resultZ = resultZ.normalize();

        float[][] matrixTranslation = new float[][]{
                {1, 0, 0, -resultX.dotProduct(eye)},
                {0, 1, 0, -resultY.dotProduct(eye)},
                {0, 0, 1, -resultZ.dotProduct(eye)},
                {0, 0, 0, 1}};
        Matrix4f translation = new Matrix4f(matrixTranslation);
        float[][] matrixDot = new float[][]{
                {resultX.getX(), resultX.getY(), resultX.getZ(), 0},
                {resultY.getX(), resultY.getY(), resultY.getZ(), 0},
                {resultZ.getX(), resultZ.getY(), resultZ.getZ(), 0},
                {0, 0, 0, 1}};
        Matrix4f dot = new Matrix4f(matrixDot);
        return translation.multiplyMatrix(dot);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        float[][] projection = new float[][]
                {{tangentMinusOnDegree / aspectRatio, 0, 0, 0},
                        {0, tangentMinusOnDegree, 0, 0},
                        {0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 2 * (nearPlane * farPlane) / (nearPlane - farPlane)},
                        {0, 0, 1, 0}};
        return new Matrix4f(projection);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f((width - 1) / 2.0f * vertex.getX() + (width - 1) / 2.0f, (1 - height) / 2.0F * vertex.getY() + (height - 1) / 2.0F);
    }

        /*float[][] matrix = new float[][] { { resultX.getX(), resultY.getX(), resultZ.getX(), 0 },
                { resultX.getY(), resultY.getY(), resultZ.getY(), 0 },
                { resultX.getZ(), resultY.getZ(), resultZ.getZ(), 0 },
                { -resultX.dotProduct(eye), -resultY.dotProduct(eye), -resultZ.dotProduct(eye), 1 } };
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(final float fov, final float aspectRatio, final float nearPlane,
                                       final float farPlane) {
        float[][] result = new float[4][4];
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result[0][0] = tangentMinusOnDegree / aspectRatio;
        result[1][1] = tangentMinusOnDegree;
        result[2][2] = (farPlane + nearPlane) / (farPlane - nearPlane);
        result[2][3] = 1.0F;
        result[3][2] = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);
        return new Matrix4f(result);
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float x = (vertex.getX() * matrix.getCell(0, 0)) + (vertex.getY() * matrix.getCell(1, 0))
                + (vertex.getZ() * matrix.getCell(2, 0)) + matrix.getCell(3, 0);
        final float y = (vertex.getX() * matrix.getCell(0, 1)) + (vertex.getY() * matrix.getCell(1, 1))
                + (vertex.getZ() * matrix.getCell(2, 1)) + matrix.getCell(3, 1);
        final float z = (vertex.getX() * matrix.getCell(0, 2)) + (vertex.getY() * matrix.getCell(1, 2))
                + (vertex.getZ() * matrix.getCell(2, 2)) + matrix.getCell(3, 2);
        final float w = (vertex.getX() * matrix.getCell(0, 3)) + (vertex.getY() * matrix.getCell(1, 3))
                + (vertex.getZ() * matrix.getCell(2, 3)) + matrix.getCell(3, 3);
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Vector2f(vertex.getX() * width + width / 2.0F, -vertex.getY() * height + height / 2.0F);
    }*/
}