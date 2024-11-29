package com.cgvsu.rasterization;

import javafx.scene.paint.Color;

public class RasterizationParams {

	public static Color[] colors = { Color.AQUA, Color.BLACK, Color.BLANCHEDALMOND };
	public static DrawingSettings drawingSettings = DrawingSettings.SOLID_COLOR;
	public static ZBuffer zBuffer;

	public static void setColor(Color color) {
		colors[0] = color;
	}

	public static void setDrawingSettings(DrawingSettings ds) {
		drawingSettings = ds;
	}

}
