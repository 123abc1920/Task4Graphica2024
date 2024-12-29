package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import com.cgvsu.camera.Camera;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.model.Model;
import com.cgvsu.rasterization.Line;
import com.cgvsu.rasterization.RasterizationParams;
import com.cgvsu.rasterization.TextureRasterization;
import com.cgvsu.rasterization.TriangleRasterization;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

	public static void render(final GraphicsContext graphicsContext, final Camera camera, final Model mesh,
			final int width, final int height) {
		Matrix4f modelMatrix = translateRotateScale();
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f projectionMatrix = camera.getProjectionMatrix();

		Matrix4f projectionViewMatrix = projectionMatrix.multiplyMatrix(viewMatrix);
		Matrix4f projectionViewModelMatrix = projectionViewMatrix.multiplyMatrix(modelMatrix);

		final int nPolygons = mesh.getPolygons().size();
		for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
			final int nVerticesInPolygon = mesh.getPolygons().get(polygonInd).getVertexIndices().size();
			ArrayList<Vector3f> resultPoints = new ArrayList<>();
			for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
				Vector3f vertex = mesh.getVertices()
						.get(mesh.getPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

				Vector3f vertexVecmath = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());
				Vector3f resultPoint = vertexToPoint(projectionViewModelMatrix.multiplyVectorDivW(vertexVecmath), width,
						height);
				resultPoints.add(resultPoint);
			}

			Vector3f[] normals = new Vector3f[3];
			Vector3f[] vertices = new Vector3f[3];

			for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
				normals[vertexInPolygonInd] = mesh.getNormals()
						.get(mesh.getPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
				vertices[vertexInPolygonInd] = resultPoints.get(vertexInPolygonInd);
			}

			boolean drawnPolygon = false;

			Vector3f[] vectorsToCamera = new Vector3f[3];
			Vector3f[] vs = new Vector3f[3];
			for (int i = 0; i < mesh.getPolygons().get(polygonInd).getVertexIndices().size(); i++) {
				Vector3f vertex = mesh.getVertices().get(mesh.getPolygons().get(polygonInd).getVertexIndices().get(i));
				vectorsToCamera[i] = camera.getPosition().sub(vertex);
				vs[i] = vertex;
			}

			if (!RasterizationParams.getDrawTexture(mesh)) {
				drawnPolygon = drawSolidColor(vertices, normals, vectorsToCamera, mesh.getColor(), vs);
			} else {
				Vector2f[] texturePoints = new Vector2f[3];
				texturePoints[0] = mesh.getTextureVertices()
						.get(mesh.getPolygons().get(polygonInd).getTextureVertexIndices().get(0));
				texturePoints[1] = mesh.getTextureVertices()
						.get(mesh.getPolygons().get(polygonInd).getTextureVertexIndices().get(1));
				texturePoints[2] = mesh.getTextureVertices()
						.get(mesh.getPolygons().get(polygonInd).getTextureVertexIndices().get(2));
				drawnPolygon = drawTexture(vertices, texturePoints, mesh.getImage(), normals, vectorsToCamera, vs);
			}

			if (RasterizationParams.getDrawLine()) {
				if (drawnPolygon) {
					drawLines(nVerticesInPolygon, resultPoints);
				}
			}

		}
	}

	private static boolean drawTexture(Vector3f[] vertices, Vector2f[] texturePoints, Image image, Vector3f[] normals,
			Vector3f[] vecsToCamera, Vector3f[] vs) {
		return TextureRasterization.fillTriangle(vertices, texturePoints, image, normals, vecsToCamera, vs);

	}

	private static boolean drawSolidColor(Vector3f[] vertices, Vector3f[] normals, Vector3f[] vs, Color color,
			Vector3f[] origVertices) {
		return TriangleRasterization.fillTriangle(vertices, color, normals, vs, origVertices);
	}

	private static void drawLines(int nVerticesInPolygon, List<Vector3f> resultPoints) {
		for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
			Line.drawLine(resultPoints.get(vertexInPolygonInd - 1).getX(),
					resultPoints.get(vertexInPolygonInd - 1).getY(), resultPoints.get(vertexInPolygonInd).getX(),
					resultPoints.get(vertexInPolygonInd).getY(), resultPoints.get(vertexInPolygonInd - 1).getZ(),
					resultPoints.get(vertexInPolygonInd).getZ());
		}
		Line.drawLine(resultPoints.get(nVerticesInPolygon - 1).getX(), resultPoints.get(nVerticesInPolygon - 1).getY(),
				resultPoints.get(0).getX(), resultPoints.get(0).getY(), resultPoints.get(nVerticesInPolygon - 1).getZ(),
				resultPoints.get(0).getZ());
	}
}