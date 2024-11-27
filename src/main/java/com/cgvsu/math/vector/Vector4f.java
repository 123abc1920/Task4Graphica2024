package com.cgvsu.math.vector;

import com.cgvsu.math.Global;

public class Vector4f {
    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float getW() {
        return w;
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
    public boolean equals(Vector4f other){
        return Math.abs(x - other.getX()) < Global.eps && Math.abs(y - other.getY()) < Global.eps && Math.abs(z - other.getZ()) < Global.eps && Math.abs(w - other.getW()) < Global.eps;
    }
    public static Vector4f addition(Vector4f v1, Vector4f v2) {
        return new Vector4f(v1.getX()+v2.getX(), v1.getY()+v2.getY(), v1.getZ()+v2.getZ(), v1.getW()+v2.getW());
    }
    public static Vector4f subtraction(Vector4f v1, Vector4f v2) {
        return new Vector4f(v1.getX()-v2.getX(), v1.getY()-v2.getY(), v1.getZ()-v2.getZ(), v1.getW()-v2.getW());
    }
    public static float lengthVector(Vector4f v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ() + v.getW() * v.getW());
    }
    public static Vector4f normalize(Vector4f v) {
        float l = lengthVector(v);
        if(l > Global.eps){
            return new Vector4f(v.getX()/l, v.getY()/l, v.getZ()/l, v.getW()/l);
        } else {
            throw new IllegalArgumentException("Division by zero");
        }
    }
    public static Vector4f multiplication(Vector4f v, float a) {
        return new Vector4f(v.getX()*a, v.getY()*a, v.getZ()*a, v.getW()*a);
    }
    public static Vector4f division(Vector4f v, float a) {
        if (a < Global.eps){
            throw new IllegalArgumentException("Division by zero");
        } else {
            return new Vector4f(v.getX() / a, v.getY() / a, v.getZ() / a, v.getW() / a);
        }
    }
    public static float scalar (Vector4f v1, Vector4f v2) {
        return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ() + v1.getW() * v2.getW();
    }

}