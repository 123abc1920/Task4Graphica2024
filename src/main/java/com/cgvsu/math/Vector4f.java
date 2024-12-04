package com.cgvsu.math;

public class Vector4f {
    private static final double eps = 1e-4f;
    public double x;
    public double y;
    public double z;
    public double w;

    public Vector4f(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            case 3:
                return w;
        }
        throw new IllegalArgumentException("index out of range");
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public Vector4f sum(Vector4f v) {
        double a = x + v.getX();
        double b = y + v.getY();
        double c = z + v.getZ();
        double d = w + v.getW();
        return new Vector4f(a, b, c, d);
    }

    public Vector4f sub(Vector4f v) {
        return new Vector4f(x - v.getX(), y - v.getY(), z - v.getZ(), w - v.getW());
    }

    public Vector4f multiplyScalar(double scalar) {
        return new Vector4f(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Vector4f diveScalar(double scalar) {
        if (Math.abs(scalar) < eps) {
            throw new ArithmeticException();
        } else {
            return new Vector4f(x / scalar, y / scalar, z / scalar, w / scalar);
        }
    }

    public double getLength() {
        return (double) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Vector4f normalize() {
        double len = getLength();
        if (Math.abs(len) > eps) {
            return new Vector4f(x / len, y / len, z / len, w / len);
        } else {
            throw new ArithmeticException();
        }
    }

    public double dotProduct(Vector4f v) {
        return this.x * v.getX() + this.y * getY() + this.z * v.getZ() + this.w * v.getW();
    }

    public Vector3f translationVector3f() {
        return new Vector3f(getX(), getY(), getZ());
    }

    @Override
    public String toString() {
        return "Vector4f{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector4f other)) return false;
        return Math.abs(x - other.x) < eps && Math.abs(y - other.y) < eps && Math.abs(z - other.z) < eps && Math.abs(w - other.w) < eps;
    }
}