package hstools.domain.components;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import hstools.domain.entities.RapidApiInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NetworkComponent {
	RestTemplate rest = new RestTemplate();
	HttpHeaders headers = new HttpHeaders();
	HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

	public NetworkComponent() {
		headers.set("X-RapidAPI-Key", "MpVxk1YV1GmshautO7ajo2MGPknxp1Y1ovXjsnsfl9vhykCnsf");
		headers.set("X-RapidAPI-Host", "omgvamp-hearthstone-v1.p.rapidapi.com");
	}

	public String rapidApiAllCollectibleCards() {
		log.info("downloading card data, takes around 30 secs...");
		long time = System.currentTimeMillis();
		ResponseEntity<String> response = rest.exchange("https://omgvamp-hearthstone-v1.p.rapidapi.com/cards",
				HttpMethod.GET, requestEntity, String.class, 1);
		log.info("card data downloaded in " + (System.currentTimeMillis() - time));
		return response.getBody();
	}

	public RapidApiInfo rapidApiInfo() {
		// HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<RapidApiInfo> response = rest.exchange("https://omgvamp-hearthstone-v1.p.rapidapi.com/info",
				HttpMethod.GET, requestEntity, RapidApiInfo.class, (Object) null);
		return response.getBody();
	}
}