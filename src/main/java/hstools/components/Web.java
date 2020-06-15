package hstools.components;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Web {
	public Document getDocument(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(30000)
					.post();
		} catch (IOException e) {
			try {
				Thread.sleep(2000);
				return getDocument(url);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return doc;
	}
}
