package hcs;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class EscreveArquivo {
	public static void escreveArquivo(String fileName, String texto) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName);
			out.println(texto);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
}
