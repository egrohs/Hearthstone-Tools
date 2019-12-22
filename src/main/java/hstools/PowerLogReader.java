package hstools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PowerLogReader extends Thread {
	private long pos;
	// private long size;
	static Pattern gameOver = Pattern.compile(
			"D (\\d+\\:\\d+\\:\\d+\\.\\d+) GameState\\.DebugPrintPower\\(\\) \\-\\s+TAG_CHANGE Entity=GameEntity tag=NEXT_STEP value=FINAL_GAMEOVER");
	// TODO \w+ basta aqui? e espa�os??
	static Pattern mana = Pattern.compile(
			"D (\\d+\\:\\d+\\:\\d+\\.\\d+) GameState\\.DebugPrintPower\\(\\) \\-\\s+TAG_CHANGE Entity=(\\w+) tag=RESOURCES value=(\\d+)");
	// Pattern turn = Pattern.compile("TAG_CHANGE Entity=GameEntity tag=TURN
	// value=(\\d+)");
	// static Pattern turn = Pattern.compile("TAG_CHANGE Entity=(\\w+)
	// tag=CURRENT_PLAYER value=1");
	private static Matcher matcher;
	static RandomAccessFile power;
	// public static Map<LocalTime, Integer> manaMap = new TreeMap<LocalTime,
	// Integer>();
	public static LocalTime lastManaTime, lastOverTime;
	public static int lastMana;
	public static boolean done;
	File powerlog = new File("C:\\Program Files (x86)\\Hearthstone\\Logs\\Power.log");
	// private boolean deleted = true;

	private void parseFile() {
		if (powerlog.exists() && power != null) {
			// System.out.println("POWERLOG EXISTE");
			done = false;
			String line = null;
			try {
				// long s = power.length();
				// if (s < size) {
				// // arquivo foi zerado
				// } else {
				// power.seek(pos);
				// }
				// size = s;
				while ((line = power.readLine()) != null) {
//					if (line.contains("Gul'dan")) {
//						System.out.println(line);
//					}
					matcher = gameOver.matcher(line);
					if (matcher.find()) {
						// end game
						// manaMap.put(LocalTime.parse(matcher.group(1)), 0);
						lastOverTime = LocalTime.parse(matcher.group(1));
					} else {
						matcher = mana.matcher(line);
						// TODO cuidar aquela carta que volta turno?
						// TODO qual dos 2 testes?
						// System.out.println(line);
						if ((matcher.find() || matcher.matches()) && !matcher.group(2).equals("Egrohs")) {
							// System.out.println("GROUP: " + matcher.group());
							// System.out.println("TIME: " + matcher.group(1));
							// System.out.println("PLAYER: " +
							// matcher.group(2));
							// System.out.println("MANA: " + matcher.group(3));
							// Game.evtMap.put(LocalTime.parse(matcher.group(1)),
							// new Evento(null,
							// Integer.parseInt(matcher.group(3))));
							// manaMap.put(LocalTime.parse(matcher.group(1)),
							// Integer.parseInt(matcher.group(3)));
							lastManaTime = LocalTime.parse(matcher.group(1));
							lastMana = Integer.parseInt(matcher.group(3));
						}
					}
				}
				pos = power.getFilePointer();
				power.close();
				power = null;
				done = true;
				// System.out.println("POWER LOG LIDO...");
				Thread.sleep(5000);
			} catch (NumberFormatException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				power = new RandomAccessFile(powerlog, "r");
				power.seek(pos);
			} catch (IOException e) {
				// jogo ainda n�o abriu.
				System.err.println("Aguardando nova partida...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			parseFile();
		}
	}
}
