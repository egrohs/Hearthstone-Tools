package hcs;

import java.util.Set;

import hcs.model.Carta;
import hcs.model.Carta.CLASS;
import hcs.model.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.getText().Font;
import javafx.scene.getText().Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
	static Stage stage;
	// private static SynergyBuilder sb;
	private static Sinergias s;
	private static VBox box;
	// private static TextField tf;
	// private static Spinner<Integer> ss;
	// private static ComboBox<CLASS> cb;
	public static VBox cartas;
	private static TextArea ta;
	private static CLASS opo = CLASS.MAGE;

	@Override
	public void start(Stage stage) {
		App.stage = stage;
		// stage.initStyle(StageStyle.TRANSPARENT);
		// funfou
		// stage.setOpacity(0.5);
		ScrollPane sp = new ScrollPane();
		box = new VBox();
		sp.setContent(box);
		// tf = new TextField("entomb");
		// tf.setMaxSize(120, 20);
		// box.getChildren().add(tf);
		// RadioButton rb = new RadioButton("WAR");
		// box.getChildren().add(rb);
		// RadioButton rb1 = new RadioButton("HUN");
		// box.getChildren().add(rb1);
		// ss = new Spinner<Integer>(1, 10, 2);
		// ss.setMaxSize(50, 20);
		// box.getChildren().add(ss);
		// TODO fazer lista visivel da combo abrir maior.
		// cb = new ComboBox<CLASS>(new
		// ImmutableObservableList<CLASS>(Arrays.copyOfRange(CLASS.values(), 0,
		// 9)));
		// cb.setValue(CLASS.PRIEST);
		// box.getChildren().add(cb);
		// TODO bot�o de reset game?
		// Button b = new Button("GO");
		// box.getChildren().add(b);
		cartas = new VBox();
		ta = new TextArea();
		ta.setEditable(false);
		ta.setMaxSize(145, 100);
		box.getChildren().add(ta);
		box.getChildren().add(cartas);
		// b.setOnAction(new EventHandler<ActionEvent>() {
		// @Override
		// public void handle(ActionEvent event) {
		// calcula(null);
		// }
		// });
		Scene scene = new Scene(sp);
		// funfou
		// scene.getStylesheets().add("main.css");
		// nao funfou
		// scene.setFill(Color.TRANSPARENT);
		// nao funfou
		// scene.setFill(null);
		stage.setScene(scene);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		// set Stage boundaries to visible bounds of the main screen
		stage.setX(primaryScreenBounds.getMinX());
		stage.setY(primaryScreenBounds.getMinY());
		stage.setWidth(160);
		stage.setHeight(primaryScreenBounds.getHeight());
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	public static void decks(String texto) {
		ta.setText(texto);
		// System.out.println("TEXTO :" + texto);
	}

	public static void provaveis(Set<Carta> temp) {
		// Necessario para evitar java.lang.IllegalStateException: Not on FX
		// application thread;
		// Outra thread atualizando a interface.
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// if (card != null) {
				// searched.add(card);
				// }
				// SinergyFromText.generateCardSynergies(card);
				// if (cb != null && cb.getValue() != null)
				{
					// ta.setText("");
					cartas.getChildren().removeAll(cartas.getChildren());
					// Carta card =
					// Universo.getCard(tf.getText().toLowerCase());
					// TODO a classe deve ser deduzida uma �nica vez pelo log.
					// if (opo == CLASS.NEUTRAL) {
					// opo = Universo.whichClass(searched);
					// }
					// int cont = (int)
					// Screen.getPrimary().getVisualBounds().getHeight() / 34 -
					// 2;
					int num = 1;
					for (Carta c : temp.keySet()) {
						// Mostra s� as primeiras 30.
						// if (num > 30)
						// break;
						String text = temp.get(c);
						// StackPane stackPane = new StackPane();
						// ImageView iv = new ImageView(new
						// Image("file:res/cards/" + c.id + ".png"));
						// if (c.getScene() == null)
						{
							// stackPane.getChildren().add(c);
							// StackPane.setAlignment(c, Pos.CENTER_LEFT);
							// String t = num + ") " + c.getName() + " f:" +
							// sinergia.freq + " v:" + sinergia.valor;
							Rectangle r = new Rectangle(130, 10, Color.BLACK);
							Text t1 = new Text(num + ") " + text);
							t1.setFill(Color.WHITE);
							t1.setFont(new Font(10));
							/* stackPane */c.getChildren().add(r);
							StackPane.setAlignment(r, Pos.CENTER_LEFT);
							/* stackPane */c.getChildren().add(t1);
							StackPane.setAlignment(t1, Pos.CENTER_LEFT);
							cartas.getChildren().add(c);
							// cont--;
							// if (cont == 0) {
							// break;
							// }
							num++;
						}
					}
					// ss.increment();
					// SinergyFromGames.imprimSins();
				}
				// jhgjhgj
				// ZoneLogReader.pendente--;
				// System.out.println("PENDENTE: "+ZoneLogReader.pendente);
			}
		});
	}

	public static void main(String[] args) {
		Universo.leCards();
		SinergyFromGames.leSinergias();
		s = new Sinergias();
		new DeckFinder();
		new PowerLogReader().start();
		new ZoneLogReader().start();
		new Game().start();
		// TODO detectar qndo um jogo comeca e termina para resetar as
		// variaveis.
		launch(args);
	}
}