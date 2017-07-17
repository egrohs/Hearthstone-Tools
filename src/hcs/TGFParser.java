package hcs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TGFParser {
	static Map<String, Mecanica> mechanics = new HashMap<String, Mecanica>();
	public static List<Sinergia> mechanicsSynergies = new ArrayList<Sinergia>();

	public TGFParser() {
		readMechanics("res/mechanics/hs.tgf");
		// loop();
	}

	// TODO mecanicas devem ter (sinergia com elas mesmas?????
	// private void loop() {
	// for (Mecanica m : mechanics.values()) {
	// // exclui as mecanicas calculadas
	// if (!Character.isUpperCase(m.regex.charAt(0))) {
	// m.aff.put(m, 0f);
	// }
	// }
	// }
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
					mechanics.put(id, new Mecanica(id, regex));
				} else {
					String[] s = line.split(" ");
					Float v = 0f;
					try {
						v = Float.parseFloat(s[2]);
					} catch (Exception e) {
					}
					// TODO cria vinculo bidirecional?
					Mecanica m1 = mechanics.get(s[0]);
					Mecanica m2 = mechanics.get(s[1]);
					mechanicsSynergies.add(new Sinergia(m1, m2, v, m1.regex + "+" + m2.regex));
					// mechanicsSynergies.add(new Synergy(mechanics.get(s[1]),
					// mechanics.get(s[0]), v));
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + file + " not found");
			sc.close();
			System.exit(1);
		} finally {
			sc.close();
		}
	}
}