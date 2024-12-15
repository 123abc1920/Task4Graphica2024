package com.cgvsu.rasterization;

import com.cgvsu.model.Model;
import com.cgvsu.zbuffer.DrawBuffer;
import com.cgvsu.zbuffer.ZBuffer;

import javafx.scene.paint.Color;

public class RasterizationParams {

	public static Color[] colors = { Color.CRIMSON, Color.CRIMSON, Color.CRIMSON };
	public static ZBuffer zBuffer = new ZBuffer(800, 450);
	public static DrawBuffer drawBuffer = new DrawBuffer(800, 450, null);

	private static boolean drawLine = false;
	private static boolean drawTexture = false;
	private static boolean isLight = true;

	private static Color lineColor = Color.DARKSLATEBLUE;

	public static Color getLineColor() {
		return lineColor;
	}

	public static void setLineColor(Color lineColor) {
		RasterizationParams.lineColor = lineColor;
	}

	public static void setColor(Color color) {
		colors[0] = colors[1] = colors[2] = color;
	}

	public static Color getColor() {
		return colors[0];
	}

	public static boolean getDrawTexture(Model m) {
		// if (m.image == null) {
		if (m.getImage() == null) {
			return false;
		}
		return drawTexture;
	}

	public static boolean getIsLight() {
		return isLight;
	}

	public static boolean getDrawLine() {
		return drawLine;
	}

	public static void setLine(boolean drawLine) {
		RasterizationParams.drawLine = drawLine;
	}

	public static void setTexture(boolean drawTexture) {
		RasterizationParams.drawTexture = drawTexture;
	}

	public static void setLight(boolean isLight) {
		RasterizationParams.isLight = isLight;
	}

}
