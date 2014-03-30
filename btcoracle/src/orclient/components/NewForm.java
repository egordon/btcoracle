package orclient.components;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class NewForm extends GridPane {
	public NewForm() {
		this.setMinWidth(500);
		this.setMinHeight(300);
		this.setPadding(new Insets(20, 20, 20, 20));
		this.setStyle("-fx-border-width:1; -fx-border-color:black;");
	}
}