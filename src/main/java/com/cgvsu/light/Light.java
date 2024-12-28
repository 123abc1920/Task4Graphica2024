package com.cgvsu.light;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.normals.FindNormals;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.triangulation.Triangulation;

import javafx.scene.paint.Color;

public class Light {
	public Vector3f position = new Vector3f(0, 3, 0);

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
			+ "v 0.100000 -0.100000 -0.100000\r\n" + "v 0.100000 0.100000 -0.100000\r\n"
			+ "vn -0.1000 -0.0000 -0.0000\r\n" + "vn -0.0000 -0.0000 -0.1000\r\n" + "vn 0.1000 -0.0000 -0.0000\r\n"
			+ "vn -0.0000 -0.0000 0.1000\r\n" + "vn -0.0000 -0.1000 -0.0000\r\n" + "vn -0.0000 0.1000 -0.0000\r\n"
			+ "vt 0.657719 0.070615\r\n" + "vt 0.657719 0.288779\r\n" + "vt 0.343923 0.288779\r\n"
			+ "vt 0.343923 0.070615\r\n" + "vt 0.982747 0.538081\r\n" + "vt 0.982747 0.758131\r\n"
			+ "vt 0.677420 0.758131\r\n" + "vt 0.677420 0.538418\r\n" + "vt 0.654372 0.535544\r\n"
			+ "vt 0.654372 0.758215\r\n" + "vt 0.344166 0.758215\r\n" + "vt 0.344166 0.535750\r\n"
			+ "vt 0.327407 0.537083\r\n" + "vt 0.327407 0.759706\r\n" + "vt 0.015414 0.759706\r\n"
			+ "vt 0.015414 0.536937\r\n" + "vt 0.656108 0.299368\r\n" + "vt 0.656108 0.524271\r\n"
			+ "vt 0.344171 0.524271\r\n" + "vt 0.344171 0.299368\r\n" + "vt 0.655295 0.773650\r\n"
			+ "vt 0.655295 0.993820\r\n" + "vt 0.346502 0.993820\r\n" + "vt 0.346502 0.773650\r\n" + "s 0\r\n"
			+ "f 1/1/1 2/2/1 4/3/1 3/4/1\r\n" + "f 3/5/2 4/6/2 8/7/2 7/8/2\r\n" + "f 7/9/3 8/10/3 6/11/3 5/12/3\r\n"
			+ "f 5/13/4 6/14/4 2/15/4 1/16/4\r\n" + "f 3/17/5 7/18/5 5/19/5 1/20/5\r\n"
			+ "f 8/21/6 4/22/6 2/23/6 6/24/6";

	public Light() {
		lightModel = ObjReader.read(modelText);
		lightModel.setColor(this.color);
		Triangulation.triangulateModel(lightModel);
		lightModel.setNormals(FindNormals.findNormals(lightModel));
		moveLight();
	}

	private void moveLight() {
		double t = this.position.getX();
		if (t > 0) {
			Model.moveModelRight(t, this.lightModel);
		} else {
			Model.moveModelLeft(Math.abs(t), this.lightModel);
		}

		t = this.position.getY();
		if (t > 0) {
			Model.moveModelDown(t, this.lightModel);
		} else {
			Model.moveModelUp(Math.abs(t), this.lightModel);
		}

		t = this.position.getZ();
		if (t > 0) {
			Model.moveModelBackward(t, this.lightModel);
		} else {
			Model.moveModelForward(Math.abs(t), this.lightModel);
		}
	}

	public void setColor(Color color) {
		lightModel.setColor(color);
		this.color = color;
	}

	public void setPosition(Vector3f v) {
		this.position = v;
		moveLight();
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
