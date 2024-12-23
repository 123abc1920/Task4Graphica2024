package com.cgvsu.objreader;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjReaderTest {

    @Test
    public void testParseVertex01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3f result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.03f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex02() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3f result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.10f);
        Assertions.assertFalse(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex03() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("ab", "o", "ba"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Failed to parse float value.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex04() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Too few vertex arguments.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex05() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0", "4.0"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
            Assertions.fail();

        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. More than three coordinates are specified for the vertex.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseTextureVertex01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0"));
        try {
            ObjReader.parseTextureVertex(wordsInLineWithoutToken, 10);
            Assertions.fail();

        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. More than two coordinates are specified for the texture vertex.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseNormal01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0", "4.0"));
        try {
            ObjReader.parseNormal(wordsInLineWithoutToken, 10);
            Assertions.fail();

        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. More than three coordinates are specified for the normal.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon01() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(2,0,0)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, null, null);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "For polygon #1: it consists only of 2 or fewer vertices.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon02() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, modelTextureVertices, modelNormals);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "Not all vertices of polygon number 1 have a texture vertex attached to them.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon03() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, modelTextureVertices, modelNormals);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "Not all vertices of polygon number 1 have a normals attached to them.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon04() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(1,2,3)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, null, null);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "For polygon #1: there is no vertex with number 4 in the file.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon05() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(2,0,0)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, null, null);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "For polygon #1: some of its points lie on the same line or are identical.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon06() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, modelTextureVertices, modelNormals);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "For polygon #1: some of its points lie on the same line or are identical.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon07() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2,1)));
//        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, null, null);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "For polygon #1: some of its points lie on the same line or are identical.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

//    @Test
//    public void testCheckPolygon08() {
//        Polygon polygon = new Polygon();
//        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2,3,4,5)));
////        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
////        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(0,0,1), new Vector3f(0,1,1), new Vector3f(1,1,1), new Vector3f(1,0,1), new Vector3f(1,0,0)));
//        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
//        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
//        try {
//            ObjReader.checkPolygon(polygon, 0, modelVertices, null, null);
//            Assertions.fail();
//
//        } catch (RuntimeException exception) {
//            String expectedError = "For polygon #1: its points do not lie in the same plane.";
//            Assertions.assertEquals(expectedError, exception.getMessage());
//        }
//    }

//    @Test
//    public void testCheckPolygon09() {
//        Polygon polygon = new Polygon();
//        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2,3)));
////        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
////        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(0,0,1), new Vector3f(1,0,0), new Vector3f(1,1,1)));
//        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
//        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
//        try {
//            ObjReader.checkPolygon(polygon, 0, modelVertices, null, null);
//            Assertions.fail();
//
//        } catch (RuntimeException exception) {
//            String expectedError = "For polygon #1: its points do not lie in the same plane.";
//            Assertions.assertEquals(expectedError, exception.getMessage());
//        }
//    }

    @Test
    public void testCheckPolygon10() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,3)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, modelTextureVertices, modelNormals);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "For polygon #1: there is no texture vertex with number 4 in the file.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckPolygon11() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,5)));
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        try {
            ObjReader.checkPolygon(polygon, 0, modelVertices, modelTextureVertices, modelNormals);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "For polygon #1: there is no normal with number 6 in the file.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckModelAfterReading01() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        ArrayList<Polygon> modelPolygons = new ArrayList<>();
        modelPolygons.add(polygon);
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0)/*, new Vector3f(1,0,0), new Vector3f(0,1,0)*/));
//        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
//        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        Model model = new Model();
        model.setVertices(modelVertices);
//        model.setTextureVertices(modelTextureVertices);
//        model.setNormals(modelNormals);
        model.setPolygons(modelPolygons);
        try {
            ObjReader.checkModelAfterReading(model);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "Model has 2 or fewer vertices.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckModelAfterReading02() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        ArrayList<Polygon> modelPolygons = new ArrayList<>();
//        modelPolygons.add(polygon);
        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        Model model = new Model();
        model.setVertices(modelVertices);
        model.setTextureVertices(modelTextureVertices);
        model.setNormals(modelNormals);
        model.setPolygons(modelPolygons);
        try {
            ObjReader.checkModelAfterReading(model);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "There is not a single polygon in the model.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckModelAfterReading03() {
        ArrayList<Polygon> modelPolygons = new ArrayList<>();

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon);

        Polygon polygon2 = new Polygon();
        polygon2.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon2.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon2.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon2);

        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        Model model = new Model();
        model.setVertices(modelVertices);
        model.setTextureVertices(modelTextureVertices);
        model.setNormals(modelNormals);
        model.setPolygons(modelPolygons);
        try {
            ObjReader.checkModelAfterReading(model);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "Not all polygons in the model have a texture vertex or normal attached to them.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckModelAfterReading04() {
        ArrayList<Polygon> modelPolygons = new ArrayList<>();

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon);

        Polygon polygon2 = new Polygon();
        polygon2.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon2.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon2.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon2);

        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        Model model = new Model();
        model.setVertices(modelVertices);
        model.setTextureVertices(modelTextureVertices);
        model.setNormals(modelNormals);
        model.setPolygons(modelPolygons);
        try {
            ObjReader.checkModelAfterReading(model);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "Not all polygons in the model have a texture vertex or normal attached to them.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckModelAfterReading05() {
        ArrayList<Polygon> modelPolygons = new ArrayList<>();

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon);

        Polygon polygon2 = new Polygon();
        polygon2.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon2.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon2.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon2);

        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        Model model = new Model();
        model.setVertices(modelVertices);
        model.setTextureVertices(modelTextureVertices);
        model.setNormals(modelNormals);
        model.setPolygons(modelPolygons);
        try {
            ObjReader.checkModelAfterReading(model);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "Not all polygons in the model have a texture vertex or normal attached to them.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testCheckModelAfterReading06() {
        ArrayList<Polygon> modelPolygons = new ArrayList<>();

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon);

        Polygon polygon2 = new Polygon();
        polygon2.setVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
        polygon2.setTextureVertexIndices(new ArrayList<Integer>(List.of(0,1,2)));
//        polygon2.setNormalIndices(new ArrayList<Integer>(List.of(0,1,2)));
        modelPolygons.add(polygon2);

        ArrayList<Vector3f> modelVertices = new ArrayList<Vector3f>(List.of(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0)));
        ArrayList<Vector2f> modelTextureVertices = new ArrayList<Vector2f>(List.of(new Vector2f(0,0), new Vector2f(1,0), new Vector2f(0,1)));
        ArrayList<Vector3f> modelNormals = new ArrayList<Vector3f>(List.of(new Vector3f(0.1f,0.1f,0.1f), new Vector3f(0.2f,0.2f,0.2f), new Vector3f(0.3f,0.3f,0.3f)));
        Model model = new Model();
        model.setVertices(modelVertices);
        model.setTextureVertices(modelTextureVertices);
        model.setNormals(modelNormals);
        model.setPolygons(modelPolygons);
        try {
            ObjReader.checkModelAfterReading(model);
            Assertions.fail();

        } catch (RuntimeException exception) {
            String expectedError = "Not all polygons in the model have a texture vertex or normal attached to them.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }
}