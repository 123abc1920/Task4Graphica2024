package com.cgvsu.render_engine;

import com.cgvsu.math.Global;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;

public class GraphicConveyor {

	public static Matrix4f translateRotateScale() {
		double[][] matrix = new double[][] { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
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

		if (resultX.getLength() > Global.eps) {
			resultX = resultX.normalize();
		}
		if (resultY.getLength() > Global.eps) {
			resultY = resultY.normalize();
		}
		if (resultZ.getLength() > Global.eps) {
			resultZ = resultZ.normalize();
		}

		double[][] matrixTranslation = new double[][] { { 1, 0, 0, -resultX.dotProduct(eye) },
				{ 0, 1, 0, -resultY.dotProduct(eye) }, { 0, 0, 1, -resultZ.dotProduct(eye) }, { 0, 0, 0, 1 } };
		Matrix4f translation = new Matrix4f(matrixTranslation);
		double[][] matrixDot = new double[][] { { resultX.getX(), resultX.getY(), resultX.getZ(), 0 },
				{ resultY.getX(), resultY.getY(), resultY.getZ(), 0 },
				{ resultZ.getX(), resultZ.getY(), resultZ.getZ(), 0 }, { 0, 0, 0, 1 } };
		Matrix4f dot = new Matrix4f(matrixDot);
		return translation.multiplyMatrix(dot);
	}

	public static Matrix4f perspective(final double fov, final double aspectRatio, final double nearPlane,
			final double farPlane) {
		double tangentMinusOnDegree = (double) (1.0F / (Math.tan(fov * 0.5F)));
		double[][] projection = new double[][] { { tangentMinusOnDegree / aspectRatio, 0, 0, 0 },
				{ 0, tangentMinusOnDegree, 0, 0 }, { 0, 0, (farPlane + nearPlane) / (farPlane - nearPlane),
						2 * (nearPlane * farPlane) / (nearPlane - farPlane) },
				{ 0, 0, 1, 0 } };
		return new Matrix4f(projection);
	}

	public static Vector3f vertexToPoint(final Vector3f vertex, final int width, final int height) {
		return new Vector3f((width - 1) / 2.0f * vertex.getX() + (width - 1) / 2.0f,
				(1 - height) / 2.0F * vertex.getY() + (height - 1) / 2.0F, vertex.getZ());
	}
}