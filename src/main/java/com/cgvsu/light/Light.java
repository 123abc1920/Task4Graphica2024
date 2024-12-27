package com.cgvsu.light;

import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class Light {
	public Vector3f position = new Vector3f(0, 100, 0);

	private boolean isOn = true;
	private Color color = Color.WHITE;

	private double k = 0.3;
	private double ks = 0.4;
	private double highLightShine = 31;

	public Light() {

	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setPosition(Vector3f v) {
		this.position = v;
	}

	public void setTarget(Vector3f v) {
	}

	public void setK(double k) {
		k = Math.max(0, Math.min(1, k));
		this.k = Math.round(k * 100.0) / 100.0;
	}

	public void setKs(double ks) {
		ks = Math.max(0, Math.min(1, ks));
		this.ks = Math.round(ks * 100.0) / 100.0;
	}

	public void setHighLightShine(double highLightShine) {
		highLightShine = Math.max(1, Math.min(64, highLightShine));
		this.highLightShine = Math.round(highLightShine);
	}

	public double getK() {
		return k;
	}

	public double getKs() {
		return ks;
	}

	public double getHighLightShine() {
		return highLightShine;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	public boolean getOn() {
		return isOn;
	}

	public Color newRgb(Color start, Vector3f n, Vector3f vectorToCamera, Vector3f dot) {
		if (!this.isOn) {
			return start;
		}

		Vector3f ray = dot.sub(this.position).normalize();

		double l = ray.dotProduct(n.multiplyScalar(-1));

		if (l < 0) {
			l = 0;
		}

		vectorToCamera = vectorToCamera.normalize();
		Vector3f reflectedRay = ray.sub(n.multiplyScalar(2 * ray.dotProduct(n)));
		reflectedRay = reflectedRay.normalize();

		double r = reflectedRay.dotProduct(vectorToCamera.multiplyScalar(-1));

		double sred = start.getRed() * color.getRed();
		double sgreen = start.getGreen() * color.getGreen();
		double sblue = start.getBlue() * color.getBlue();

		double red = Math.max(0,
				Math.min(1, (ambient(start.getRed()) + diffuse(sred, l) + specular(start.getRed(), r))));
		double green = Math.max(0,
				Math.min(1, (ambient(start.getGreen()) + diffuse(sgreen, l) + specular(start.getGreen(), r))));
		double blue = Math.max(0,
				Math.min(1, (ambient(start.getBlue()) + diffuse(sblue, l) + specular(start.getBlue(), r))));

		return new Color(red, green, blue, 1);
	}

	private double ambient(double c) {
		return c * (1 - k);
	}

	private double diffuse(double c, double l) {
		return c * k * l;
	}

	private double specular(double c, double r) {
		if (r < 0) {
			return 0;
		}
		return ks * Math.pow(r, highLightShine);
	}

}
