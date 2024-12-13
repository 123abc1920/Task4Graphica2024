package com.cgvsu.model;

import com.cgvsu.math.AffineTransformation;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Model {

	private ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	private ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
	private ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

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
		// C:\\Users\\annam\\OneDrive\\Рабочий стол\\textures\\fox\\animalia_fox_1.png
		// C:\\Users\\annam\\Downloads\\caracal_texture.png
		// C:\\Users\\annam\\Downloads\\text.png
		// C:\\Users\\annam\\Downloads\\caracal_texture.png
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

	public static void moveModelLeft(Model model) {
		for (int i = 0; i < model.vertices.size(); i++) {
			Vector3f v = AffineTransformation.translate(0, 0,0,new Vector3f(model.getVertices().get(i).getX(),model.getVertices().get(i).getY(), model.getVertices().get(i).getZ()));
			model.vertices.set(i, v);
		}
	}
}
