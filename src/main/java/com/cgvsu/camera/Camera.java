package com.cgvsu.camera;

import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.math.Matrix4f;

public class Camera {

	private Vector3f position;
	private Vector3f target;
	private double fov;
	private double aspectRatio;
	private double nearPlane;
	private double farPlane;

	public Camera(final Vector3f position, final Vector3f target, final double fov, final double aspectRatio,
			final double nearPlane, final double farPlane) {
		this.position = position;
		this.target = target;
		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.nearPlane = nearPlane;
		this.farPlane = farPlane;
	}

	public void setPosition(final Vector3f position) {
		this.position = position;
	}

	public void setTarget(final Vector3f target) {
		this.target = target;
	}

	public void setAspectRatio(final double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getTarget() {
		return target;
	}

	public void movePosition(final Vector3f translation) {
		this.position = this.position.add(translation);
	}

	public void moveTarget(final Vector3f translation) {
		this.target = this.target.add(translation);
	}

	public Matrix4f getViewMatrix() {
		return GraphicConveyor.lookAt(position, target);
	}

	public Matrix4f getProjectionMatrix() {
		return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
	}

	public void setFarPlane(double farPlane) {
		if (farPlane > this.nearPlane) {
			this.farPlane = Math.round(farPlane * 100.0) / 100.0;
		}
	}

	public void setNearPlane(double nearPlane) {
		if (nearPlane < this.farPlane) {
			this.nearPlane = Math.round(nearPlane * 1000.0) / 1000.0;
			System.out.println("kkk");
		}
	}

	public void setFov(double fov) {
		fov = Math.round(fov * 100.0) / 100.0;
		this.fov = Math.max(0, Math.min(360, fov));
	}

	public double getFarPlane() {
		return farPlane;
	}

	public double getNearPlane() {
		return nearPlane;
	}

	public double getFov() {
		return fov;
	}

	@Override
	public String toString() {
		return String.format("Position: %s, Target: %s", this.position, this.target, this.farPlane, this.nearPlane,
				this.aspectRatio, this.fov);
	}
}