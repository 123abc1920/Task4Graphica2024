package com.cgvsu.model;

import com.cgvsu.math.AffineTransformation;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Model {

	private ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	private ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
	private ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

	// private static final double TRANSLATION = 0.4;
	// private static final double SCALE = 0.5;
	// private static final int ROTATE = 3;
	// private static final double DECLINE = 0.9;

	private Color color = Color.CRIMSON;

	private Image image = null;
	private String imageSource = "";

	public ArrayList<Vector3f> getVertices() {
		return vertices;
	}

	public void setVertices(ArrayList<Vector3f> vertices) {
		this.vertices = vertices;
	}

	public ArrayList<Vector2f> getTextureVertices() {
		return textureVertices;
	}

	public void setTextureVertices(ArrayList<Vector2f> textureVertices) {
		this.textureVertices = textureVertices;
	}

	public ArrayList<Vector3f> getNormals() {
		return normals;
	}

	public void setNormals(ArrayList<Vector3f> normals) {
		this.normals = normals;
	}

	public ArrayList<Polygon> getPolygons() {
		return polygons;
	}

	public void setPolygons(ArrayList<Polygon> polygons) {
		this.polygons = polygons;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getImageSource() {
		return imageSource;
	}

	public void setImageSource(String imageSource) {
		this.imageSource = imageSource;
	}

	public Model() {

	}

	public String getTextureSource() {
		if (textureVertices.size() == 0) {
			return "Нет текстурных вершин";
		}
		if (image == null) {
			return "Нет текстуры";
		}
		return this.imageSource;
	}

	public void deleteTexture() {
		this.image = null;
		this.imageSource = "";
	}

	public void setTexture(String s) {
		if (s == null || s.equals("")) {
			return;
		}
		imageSource = s;
		InputStream stream = null;
		try {
			stream = new FileInputStream(s);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Image image = new Image(stream);
		this.image = image;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public static void moveModelLeft(double trans, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.translate(trans, 0, 0, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void moveModelRight(double trans, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.translate(-trans, 0, 0, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void moveModelUp(double trans, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.translate(0, trans, 0, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void moveModelDown(double trans, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.translate(0, -trans, 0, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void moveModelForward(double trans, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.translate(0, 0, -trans, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void moveModelBackward(double trans, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.translate(0, 0, trans, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void rotateModelOnYUp(int rotate, Model model) {
		model.vertices.replaceAll(w -> AffineTransformation.rotate(0, rotate, 0, w));
	}

	public static void rotateModelOnYDown(int rotate, Model model) {
		model.vertices.replaceAll(w -> AffineTransformation.rotate(0, -rotate, 0, w));
	}

	public static void rotateModelOnXNotClockwise(int rotate, Model model) {
		model.vertices.replaceAll(w -> AffineTransformation.rotate(rotate, 0, 0, w));
	}

	public static void rotateModelOnXClockwise(int rotate, Model model) {
		model.vertices.replaceAll(w -> AffineTransformation.rotate(-rotate, 0, 0, w));
	}

	public static void rotateModelOnZClockwise(int rotate, Model model) {
		model.vertices.replaceAll(w -> AffineTransformation.rotate(0, 0, rotate, w));
	}

	public static void rotateModelOnZNotClockwise(int rotate, Model model) {
		model.vertices.replaceAll(w -> AffineTransformation.rotate(0, 0, -rotate, w));
	}

	public static void scaleModelOnXForward(double scale, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.scale(scale, 1, 1, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void scaleModelOnXBackward(double decline, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.scale(decline, 1, 1, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void scaleModelOnYUp(double scale, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.scale(1, scale, 1, new Vector3f(model.getVertices().get(i).getY(),
					model.getVertices().get(i).getZ(), model.getVertices().get(i).getX()));
			model.vertices.set(i, v);
		}
	}

	public static void scaleModelOnYDown(double decline, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.scale(1, decline, 1, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void scaleModelOnZRight(double scale, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.scale(1, 1, scale, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

	public static void scaleModelOnZLeft(double decline, Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.scale(1, 1, decline, new Vector3f(model.getVertices().get(i).getX(),
					model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}

}
