package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.nio.file.SensitivityWatchEventModifier;

import javafx.application.Platform;

/**
 * Leitor de logs do hearthstone durante o jogo.
 * 
 * @author 99689650068
 *
 */
public class LogReader extends Thread {
	// static BufferedReader br = null;
	static RandomAccessFile raf;
	boolean first = true;

	public static void main1(String[] args) {
		Universo.leCards();
		DeckFinder.leDecks();
		new LogReader().start();
		DeckFinder.similaridade(new String[] { "N'Zoth's First Mate", "FIERY WAR AXE", "AZURE DRAKE" });
	}

	public static void main2(String[] args) {
		Universo.leCards();
		try {
			// zone from -> OPPOSING GRAVEYARD // moeda
			// Pattern p = Pattern.compile("OPPOSING (HAND|SECRET) -> OPPOSING
			// (PLAY|GRAVEYARD|SECRET)");
			// Matcher m;
			// int cont = 0;
			raf = new RandomAccessFile(new File("res/hs.log"), "r");
			// String line = null;
			// while ((line = bufReader.readLine()) != null) {
			// m = p.matcher(line);
			// System.out.println("LINHA: " + line);
			// if (m.find() || m.matches()) {
			leNovaLinha();
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Universo.leCards();
		DeckFinder.leDecks();
		new LogReader().start();
	}

	static Set<Carta> cars = new HashSet<Carta>();

	private static Carta leNovaLinha() {
		String sCurrentLine;
		// Pattern p = Pattern.compile("OPPOSING (HAND|SECRET) -> OPPOSING
		// (PLAY|GRAVEYARD|SECRET)");
		Pattern p = Pattern.compile("OPPOSING (HAND|SECRET) ->");
		// TODO com eese pattern tb esta pegando as cards descartadas
		// indevidamente?
		Matcher m;
		try {
			// raf = new RandomAccessFile(new File(path), "r");
			while ((sCurrentLine = raf.readLine()) != null) {
				// sCurrentLine = bufReader.readLine();
				// System.out.println(sCurrentLine);
				// lastLine = sCurrentLine;
				m = p.matcher(sCurrentLine);
				// TODO qual dos 2 testes?
				if (m.find() || m.matches()) {
					// System.out.println(sCurrentLine);
					String[] tags = sCurrentLine.split(" ");
					for (String tag : tags) {
						if (tag.startsWith("cardId")) {
							String id = tag.substring(tag.indexOf("=") + 1, tag.length());
							Carta c = Universo.getCard(id);
							if (c != null) {
								System.out.println(c);
								// cars.add(c);
								// DeckFinder.similaridade(cars);
								App.calcula(c);
								cont--;
								if(cont<=0)return c;
							}
							// System.out.println(pos);
							// return c;
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				pos = raf.getFilePointer();
				raf.close();
			} catch (IOException e) {
				// // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	static int cont=2;
	private static long pos;
	// static String path = "C:\\Program Files
	// (x86)\\Hearthstone\\Logs\\Zone.log";
	static String path = "C:\\Program Files (x86)\\Hearthstone\\Hearthstone_Data\\output_log.txt";

	@Override
	public void run() {
		
		try {
			while (true) {
				if(cont<=0)break;
				raf = new RandomAccessFile(new File(path), "r");
				if (first) {
//					raf.seek(raf.length());
					first = false;
				} else {
					raf.seek(pos);
				}
				Carta c = leNovaLinha();
				
				Thread.sleep(1000);
			}
		} catch (FileNotFoundException fnf) {
			// TODO jogo ainda não abriu, tratar?
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run3() {
		final Path path = Paths.get("C:\\Program Files (x86)\\Hearthstone\\Logs");
		WatchService watchService;
		try {
			watchService = path.getFileSystem().newWatchService();
			// path.register(watchService,
			// StandardWatchEventKinds.ENTRY_MODIFY);
			path.register(watchService, new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_MODIFY },
					SensitivityWatchEventModifier.HIGH);
			WatchKey watchKey = null;
			while (true) {
				watchKey = watchService.poll(500, TimeUnit.MILLISECONDS);
				if (watchKey != null) {
					// watchKey.pollEvents().stream().forEach(event ->
					// System.out.println(event.context()));
					for (WatchEvent<?> event : watchKey.pollEvents()) {
						verifica(path, (Path) event.context());
					}
					watchKey.reset();
				}
				Thread.sleep(50);
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run2() {
		// TODO mudar path dinamico. Talvez tenha que fechar o raf e abrir
		// sempre.
		// final Path path = Paths.get("C:\\Program Files
		// (x86)\\Hearthstone\\Hearthstone_Data");
		final Path path = Paths.get("C:\\Program Files (x86)\\Hearthstone\\Logs");
		// System.out.println("PATH: " + path);
		try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
			final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			// final WatchKey watchKey = path.register(watchService, new
			// WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY},
			// SensitivityWatchEventModifier.HIGH);
			while (true) {
				final WatchKey wk = watchService.take();
				for (WatchEvent<?> event : wk.pollEvents()) {
					// we only register "ENTRY_MODIFY" so the context is always
					// a Path.
					final Path changed = (Path) event.context();
					// System.out.println("uri: " + changed.toUri());
					// System.out.println(changed.getFileName());
					// System.out.println(changed.getParent());
					// System.out.println(changed);
					// if (changed.endsWith("output_log.txt")) {
					verifica(path, changed);
				}
				// reset the key
				boolean valid = wk.reset();
				if (!valid) {
					System.out.println("Key has been unregisterede");
				}
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void verifica(final Path path, final Path changed) throws FileNotFoundException, IOException {
		if (changed.endsWith("Zone.log")) {
			// System.out.println("My file has changed");
			raf = new RandomAccessFile(new File(path.toString() + "\\" + changed.getFileName()), "r");
			if (pos == 0) {
				System.out.println("LISTENING: " + changed);
				// br = new BufferedReader(new FileReader(new
				// File(path.toString() + "\\" +
				// changed.getFileName())));
			} else {
				// TODO posicionar no arquivo onde o jogo comecou... pelo
				// horario
				raf.seek(pos);
			}
			Carta c = leNovaLinha();
			App.calcula(c);
		}
	}
}