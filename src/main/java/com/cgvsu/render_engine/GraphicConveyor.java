package com.cgvsu.render_engine;
import javax.vecmath.*;
import com.cgvsu.math.matrix.Matrix4f;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        float[][] matrix = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};
        return new Matrix4f(matrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[][] matrix = new float[][]{
                {resultX.x, resultY.x, resultZ.x, 0},
                {resultX.y, resultY.y, resultZ.y, 0},
                {resultX.z, resultY.z, resultZ.z, 0},
                {-resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}};
        return new Matrix4f(matrix);
    }

    /*public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float[][] result = new float[4][4];
        //Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0,0, tangentMinusOnDegree / aspectRatio) ;
        result.set(1, 1, tangentMinusOnDegree);
        result.set(2, 2, (farPlane + nearPlane) / (nearPlane - farPlane));
        result.set(2, 3, 1.0F);
        result.set(3, 2, (2 * nearPlane * farPlane) / (nearPlane - farPlane));
        return new Matrix4f(result);
    }*/
    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = Matrix4f.zeroMatrix();
        float tangentMinusOnDegree = (float) (1.0F / Math.tan(fov * 0.5F));
        result.set(0, 0, tangentMinusOnDegree / aspectRatio);
        result.set(1, 1, tangentMinusOnDegree);
        result.set(2, 2, (farPlane + nearPlane) / (nearPlane - farPlane));
        result.set(2, 3, -1.0F);
        result.set(3, 2, (2 * nearPlane * farPlane) / (nearPlane - farPlane));

        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float x = (vertex.x * matrix.get(0,0)) + (vertex.y * matrix.get(1,0)) + (vertex.z * matrix.get(2,0)) + matrix.get(3,0);
        final float y = (vertex.x * matrix.get(0,1)) + (vertex.y * matrix.get(1,1)) + (vertex.z * matrix.get(2,1)) + matrix.get(3,1);
        final float z = (vertex.x * matrix.get(0,2)) + (vertex.y * matrix.get(1,2)) + (vertex.z * matrix.get(2,2)) + matrix.get(3,2);
        final float w = (vertex.x * matrix.get(0,3)) + (vertex.y * matrix.get(1,3)) + (vertex.z * matrix.get(2,3)) + matrix.get(3,3);
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
