package hcs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TGFParser {
	public static Map<String, Mechanic> mechs = new HashMap<String, Mechanic>();

	public TGFParser() {
		readMechanics("input/hs.tgf");
		loop();
	}

	private void loop() {
		for (Mechanic m : mechs.values()) {
			m.aff.add(m);
		}
	}

	/**
	 * Lê arquivo de grafo tgf contendo relacionamento entre as mecanicas.
	 * 
	 * @param file
	 *            Arquivo tgf das mecanicas.
	 */
	private void readMechanics(String file) {
		Scanner sc = null;
		try {
			sc = new Scanner(new FileReader(file));
			boolean nodes = true;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if ("#".equals(line)) {
					nodes = false;
					continue;
				}
				if (nodes) {
					String id = line.substring(0, line.indexOf(" "));
					String regex = line.substring(line.indexOf(" ") + 1);
					// cria nodo
					// ns.put(s[0], new Mechanic());
					mechs.put(id, new Mechanic(id, regex));
				} else {
					// cria vinculo bidirecional?
					String[] s = line.split(" ");
					mechs.get(s[0]).aff.add(mechs.get(s[1]));
					mechs.get(s[1]).aff.add(mechs.get(s[0]));
				}

			}
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + file + " not found");
			System.exit(1);
		} finally {
			sc.close();
		}
	}
}