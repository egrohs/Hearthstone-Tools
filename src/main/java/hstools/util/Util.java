package hstools.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Util {
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

	private void sortByProp(Collection c, String prop) {
		//c.sort
	}
}
