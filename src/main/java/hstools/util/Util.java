package hstools.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Util {
	private static ClassLoader clsLoader = Util.class.getClassLoader();

	public static Object file2JSONObject(String fname) {
		JSONParser parser = new JSONParser();
		try {
			return parser.parse(new FileReader(new File(fname)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<String[]> csv2CardSyns() {
		List<String[]> syns = new ArrayList<String[]>();
		Scanner sc = null;
		try {
			sc = new Scanner(new File(clsLoader.getResource("synergy/combos.csv").getFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// skip header row
		sc.nextLine();
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split(";");
			syns.add(line);
		}
		sc.close();
		System.out.println(syns.size() + " combos loaded.");

		return syns;
	}
}