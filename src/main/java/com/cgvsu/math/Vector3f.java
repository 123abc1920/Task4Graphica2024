package com.cgvsu.math;

import com.cgvsu.math.Global;

public class Vector3f {
	private float x;
	private float y;
	private float z;

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
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

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float get(int index) {
		switch (index) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new IndexOutOfBoundsException("Index must be 0, 1, or 2 for a 3D vector.");
		}
	}

	public void set(int index, float value) {
		switch (index) {
		case 0:
			this.x = value;
			break;
		case 1:
			this.y = value;
			break;
		case 2:
			this.z = value;
			break;
		default:
			throw new IndexOutOfBoundsException("Index must be 0, 1, or 2 for a 3D vector.");
		}
	}

	public boolean equals(Vector3f other) {
		return Math.abs(x - other.getX()) < Global.eps && Math.abs(y - other.getY()) < Global.eps
				&& Math.abs(z - other.getZ()) < Global.eps;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3f normalize() {
		float len = this.length();
		if (len > Global.eps) {
			return new Vector3f(x / len, y / len, z / len);
		} else {
			throw new IllegalArgumentException("Cannot normalize a zero-length vector.");
		}
	}

	public Vector3f add(Vector3f other) {
		return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
	}

	public Vector3f sub(Vector3f other) {
		return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
	}

	public Vector3f mul(float scalar) {
		return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
	}

	public Vector3f div(float scalar) {
		if (scalar < Global.eps) {
			throw new IllegalArgumentException("Division by zero is not allowed.");
		} else {
			return new Vector3f(this.x / (float) scalar, this.y / (float) scalar, this.z / (float) scalar);
		}
	}

	public float dot(Vector3f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	public Vector3f cross(Vector3f other) {
		return new Vector3f(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z,
				this.x * other.y - this.y * other.x);
	}
}