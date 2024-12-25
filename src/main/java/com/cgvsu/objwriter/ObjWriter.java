package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ObjWriter {
    public static String write(Model model) {
        return String.valueOf(writeVertices(model.getVertices())) +
                writeTextureVertices(model.getTextureVertices()) +
                writeNormals(model.getNormals()) +
                writePolygons(model.getPolygons());
    }

    protected static StringBuilder writeVertices(ArrayList<Vector3f> vertices) {
        StringBuilder stringVertices = new StringBuilder();
        for (Vector3f vertex : vertices) {
            stringVertices.append(String.format(Locale.US, "v %.4f %.4f %.4f\n", vertex.getX(), vertex.getY(), vertex.getZ()));
        }
        return stringVertices;
    }

    protected static StringBuilder writeTextureVertices(ArrayList<Vector2f> textureVertices) {
        StringBuilder stringTextureVertices = new StringBuilder();
        for (Vector2f textureVertex : textureVertices) {
            stringTextureVertices.append(String.format(Locale.US, "vt %.4f %.4f\n", textureVertex.getX(), textureVertex.getY()));
        }
        return stringTextureVertices;
    }

    protected static StringBuilder writeNormals(ArrayList<Vector3f> normals) {
        StringBuilder stringNormals = new StringBuilder();
        for (Vector3f normal : normals) {
            stringNormals.append(String.format(Locale.US, "vn %.4f %.4f %.4f\n", normal.getX(), normal.getY(), normal.getZ()));
        }
        return stringNormals;
    }

    protected static StringBuilder writePolygons(ArrayList<Polygon> polygons) {
        StringBuilder stringPolygons = new StringBuilder();
        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            List<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            List<Integer> normalIndices = polygon.getNormalIndices();

            stringPolygons.append("f");

            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i) + 1; // Переключение с 0-индекса на 1
                String faceElement = String.valueOf(vertexIndex);

                if (!textureVertexIndices.isEmpty()) {
                    int textureIndex = textureVertexIndices.get(i) + 1;
                    faceElement += "/" + textureIndex;
                }
                if (!normalIndices.isEmpty()) {
                    int normalIndex = normalIndices.get(i) + 1;
                    if (textureVertexIndices.isEmpty()) {
                        faceElement += "/";
                    }
                    faceElement += "/" + normalIndex;
                }

                stringPolygons.append(" ").append(faceElement);
            }

            stringPolygons.append("\n");
        }
        return stringPolygons;
    }
}
