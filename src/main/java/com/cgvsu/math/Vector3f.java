package com.cgvsu.math;

public class Vector3f {
	private float x;
	private float y;
	private float z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public float get(int index) {
		switch (index) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		}
		throw new IllegalArgumentException("index out of bounds");
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public Vector3f add(Vector3f v) {
		return new Vector3f(x + v.getX(), y + v.getY(), z + v.getZ());
	}

	public void addVectorThis(Vector3f v) {
		this.x += v.getX();
		this.y += v.getY();
		this.z += v.getZ();
	}

	public Vector3f sub(Vector3f v) {
		return new Vector3f(x - v.getX(), y - v.getY(), z - v.getZ());
	}

	public Vector3f multiplyScalar(float a) {
		return new Vector3f(x * a, y * a, z * a);
	}

	public Vector3f divScalar(float a) {
		if (Math.abs(a) < Global.eps) {
			throw new ArithmeticException("Division by zero");
		} else {
			return new Vector3f(x / a, y / a, z / a);
		}
	}

	public float getLength() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3f normalize() {
		float l = getLength();
		if (Math.abs(l) > Global.eps) {
			return new Vector3f(x / l, y / l, z / l);
		} else {
			throw new IllegalArgumentException("Division by zero or near-zero value");
		}
	}

	public float dotProduct(Vector3f v) {
		return this.x * v.getX() + this.y * v.getY() + this.z * v.getZ();
	}

	public Vector3f vectorMultiply(Vector3f v) {
		float newX = this.y * v.getZ() - this.z * v.getY();
		float newY = this.z * v.getX() - this.x * v.getZ();
		float newZ = this.x * v.getY() - this.y * v.getX();
		return new Vector3f(newX, newY, newZ);
	}

	public Vector4f translationToVector4f() {
		return new Vector4f(getX(), getY(), getZ(), 1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Vector3f other))
			return false;
		return Math.abs(x - other.x) < Global.eps && Math.abs(y - other.y) < Global.eps
				&& Math.abs(z - other.z) < Global.eps;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public static Vector3f multiplyMatrix4ByVector3(Matrix4f matrix, Vector3f vertex) {
		float x = matrix.getCell(0, 0) * vertex.getX() + matrix.getCell(0, 1) * vertex.getY()
				+ matrix.getCell(0, 2) * vertex.getZ() + matrix.getCell(0, 3);
		float y = matrix.getCell(1, 0) * vertex.getX() + matrix.getCell(1, 1) * vertex.getY()
				+ matrix.getCell(1, 2) * vertex.getZ() + matrix.getCell(1, 3);
		float z = matrix.getCell(2, 0) * vertex.getX() + matrix.getCell(2, 1) * vertex.getY()
				+ matrix.getCell(2, 2) * vertex.getZ() + matrix.getCell(2, 3);
		float w = matrix.getCell(3, 0) * vertex.getX() + matrix.getCell(3, 1) * vertex.getY()
				+ matrix.getCell(3, 2) * vertex.getZ() + matrix.getCell(3, 3);

		// Деление на W для преобразования в нормализованное устройство
		return new Vector3f(x / w, y / w, z / w);
	}
}
