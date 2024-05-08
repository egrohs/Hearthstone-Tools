package hstools.domain.components;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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

	String bNETClientID = "121e0dd9cccb413da3924481f079418d";
	String bNETClientSecret = "Pxw7S3pT1I2DIev2gdEFTdK0vYAaMXv1";
	// curl -u {client_id}:{client_secret} -d grant_type=client_credentials https://oauth.battle.net/token
	String bNEToauthURL = "https://oauth.battle.net/token";
	
	String api = "https://us.api.blizzard.com/hearthstone/cards/678?locale=en_US";

	public NetworkComponent() {
		headers.set("X-RapidAPI-Key", "MpVxk1YV1GmshautO7ajo2MGPknxp1Y1ovXjsnsfl9vhykCnsf");
		headers.set("X-RapidAPI-Host", "omgvamp-hearthstone-v1.p.rapidapi.com");
	
		String encoding = Base64.getEncoder().encodeToString((bNETClientID + ":" + bNETClientSecret).getBytes());
		headers.set("Authorization", "Basic " + encoding);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	}

	public static void main(String[] args) {
		NetworkComponent nc = new NetworkComponent();
		nc.bNEToauthURL();
	}

	private void bNEToauthURL() {
	    HttpEntity<String> requestEntity2 = new HttpEntity<>("grant_type=client_credentials", headers);
		ResponseEntity<String> response = rest.postForEntity(bNEToauthURL, requestEntity2, String.class);
		System.out.println(response);
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
		ResponseEntity<RapidApiInfo> response = rest.exchange("https://omgvamp-hearthstone-v1.p.rapidapi.com/info",
				HttpMethod.GET, requestEntity, RapidApiInfo.class, (Object) null);
		return response.getBody();
	}
}