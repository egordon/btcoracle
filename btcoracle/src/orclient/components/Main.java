package orclient.components;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	private Config config;
	private Home home;

	public static void main(String[] args) {
		launch(args);
	}

	public Main() {
		config = new Config(this);
		home = new Home();
	}

	@Override
	public void start(Stage stage) {
		config.setStage(stage);
		home.setStage(stage);
		
		stage.setResizable(false);
		stage.setScene(config.getScene());
		stage.setTitle("Configuration");
		stage.show();
	}

	public void gotoHome(Stage stage) {
		stage.setTitle("Bitcoin Oracle");
		stage.setScene(home.getScene());
		stage.sizeToScene();
	}
}