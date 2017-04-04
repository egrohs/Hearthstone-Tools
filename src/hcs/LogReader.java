package hcs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogReader {
	public static void main(String[] args) {
		try {
			Scanner s = new Scanner(new File("hs.log"));
			Pattern p = Pattern.compile("OPPOSING (HAND|SECRET) -> OPPOSING (PLAY|GRAVEYARD|SECRET)$");
			Matcher m;
			while (s.hasNextLine()) {
				String line = s.nextLine();
				// zone from -> OPPOSING GRAVEYARD // moeda
				m = p.matcher(line);
				if (m.find()) {
					System.out.println(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ww() throws IOException, InterruptedException {
		final Path path = FileSystems.getDefault().getPath(System.getProperty("user.home"), "Desktop");
		System.out.println(path);
		try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
			final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			while (true) {
				final WatchKey wk = watchService.take();
				for (WatchEvent<?> event : wk.pollEvents()) {
					// we only register "ENTRY_MODIFY" so the context is always
					// a Path.
					final Path changed = (Path) event.context();
					System.out.println(changed);
					if (changed.endsWith("myFile.txt")) {
						System.out.println("My file has changed");
					}
				}
				// reset the key
				boolean valid = wk.reset();
				if (!valid) {
					System.out.println("Key has been unregisterede");
				}
			}
		}
	}
}