package com.cgvsu.math;

public class Vector2f {
	private static final double eps = 1e-4f;
	private double x;
	private double y;

	public Vector2f(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double get(int index) {
		switch (index) {
		case 0:
			return x;
		case 1:
			return y;
		}
		throw new IllegalArgumentException();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Vector2f add(Vector2f other) {
		return new Vector2f(x + other.getX(), y + other.getY());
	}

	public Vector2f sub(Vector2f other) {
		return new Vector2f(x - other.getX(), y - other.getY());
	}

	public Vector2f multiplyScalar(double scalar) {
		return new Vector2f(x * scalar, y * scalar);
	}

	public Vector2f divScalar(double scalar) {
		if (Math.abs(scalar) < eps) {
			throw new ArithmeticException("Делить на 0 нельзя");
		}
		return new Vector2f(x / scalar, y / scalar);
	}

	public double getLength() {
		return (double) Math.sqrt(x * x + y * y);
	}

	public Vector2f normalize() {
		double length = getLength();
		if (Math.abs(length) > eps) {
			return new Vector2f(x / length, y / length);
		} else {
			throw new IllegalArgumentException("Делить на 0 нельзя");
		}
	}

	public double dotProduct(Vector2f v) {
		return this.x * v.getX() + this.y * v.getY();
	}

	@Override
	public String toString() {
		return "Vector2f{" + "x=" + x + ", y=" + y + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Vector2f other))
			return false;
		return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps;
	}
}