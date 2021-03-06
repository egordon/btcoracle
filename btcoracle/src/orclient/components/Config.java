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
import javafx.stage.Stage;
import orclient.application.Main;
import orclient.util.GlobalConfig;
import orclient.util.LocalBTC;

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

		Label lblURL = new Label("Oracle URL:");
		Label lblPublicKey = new Label("Oracle public key:");
		Label lblAddress = new Label("Local address:");

		final TextField tfURL = new TextField("http://btcoracle.ethankgordon.com:8000/");
		final TextField tfPublicKey = new TextField(
				"020a1b1653f15b1cd7b4b1667fff5b938845feced1141e11f38ccd9f3191b3f935");
		final TextField tfAddress = new TextField("mnE5BzptBDCPZYRMxtKMUwuzJKnaQuqTsN");
		// tfAddress.setDisable(true);

		Button btnGen = new Button("Generate");
		btnGen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				GlobalConfig.globalConfig.put("sendAddress", LocalBTC.newBTCAddress());
				tfAddress.setText(GlobalConfig.globalConfig.get("sendAddress"));
			}
		});

		Button btnNext = new Button("Proceed");
		btnNext.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GlobalConfig.globalConfig.put("sendAddress", tfAddress.getText());
				GlobalConfig.globalConfig.put("oracleURL", tfURL.getText());
				GlobalConfig.globalConfig.put("oraclePubKey", tfPublicKey.getText());

				// Generate the Multisig Address
				String localPubKey = LocalBTC.getPubKey(GlobalConfig.globalConfig.get("sendAddress"));
				GlobalConfig.globalConfig.put("multisig",
						LocalBTC.generateMultisigAddress(localPubKey, GlobalConfig.globalConfig.get("oraclePubKey")));

				// Account
				GlobalConfig.globalConfig.put("account", "orclient");

				LocalBTC.assignAccount(GlobalConfig.globalConfig.get("sendAddress"), "orclient");

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