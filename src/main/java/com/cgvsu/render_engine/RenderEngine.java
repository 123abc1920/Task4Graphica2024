package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.model.Model;
import com.cgvsu.rasterization.DrawingSettings;
import com.cgvsu.rasterization.Rasterization;
import com.cgvsu.rasterization.RasterizationParams;
import com.cgvsu.rasterization.ZBuffer;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

	public static void render(final GraphicsContext graphicsContext, final Camera camera, final Model mesh,
			final int width, final int height) {
		Matrix4f modelMatrix = translateRotateScale();
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f projectionMatrix = camera.getProjectionMatrix();

		Matrix4f projectionViewMatrix = projectionMatrix.multiplyMatrix(viewMatrix);
		Matrix4f projectionViewModelMatrix = projectionViewMatrix.multiplyMatrix(modelMatrix);

		RasterizationParams.zBuffer = new ZBuffer(width, height);

		final int nPolygons = mesh.polygons.size();
		for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
			final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();
			ArrayList<Vector3f> resultPoints = new ArrayList<>();
			for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
				Vector3f vertex = mesh.vertices
						.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

				Vector3f vertexVecmath = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());
				Vector3f resultPoint = vertexToPoint(projectionViewModelMatrix.multiplyVectorDivW(vertexVecmath), width,
						height);
				resultPoints.add(resultPoint);
			}

			double[] arrX = new double[3];
			double[] arrY = new double[3];
			double[] arrZ = new double[3];
			for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
				arrX[vertexInPolygonInd] = resultPoints.get(vertexInPolygonInd).getX();
				arrY[vertexInPolygonInd] = resultPoints.get(vertexInPolygonInd).getY();
				arrZ[vertexInPolygonInd] = resultPoints.get(vertexInPolygonInd).getZ();
			}

			if (RasterizationParams.drawingSettings == DrawingSettings.SOLID_COLOR) {
				drawSolidColor(graphicsContext, arrX, arrY, arrZ);
			}

			if (RasterizationParams.drawingSettings == DrawingSettings.LINES) {
				drawLines(graphicsContext, nVerticesInPolygon, resultPoints);
			}

			if (RasterizationParams.drawingSettings == DrawingSettings.LINES_SOLID_COLOR) {
				drawSolidColor(graphicsContext, arrX, arrY, arrZ);
				drawLines(graphicsContext, nVerticesInPolygon, resultPoints);
			}

			if (RasterizationParams.drawingSettings == DrawingSettings.lINES_TEXTURE) {

			}

			if (RasterizationParams.drawingSettings == DrawingSettings.TEXTURE) {

			}

		}
	}

	private static void drawSolidColor(GraphicsContext graphicsContext, double[] arrX, double[] arrY, double[] arrZ) {
		Rasterization.fillTriangle(graphicsContext, arrX, arrY, arrZ, RasterizationParams.colors,
				RasterizationParams.zBuffer);
	}

	private static void drawLines(GraphicsContext graphicsContext, int nVerticesInPolygon,
			List<Vector3f> resultPoints) {
		for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
			graphicsContext.strokeLine(resultPoints.get(vertexInPolygonInd - 1).getX(),
					resultPoints.get(vertexInPolygonInd - 1).getY(), resultPoints.get(vertexInPolygonInd).getX(),
					resultPoints.get(vertexInPolygonInd).getY());
		}

		graphicsContext.strokeLine(resultPoints.get(nVerticesInPolygon - 1).getX(),
				resultPoints.get(nVerticesInPolygon - 1).getY(), resultPoints.get(0).getX(),
				resultPoints.get(0).getY());
	}
}