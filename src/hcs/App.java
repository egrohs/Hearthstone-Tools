package hcs;

import java.util.Set;

import hcs.Carta.CLASS;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
	static Stage stage;

	@Override
	public void start(Stage stage) {
		App.stage = stage;
		stage.initStyle(StageStyle.TRANSPARENT);
		// Text text = new Text("Transparent!");
		// text.setFont(new Font(40));
		Scene scene = monta();
		// VBox box = new VBox();
		// box.getChildren().add(new ImageView(new
		// Image("file:Bars/AT_001.png")));
		// box.getChildren().add(new ImageView(new
		// Image("file:Bars/AT_002.png")));
		// box.getChildren().add(text);
		// final Scene scene = new Scene(box, 300, 250);
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		// set Stage boundaries to visible bounds of the main screen
		stage.setX(primaryScreenBounds.getMinX());
		stage.setY(primaryScreenBounds.getMinY());
		stage.setWidth(300);
		stage.setHeight(primaryScreenBounds.getHeight());
		stage.setScene(scene);
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	public static void main(String[] args) {
		Universo.leCards();
		Jogos.leSinergias();
		launch(args);
	}

	private static Scene monta() {
		VBox box = new VBox();
		final TextField tf = new TextField();
		box.getChildren().add(tf);
		Button b = new Button("GO");
		box.getChildren().add(b);
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Set<Sinergia> sub = Jogos.provaveis(Universo.getCard(tf.getText().toLowerCase()), 1,CLASS.WARRIOR);
				int cont = (int) Screen.getPrimary().getVisualBounds().getHeight() / 34 - 2;
				for (Sinergia sinergia : sub) {
					box.getChildren().add(new ImageView(new Image("file:Bars/" + ((Carta) sinergia.e2).id + ".png")));
					cont--;
					if (cont == 0) {
						break;
					}
				}
			}
		});
		Scene sc = new Scene(box);
		sc.setFill(null);
		return sc;
	}
}