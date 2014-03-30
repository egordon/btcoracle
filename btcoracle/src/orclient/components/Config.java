package orclient.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Config {
	private Stage stage;
	private Scene scene;
	private GridPane grid;

	public Config(final Main main) {
		grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.getColumnConstraints().add(new ColumnConstraints(150));
		grid.getColumnConstraints().add(new ColumnConstraints(300));
		grid.getColumnConstraints().add(new ColumnConstraints(100));
		grid.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);

		Label lblURL = new Label("Oracle URL:");
		Label lblPublicKey = new Label("Oracle public key:");
		Label lblAddress = new Label("Local address:");

		final TextField tfURL = new TextField();
		final TextField tfPublicKey = new TextField();
		final TextField tfAddress = new TextField();
		tfAddress.setDisable(true);

		Button btnGen = new Button("Generate");
		btnGen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO egordon fill this out!
				tfAddress.setText(Math.random() + "");
			}
		});

		Button btnNext = new Button("Proceed");
		btnNext.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// TODO egordon what do you want to do with the info?
				main.gotoHome(stage);
			}
		});

		grid.add(lblURL, 0, 0);
		grid.add(lblPublicKey, 0, 1);
		grid.add(lblAddress, 0, 2);

		grid.add(tfURL, 1, 0, 2, 1);
		grid.add(tfPublicKey, 1, 1, 2, 1);
		grid.add(tfAddress, 1, 2, 1, 1);

		grid.add(btnGen, 2, 2, 1, 1);
		grid.add(btnNext, 0, 3);
	}

	public Scene getScene() {
		if (scene == null) {
			scene = new Scene(grid);
		}
		return scene;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}