package com.cgvsu.light;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.normals.FindNormals;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.triangulation.Triangulation;

import javafx.scene.paint.Color;

public class Light {
	public Vector3f position = new Vector3f(3, 0, 0);

	public Model lightModel = new Model();

	private boolean isOn = true;
	private Color color = Color.WHITE;

	private double k = 0.3;
	private double ks = 0.4;
	private double highLightShine = 31;

	private String modelText = "# Blender 4.0.1\r\n" + "# www.blender.org\r\n" + "o Cube\r\n"
			+ "v -0.100000 -0.100000 0.100000\r\n" + "v -0.100000 0.100000 0.100000\r\n"
			+ "v -0.100000 -0.100000 -0.100000\r\n" + "v -0.100000 0.100000 -0.100000\r\n"
			+ "v 0.100000 -0.100000 0.100000\r\n" + "v 0.100000 0.100000 0.100000\r\n"
			+ "v 0.100000 -0.100000 -0.100000\r\n" + "v 0.100000 0.100000 -0.100000\r\n" + "s 0\r\n" + "f 1 2 4 3\r\n"
			+ "f 3 4 8 7\r\n" + "f 7 8 6 5\r\n" + "f 5 6 2 1\r\n" + "f 3 7 5 1\r\n" + "f 8 4 2 6";

	public Light() {
		lightModel = ObjReader.read(modelText);
		lightModel.setColor(this.color);
		Triangulation.triangulateModel(lightModel);
		lightModel.setNormals(FindNormals.findNormals(lightModel));
		moveLight(this.position, new Vector3f());
	}

	private void moveLight(Vector3f v, Vector3f v2) {
		double t = v.getX() - v2.getX();
		if (t > 0) {
			Model.moveModelRight(t, this.lightModel);
		} else {
			Model.moveModelLeft(Math.abs(t), this.lightModel);
		}

		t = v.getY() - v2.getY();
		if (t > 0) {
			Model.moveModelDown(t, this.lightModel);
		} else {
			Model.moveModelUp(Math.abs(t), this.lightModel);
		}

		t = v.getZ() - v2.getZ();
		if (t > 0) {
			Model.moveModelForward(t, this.lightModel);
		} else {
			Model.moveModelBackward(Math.abs(t), this.lightModel);
		}
	}

	public void setColor(Color color) {
		lightModel.setColor(color);
		this.color = color;
	}

	public void setPosition(Vector3f v) {
		moveLight(v, this.position);
		this.position = v;
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

	public Color getColor() {
		return color;
	}

	public Color newRgb(Color start, Vector3f n, Vector3f vectorToCamera, Vector3f ray) {
		if (!this.isOn) {
			return start;
		}

		double l = ray.dotProduct(n.multiplyScalar(-1));

		if (l < 0) {
			l = 0;
		}

		vectorToCamera = vectorToCamera.normalize();
		Vector3f reflectedRay = ray.sub(n.multiplyScalar(2 * ray.dotProduct(n)));
		try {
			reflectedRay = reflectedRay.normalize();
		} catch (Exception e) {
			reflectedRay = new Vector3f();
		}

		double r = reflectedRay.dotProduct(vectorToCamera.multiplyScalar(-1));
		if (r < 0) {
			r = 0;
		}

		double sred = start.getRed() * color.getRed();
		double sgreen = start.getGreen() * color.getGreen();
		double sblue = start.getBlue() * color.getBlue();

		double red = Math.max(0, Math.min(1, (ambient(start.getRed()) + diffuse(sred, l) + specular(sred, r))));
		double green = Math.max(0, Math.min(1, (ambient(start.getGreen()) + diffuse(sgreen, l) + specular(sgreen, r))));
		double blue = Math.max(0, Math.min(1, (ambient(start.getBlue()) + diffuse(sblue, l) + specular(sblue, r))));

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
