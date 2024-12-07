package com.cgvsu;

import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.triangulation.Triangulation;

import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;

import com.cgvsu.camera.Camera;
import com.cgvsu.camera.CameraManager;
import com.cgvsu.math.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.normals.FindNormals;

public class GuiController {

	final private float TRANSLATION = 0.5F; // 10.0F;

	@FXML
	AnchorPane anchorPane;

	@FXML
	private Canvas canvas;

	@FXML
	private VBox vBoxMain;

	@FXML
	private VBox VBoxCamera;

	@FXML
	private TextField textFieldPosX;

	@FXML
	private TextField textFieldPosY;

	@FXML
	private TextField textFieldPosZ;

	@FXML
	private TextField textFieldTarX;

	@FXML
	private TextField textFieldTarY;

	@FXML
	private TextField textFieldTarZ;

	private boolean isInLightMode = true;

	private Model mesh = null;

	private Camera camera = CameraManager.getActive();

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
		anchorPane.prefWidthProperty()
				.addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue() - vBoxMain.getWidth()));
		anchorPane.prefHeightProperty()
				.addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

		timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);

		KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
			double width = canvas.getWidth();
			double height = canvas.getHeight();

			camera = CameraManager.getActive();
			System.out.println(camera.toString());

			canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
			camera.setAspectRatio((float) (width / height));

			if (mesh != null) {
				RenderEngine.render(canvas.getGraphicsContext2D(), CameraManager.getActive(), mesh, (int) width,
						(int) height);
//				VBoxMain.setVisible(true);
			}
		});

		anchorPane.getStylesheets().add(getClass().getResource("styles/light_mode.css").toExternalForm());
		showStartInfo();

		timeline.getKeyFrames().add(frame);
		timeline.play();
	}

	private void showStartInfo() {
		textFieldPosX.setText(Double.toString(camera.getPosition().getX()));
		textFieldPosY.setText(Double.toString(camera.getPosition().getY()));
		textFieldPosZ.setText(Double.toString(camera.getPosition().getZ()));
		textFieldTarX.setText(Double.toString(camera.getTarget().getX()));
		textFieldTarY.setText(Double.toString(camera.getTarget().getY()));
		textFieldTarZ.setText(Double.toString(camera.getTarget().getZ()));
	}

	@FXML
	private void handleMousePressed(MouseEvent mouseEvent) {
		anchorPane.requestFocus();
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			startXLMB = mouseEvent.getSceneX();
			startYLMB = mouseEvent.getSceneY();
		} /*
			 * else if (mouseEvent.getButton() == MouseButton.SECONDARY) { startXRMB =
			 * mouseEvent.getSceneX(); startYRMB = mouseEvent.getSceneY(); }
			 */
	}

	@FXML
	private void handleMouseClickedVBoxMain(MouseEvent mouseEvent) {
		vBoxMain.requestFocus();
	}

	@FXML
	private void handleMouseDragged(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			endXLMB = mouseEvent.getSceneX();
			endYLMB = mouseEvent.getSceneY();
			double shiftXLBM = endXLMB - startXLMB;
			double shiftYLMB = endYLMB - startYLMB;

			camera.movePosition(new Vector3f((float) shiftXLBM * 0.05f, (float) shiftYLMB * 0.05f, 0));
			camera.moveTarget(new Vector3f((float) shiftXLBM * 0.05f, (float) shiftYLMB * 0.05f, 0));

			startXLMB = mouseEvent.getSceneX();
			startYLMB = mouseEvent.getSceneY();
		} /*
			 * else if (mouseEvent.getButton() == MouseButton.SECONDARY) { endXRMB =
			 * mouseEvent.getX(); endYRMB = mouseEvent.getY(); double shiftXRBM = endXRMB -
			 * startXRMB; double shiftYRMB = endYRMB - startYRMB; }
			 */
	}

	@FXML
	private void handleMouseScroll(ScrollEvent scrollEvent) {
		if (scrollEvent.getDeltaY() > 0)
			camera.movePosition(new Vector3f(0, 0, -TRANSLATION * 10));
		else
			camera.movePosition(new Vector3f(0, 0, TRANSLATION * 10));
	}

	@FXML
	private void handleThemeChange() {
		isInLightMode = !isInLightMode;
		anchorPane.getStylesheets().clear();
		String filePath = isInLightMode ? "styles/light_mode.css" : "styles/dark_mode.css";
		anchorPane.getStylesheets().add(getClass().getResource(filePath).toExternalForm());
	}

	/**
	 * Добавить обработчик событий дял всех textField по нажатии кнопки Enter
	 */

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

		Triangulation.triangulateModel(mesh);
		mesh.normals = FindNormals.findNormals(mesh);
		mesh.setTexture(null);
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