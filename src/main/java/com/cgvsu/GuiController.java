package com.cgvsu;

import com.cgvsu.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import com.cgvsu.math.Vector3f;


import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 0.5F; //10.0F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private VBox VBoxMain;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 00, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    private double startXLMB;

    private double startYLMB;

    private double endXLMB;

    private double endYLMB;

    private double startXRMB;

    private double startYRMB;

    private double endXRMB;

    private double endYRMB;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height);
            }
        });

        canvas.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
//                startXLMB = mouseEvent.getSceneX();
                startYLMB = mouseEvent.getSceneY();
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                startXRMB = mouseEvent.getSceneX();
            }
        });

        canvas.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
//                endXLMB = mouseEvent.getSceneX();
                endYLMB = mouseEvent.getSceneY();
//                double shiftXLBM = endXLMB - startXLMB;
                double shiftYLMB = endYLMB - startYLMB;
                camera.movePosition(new Vector3f(/*(float) shiftX * 0.05f*/0,  (float) shiftYLMB * 0.05f, 0)); // вынести 0.05f как поле класса чтобы можно было изменить чувствительность
                camera.moveTarget(new Vector3f(/*(float) shiftX * 0.05f*/0,  (float) shiftYLMB * 0.05f, 0));
//                startXLMB = mouseEvent.getSceneX();
                startYLMB = mouseEvent.getSceneY();
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                //todo: добавить поворот вокруг оси, мб заменить на другую реализацию
//                Vector3f pos = camera.getPosition();
//                double coordX = camera.getPosition().x; double coordZ = camera.getPosition().z;
//                endXRMB = mouseEvent.getSceneX();
//                double shiftXRMB = endXRMB - startXRMB;
//                camera.rotateAroundY(new Vector3f(pos.x + ));
            }
        });

        canvas.setOnScroll(swipeEvent -> {
            if (swipeEvent.getDeltaY() > 0) camera.movePosition(new Vector3f(0, 0, -TRANSLATION * 10));
            else camera.movePosition(new Vector3f(0, 0, TRANSLATION * 10));
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
}