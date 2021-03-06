package orclient.components;

import java.net.URLEncoder;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orclient.util.GlobalConfig;
import orclient.util.Hasher;
import orclient.util.LocalBTC;

import org.json.JSONObject;

public class Home {
	private Stage stage;
	private Scene scene;
	private VBox vbox;

	private ToggleGroup group;
	private ComboBox<String> cbTransactions;
	
	private NewForm generate;
	private NewForm review;

	public Home() {
		group = new ToggleGroup();
		RadioButton rb1 = new RadioButton("Create a new transaction");
		RadioButton rb2 = new RadioButton("Review existing transactions");
		rb1.setToggleGroup(group);
		rb2.setToggleGroup(group);
		rb1.setSelected(true);

		VBox vboxRb = new VBox(10);
		vboxRb.getChildren().addAll(rb1, rb2);

		initGenerateForm();
		initReviewForm();

		rb1.setUserData(generate);
		rb2.setUserData(review);

		vbox = new VBox(25);
		vbox.setPadding(new Insets(50, 50, 50, 50));
		vbox.getChildren().addAll(vboxRb, generate);

		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				vbox.getChildren().remove(1);
				vbox.getChildren().add((NewForm) group.getSelectedToggle().getUserData());
			}
		});
	}

	private void initGenerateForm() {
		generate = new NewForm();

		generate.setHgap(10);
		generate.setVgap(10);

		Label lblRec = new Label("Send to Address:");
		Label lblAmt = new Label("Amount: ");
		Label lblPython = new Label("Python Condition: ");
		Label lblWarning = new Label("WARNING: Only sign if you are CERTAIN about this transaction!");

		final TextField tfRec = new TextField();
		final TextField tfAmt = new TextField();
		final TextArea taPython = new TextArea();
		taPython.setStyle("-fx-font-family: \"Courier New\";");

		GridPane.setHgrow(tfRec, Priority.ALWAYS);
		GridPane.setHgrow(tfAmt, Priority.ALWAYS);
		GridPane.setHgrow(taPython, Priority.ALWAYS);

		Button btnSign = new Button("Sign Transaction");
		btnSign.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				// TODO: Sign Transaction Script
				String txid = LocalBTC.sendFromAccount(GlobalConfig.globalConfig.get("account"),
						GlobalConfig.globalConfig.get("sendAddress"), Double.parseDouble(tfAmt.getText()));
				String raw = LocalBTC.createTransaction(txid, tfRec.getText(), Double.parseDouble(tfAmt.getText()));
				raw = Hasher.addHashToTransaction(raw, Hasher.sha256(taPython.getText()));
				String signed = LocalBTC.signTransaction(raw);
				String python = taPython.getText();
				String pyHash = Hasher.sha256(python);
				String confirmed = "0";
				String fullSigned = "";

				// Now write to global config
				GlobalConfig.writeTransaction(txid, raw, signed, fullSigned, confirmed, python, pyHash);
				cbTransactions.getItems().add(txid);
				group.selectToggle(group.getToggles().get(1));
			}
		});

		// Add components to generate form
		generate.add(lblRec, 0, 0);
		generate.add(tfRec, 1, 0, 2, 1);
		generate.add(lblAmt, 0, 1);
		generate.add(tfAmt, 1, 1, 2, 1);
		generate.add(lblPython, 0, 2);
		generate.add(taPython, 0, 3, 3, 2);
		generate.add(lblWarning, 0, 5, 2, 1);
		generate.add(btnSign, 2, 5, 1, 1);
	}

	private void initReviewForm() {
		review = new NewForm();
		review.setVgap(10);

		cbTransactions = new ComboBox<String>();
		cbTransactions.setVisibleRowCount(6);

		final TextArea taDetails = new TextArea();
		taDetails.setStyle("-fx-font-family: \"Courier New\";");
		taDetails.setEditable(false);
		taDetails.setWrapText(true);
		cbTransactions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String s1, String s2) {
				taDetails.setText(GlobalConfig.getTransaction(s2).toString());
			}
		});
		if (!cbTransactions.getItems().isEmpty()) {
			cbTransactions.getSelectionModel().selectFirst();
		}

		final Stage popup = new Stage();
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.setTitle("Notice");
		final Label message = new Label("");
		final Button button = new Button("OK");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				popup.close();
			}
		});
		VBox popupVbox = new VBox(10);
		popupVbox.setAlignment(Pos.CENTER);
		popupVbox.setPadding(new Insets(10, 10, 10, 10));
		popupVbox.getChildren().addAll(message, button);
		popup.setScene(new Scene(popupVbox, 200, 150));

		Button btnSend = new Button("Send to Oracle");
		btnSend.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent ae) {
				// TODO egordon add functionality!
				String txid = cbTransactions.getSelectionModel().getSelectedItem();
				JSONObject js = GlobalConfig.getTransaction(txid);
				String signed = js.getString("partialSigned");
				String python = js.getString("python");
				JSONObject fullSigned = LocalBTC.sendToOracle(signed, URLEncoder.encode(python));
				String fs = fullSigned.getJSONArray("data").getString(0);
				System.out.println(fullSigned);
				if (fs.length() < 50) message.setText("Oracle failed to sign \ntransaction.");
				else {
					message.setText("Transaction success!");
					LocalBTC.sendTransaction(fs);
					GlobalConfig.confirmTX(txid, fs);
				}
				int index = cbTransactions.getSelectionModel().getSelectedIndex();
				cbTransactions.getSelectionModel().clearAndSelect(index);
				popup.show();
			}
		});

		GridPane.setHgrow(cbTransactions, Priority.ALWAYS);
		GridPane.setHgrow(taDetails, Priority.ALWAYS);
		GridPane.setVgrow(taDetails, Priority.ALWAYS);
		
		review.add(cbTransactions, 0, 0);
		review.add(taDetails, 0, 1);
		review.add(btnSend, 0, 2);
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