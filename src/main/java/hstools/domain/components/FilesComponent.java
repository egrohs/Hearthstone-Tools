package hstools.domain.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import hstools.domain.entities.RapidApiInfo;

@Component
public class FilesComponent {
	ObjectMapper om = new ObjectMapper();
	private static ClassLoader clsLoader = FilesComponent.class.getClassLoader();

	public void updateRapidApiInfoFile(RapidApiInfo rapidApiInfo) {
		try (PrintWriter out = new PrintWriter("rapidApiInfo.json")) {
			String s = om.writeValueAsString(rapidApiInfo);
			out.println(s);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void updateCardsFile(String jsonCards) {
		try (PrintWriter out = new PrintWriter("cards.collectible.json")) {
			String cards = om.writeValueAsString(jsonCards);
			out.println(cards);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public RapidApiInfo loadRapidApiInfoFile() {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.readValue(new File("rapidApiInfo.json"), RapidApiInfo.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	public static CardSets file2Cards(String fname) {
//		ObjectMapper om = new ObjectMapper();
//		try {
//			return om.readValue(new File(fname), CardSets.class);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}

	public Object file2JSONObject(String fname) {
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

	public List<String[]> csv2CardSyns() {
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