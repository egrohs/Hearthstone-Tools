package hcs;

import java.util.Set;

import com.sun.javafx.collections.ImmutableObservableList;

import hcs.Carta.CLASS;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
		VBox box = new VBox();
		final TextField tf = new TextField();
		tf.setMaxSize(130, 20);
		box.getChildren().add(tf);
		// RadioButton rb = new RadioButton("WAR");
		// box.getChildren().add(rb);
		// RadioButton rb1 = new RadioButton("HUN");
		// box.getChildren().add(rb1);
		Spinner<Integer> ss = new Spinner<Integer>(1, 10, 1);
		box.getChildren().add(ss);
		ComboBox<CLASS> cb = new ComboBox<CLASS>(new ImmutableObservableList<CLASS>(CLASS.values()));
		box.getChildren().add(cb);
		Button b = new Button("GO");
		box.getChildren().add(b);
		VBox cartas = new VBox();
		box.getChildren().add(cartas);
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cartas.getChildren().removeAll(cartas.getChildren());
				Set<Sinergia> sub = Jogos.provaveis(Universo.getCard(tf.getText().toLowerCase()), ss.getValue(),
						cb.getValue());
				int cont = (int) Screen.getPrimary().getVisualBounds().getHeight() / 34 - 2;
				for (Sinergia sinergia : sub) {
					StackPane stackPane = new StackPane();
					Carta c = (Carta) sinergia.e2;
					stackPane.getChildren().add(new ImageView(new Image("file:Bars/" + c.id + ".png")));
					Text t = new Text(sinergia.valor.toString());
					t.setFill(Color.WHITE);
					t.setFont(new Font(20));
					stackPane.getChildren().add(t);
					cartas.getChildren().add(stackPane);
					cont--;
					if (cont == 0) {
						break;
					}
				}
			}
		});
		Scene scene = new Scene(box);
		scene.setFill(null);
		stage.setScene(scene);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		// set Stage boundaries to visible bounds of the main screen
		stage.setX(primaryScreenBounds.getMinX());
		stage.setY(primaryScreenBounds.getMinY());
		stage.setWidth(130);
		stage.setHeight(primaryScreenBounds.getHeight());
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	public static void main(String[] args) {
		Universo.leCards();
		Jogos.leSinergias();
		launch(args);
	}
}