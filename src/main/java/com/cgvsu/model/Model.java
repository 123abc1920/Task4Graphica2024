package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Model {

	public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
	public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	public ArrayList<Polygon> polygons = new ArrayList<Polygon>();

	public Image image = null;
	private String imageSource = "";

	public String getTextureSource() {
		if (image == null) {
			return "";
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
}
