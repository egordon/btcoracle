package orclient.application;

import orclient.components.Config;
import orclient.components.Home;
import orclient.util.LocalBTC;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	private Config config;
	private Home home;

	public static void main(String[] args) {
		if(!LocalBTC.checkBitcoind()) {
			System.err.println("Cannot find bitcoind! Please install it to use this app: https://en.bitcoin.it/wiki/Bitcoind");
			System.exit(1);
		}
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