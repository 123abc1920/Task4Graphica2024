package com.cgvsu.camera;

import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.math.Matrix4f;

public class Camera {

	private Vector3f position;
	private Vector3f target;
	private float fov;
	private float aspectRatio;
	private float nearPlane;
	private float farPlane;

	public Camera(final Vector3f position, final Vector3f target, final float fov, final float aspectRatio,
			final float nearPlane, final float farPlane) {
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

	public void setAspectRatio(final float aspectRatio) {
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

	@Override
	public String toString() {
		return String.format("Position: %s, Target: %s", this.position, this.target, this.farPlane, this.nearPlane,
				this.aspectRatio, this.fov);
	}
}