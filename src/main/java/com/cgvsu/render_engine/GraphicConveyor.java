package com.cgvsu.render_engine;
import com.cgvsu.math.*;


public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        float[][] matrix = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        /*Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);*/
        Vector3f resultZ = Vector3f.normalize(Vector3f.subtraction(eye, target));
        Vector3f resultX = Vector3f.normalize(Vector3f.cross(up, resultZ));
        Vector3f resultY = Vector3f.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[][] matrix = new float[][]{
                {resultX.getX(), resultY.getX(), resultZ.getX(), 0},
                {resultX.getY(), resultY.getY(), resultZ.getY(), 0},
                {resultX.getZ(), resultY.getZ(), resultZ.getZ(), 0},
                {-resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}
        };
        return new Matrix4f(matrix);
    }

        public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float[][] resultMatrix = new float[4][4];
        float tangent = (float) Math.tan(Math.toRadians(fov) / 2);

        resultMatrix[0][0] = 1 / (aspectRatio * tangent);
        resultMatrix[1][1] = 1 / tangent;
        resultMatrix[2][2] = -(farPlane + nearPlane) / (farPlane - nearPlane);
        resultMatrix[2][3] = -1;
        resultMatrix[3][2] = -(2 * farPlane * nearPlane) / (farPlane - nearPlane);

        return new Matrix4f(resultMatrix);
    }


    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        float x = vertex.getX() * matrix.get(0, 0) + vertex.getY() * matrix.get(1, 0) + vertex.getZ() * matrix.get(2, 0) + matrix.get(3, 0);
        float y = vertex.getX() * matrix.get(0, 1) + vertex.getY() * matrix.get(1, 1) + vertex.getZ() * matrix.get(2, 1) + matrix.get(3, 1);
        float z = vertex.getX() * matrix.get(0, 2) + vertex.getY() * matrix.get(1, 2) + vertex.getZ() * matrix.get(2, 2) + matrix.get(3, 2);
        float w = vertex.getX() * matrix.get(0, 3) + vertex.getY() * matrix.get(1, 3) + vertex.getZ() * matrix.get(2, 3) + matrix.get(3, 3);

        return new Vector3f(x / w, y / w, z / w);
    }


    /*public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.getX() * width + width / 2.0F, -vertex.getY() * height + height / 2.0F);
    }*/
    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        float x = vertex.getX() * width + width / 2.0F;
        float y = -vertex.getY() * height + height / 2.0F;
        return new Vector2f(x, y);
    }



}
