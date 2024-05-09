package hstools.domain.components;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hstools.domain.entities.Card;
import hstools.domain.entities.RapidApiInfo;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NetworkComponent {
	RestTemplate rest = new RestTemplate();
	HttpHeaders headers = new HttpHeaders();
	HttpEntity<String> requestEntity = new HttpEntity<>("grant_type=client_credentials", headers);

	String bNETClientID = "121e0dd9cccb413da3924481f079418d";
	String bNETClientSecret = "Pxw7S3pT1I2DIev2gdEFTdK0vYAaMXv1";
	// curl -u {client_id}:{client_secret} -d grant_type=client_credentials
	// https://oauth.battle.net/token
	String bNEToauthURL = "https://oauth.battle.net/token";

	String api = "https://us.api.blizzard.com";
	String token = "EUqFJTZslul5F4yOw5VOowIaL72BA6lCa5";

	public NetworkComponent() {
		headers.set("X-RapidAPI-Key", "MpVxk1YV1GmshautO7ajo2MGPknxp1Y1ovXjsnsfl9vhykCnsf");
		headers.set("X-RapidAPI-Host", "omgvamp-hearthstone-v1.p.rapidapi.com");

		String encoding = Base64.getEncoder().encodeToString((bNETClientID + ":" + bNETClientSecret).getBytes());
		headers.set("Authorization", "Basic " + encoding);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBearerAuth(token);
	}

	public static void main(String[] args) {
		NetworkComponent nc = new NetworkComponent();
		nc.bNEToauthURL();
		// nc.bNETMetadata();
	}

	@JsonIgnoreProperties
	static class BNetOauth {
		@Getter
		String access_token;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	static class BNetDefault {
		String slug, id, name;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	static class BNetMetadata {
		Set<String> filterableFields;
		Set<BNetDefault> keywords, spellSchools, minionTypes, classes, rarities, types, sets;
	}

	private void bNETMetadata() {
		String url = "/hearthstone/metadata";
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(api + url).queryParam("locale", "en_US");
		System.out.println(builder.buildAndExpand(/* urlParams */).toUri());

		ResponseEntity<BNetMetadata> response2 = rest.exchange(builder.buildAndExpand().toUri(), HttpMethod.GET,
				requestEntity, BNetMetadata.class);
		System.out.println(response2);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	static class BNetCard {
//	private Integer collectible;
//	private Integer classId;
//	private Integer cardTypeId;
//	private Integer cardSetId;
//	private Integer rarityId;
//	private Boolean isZilliaxFunctionalModule;
//	private Set<Integer> multiClassIds;
//	private Set<Integer> keywordIds;
		private Set<Card> cards;
	}
	
	private void bNEToauthURL() {
		// HttpEntity<String> requestEntity2 = new
		// HttpEntity<>("grant_type=client_credentials", headers);
//		ResponseEntity<BNetOauth> response = rest.postForEntity(bNEToauthURL, requestEntity2, BNetOauth.class);
//		System.out.println(response);
//		String token = response.getBody().getAccess_token();
//		<200,{"access_token":"EUqFJTZslul5F4yOw5VOowIaL72BA6lCa5","token_type":"bearer","expires_in":86399,
//			"sub":"121e0dd9cccb413da3924481f079418d"},[Date:"Wed, 08 May 2024 14:56:56 GMT", Content-Type:"application/json;"
//			"charset=UTF-8", Transfer-Encoding:"chunked", Connection:"keep-alive", Vary:"Origin", "Access-Control-Request-Method", 
//			"Access-Control-Request-Headers", Cache-Control:"no-store", Pragma:"no-cache"]>

		String url = "/hearthstone/cards";// ?locale=en_US&name=A Light in the Darkness&set=descent-of-dragons";
		// URI (URL) parameters
		// O map eh usado ("/{var1}/path1/{var2}") usados em paramentros entre {} na url
		// Map<String, String> urlParams = new HashMap<>();
		//// urlParams.put(":region","us");
		// urlParams.put("locale", "en_US");
		// urlParams.put(":idorslug","52119-arch-villain-rafaam");

		// Query parameters
		// Acho q sao paramentros passados no=a url
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(api + url)
				// Add query parameter
				.queryParam("locale", "en_US")
				//// .queryParam(":region","us")
				// .queryParam(":idorslug","52119-arch-villain-rafaam")
				// .queryParam("name", "A Light in the Darkness")
				.queryParam("set", "descent-of-dragons")
		// gameMode=battlegrounds
		// keyword
		// textFilter
		;
		// To view only Standard cards, include the parameter set=standard or
		// setGroup=standard.
		// To return only uncollectible cards in a search, use the parameter
		// collectible=0. To return both collectible and uncollectible cards, use
		// collectible=0,1.
		// textFilter=give%20your%20minions returns all cards that include text such as
		// Give your minions +1/+1.

		System.out.println(builder.buildAndExpand(/* urlParams */).toUri());

//		ResponseEntity<String> response2 = rest.exchange(builder.buildAndExpand().toUri(), HttpMethod.POST, requestEntity,
//				String.class);

		ResponseEntity<BNetCard> response2 = rest.postForEntity(builder.buildAndExpand().toUri(),
				requestEntity, BNetCard.class/*new ParameterizedTypeReference<List<BNetCard>>() {}*/);

//		URIs follow a standard syntax using the format of {region}.api.blizzard.com/{API path}
//		ResponseEntity<String> response2 = rest.postForEntity(url, requestEntity2, String.class);

//		<200, {"cards":[{"id":38913,...},
//		Access-Control-Allow-Origin:"*", X-Powered-By:"BWA", blizzard-token-expires:"2024-05-09T17:07:23.500Z", ETag:"W/"386f4-5wNLayj08roM0BFQ18p06xCUFGQ"", 
//		x-trace-traceid:"3325eb62-7a30-3950-9dcf-29c682c7d4b6", x-trace-spanid:"17c29281-d7ae-15c0-2be0-fa164dd9e493", 
//		x-trace-parentspanid:"17c29281-d7ae-1590-2be0-fa164dd9e493", x-frame-options:"SAMEORIGIN", X-Content-Type-Options:"nosniff", server:"blizzard"]>
		System.out.println(response2);
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