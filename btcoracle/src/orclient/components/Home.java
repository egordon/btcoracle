package orclient.components;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Home {
	private Stage stage;
	private Scene scene;
	private VBox vbox;

	public Home() {
		vbox = new VBox(50);
		vbox.setPadding(new Insets(50, 50, 50, 50));
		
		final ToggleGroup group = new ToggleGroup();
		RadioButton rb1 = new RadioButton("Create a new transaction");
		RadioButton rb2 = new RadioButton("Review existing transactions");
		rb1.setToggleGroup(group);
		rb2.setToggleGroup(group);
		rb1.setSelected(true);
		
		VBox vboxRb = new VBox(10);
		vboxRb.getChildren().addAll(rb1, rb2);
		
		vbox.getChildren().add(vboxRb);
	}

	public Scene getScene() {
		if (scene == null) {
			scene = new Scene(vbox);
		}
		return scene;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}