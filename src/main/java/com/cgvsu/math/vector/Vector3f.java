package com.cgvsu.math.vector;

import com.cgvsu.math.Global;

public class Vector3f {
    private final float x;
    private final float y;
    private final float z;


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
    public boolean equals(Vector3f other) {
        return Math.abs(x - other.getX()) < Global.eps && Math.abs(y - other.getY()) < Global.eps && Math.abs(z - other.getZ()) < Global.eps;
    }
    public static float lengthVector(Vector3f vector) {
        return (float) Math.sqrt(vector.getX()*vector.getX()+vector.getY()*vector.getY()+vector.getZ()*vector.getZ());
    }
    public static Vector3f addition(Vector3f v1, Vector3f v2) {
        return new Vector3f(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ());
    }

    public static Vector3f subtraction(Vector3f v1, Vector3f v2) {
        return new Vector3f(v1.getX() - v2.getX(), v1.getY() - v2.getY(), v1.getZ() - v2.getZ());
    }

    public static Vector3f multiplication(Vector3f v1, float a) {
        return new Vector3f(v1.getX() * a, v1.getY() * a, v1.getZ() * a);
    }

    public static Vector3f division(Vector3f v1, float a) {
        if (a < Global.eps) {
            throw new IllegalArgumentException("Division by zero");
        } else {
            return new Vector3f(v1.getX() / a, v1.getY() / a, v1.getZ() / a);
        }
    }
    public static float length(Vector3f v){
        return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
    }
    public static Vector3f normalize(Vector3f v){
        float l = length(v);
        if(l>Global.eps){
            return new Vector3f(v.getX()/l, v.getY()/l, v.getZ()/l);
        } else {
            throw new IllegalArgumentException("Division by zero");
        }
    }
    public Vector3f cross(Vector3f other){
        return new Vector3f(this.y * other.z - this.z * other.y,this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x );
    }
    public static float scalar(Vector3f vector1, Vector3f vector2) {
        return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY() + vector1.getZ() * vector2.getZ();
    }

}