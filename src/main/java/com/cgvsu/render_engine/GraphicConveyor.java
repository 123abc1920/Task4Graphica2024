package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

public class GraphicConveyor {

	public static Matrix4f rotateScaleTranslate() {
		float[][] matrix = new float[][] { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
		return new Matrix4f(matrix);
	}

	public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
		return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
	}

	public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
		Vector3f resultZ = target.sub(eye);
		Vector3f resultX = up.cross(resultZ);
		Vector3f resultY = resultZ.cross(resultX);

		resultX.normalize();
		resultY.normalize();
		resultZ.normalize();

		float[][] matrix = new float[][] { { resultX.getX(), resultY.getX(), resultZ.getX(), 0 },
				{ resultX.getY(), resultY.getY(), resultZ.getY(), 0 },
				{ resultX.getZ(), resultY.getZ(), resultZ.getZ(), 0 },
				{ -resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1 } };
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
		final float x = (vertex.getX() * matrix.get(0, 0)) + (vertex.getY() * matrix.get(1, 0))
				+ (vertex.getZ() * matrix.get(2, 0)) + matrix.get(3, 0);
		final float y = (vertex.getX() * matrix.get(0, 1)) + (vertex.getY() * matrix.get(1, 1))
				+ (vertex.getZ() * matrix.get(2, 1)) + matrix.get(3, 1);
		final float z = (vertex.getX() * matrix.get(0, 2)) + (vertex.getY() * matrix.get(1, 2))
				+ (vertex.getZ() * matrix.get(2, 2)) + matrix.get(3, 2);
		final float w = (vertex.getX() * matrix.get(0, 3)) + (vertex.getY() * matrix.get(1, 3))
				+ (vertex.getZ() * matrix.get(2, 3)) + matrix.get(3, 3);
		return new Vector3f(x / w, y / w, z / w);
	}

	public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
		return new Vector2f(vertex.getX() * width + width / 2.0F, -vertex.getY() * height + height / 2.0F);
	}
}
