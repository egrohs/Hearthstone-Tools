package hstools.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

/**
 * Utility to read google sheets.
 * 
 * @author EGrohs
 *
 */
//https://developers.google.com/sheets/api/quickstart/java
public class GoogleSheets {
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static Sheets service;
	// private static final String TOKENS_DIRECTORY_PATH = "tokens";

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		/* Google */Credential credentials = GoogleSheets.getCredentials(HTTP_TRANSPORT);
		// Create a Sheets service object.
		Sheets sheets = new Sheets.Builder(HTTP_TRANSPORT, new JacksonFactory(), credentials).build();

		ValueRange response = sheets.spreadsheets().values()
				.get("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "TAGS!A2:C").execute();
//	} catch (GeneralSecurityException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
		System.out.println(response.getValues());
	}

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GoogleSheets.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		System.out.println(clientSecrets);
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				// .setDataStoreFactory(new FileDataStoreFactory(new
				// java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("me");
	}

//	public static List<List<Object>> getDados3(String spreadsheetId, String range) {
//		NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//		// Create a GoogleCredentials object.
//		GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
//
//		// Create a Sheets service object.
//		Sheets sheets = new Sheets.Builder(HTTP_TRANSPORT, new JacksonFactory(), credentials).build();
//
//		// Do something with the Sheets service object.
//		// For example, you can get a list of all the sheets in a spreadsheet.
//		Sheets.Spreadsheets.List request = sheets.spreadsheets().list(/* spreadsheetId */ "YOUR_SPREADSHEET_ID");
//		request.setFields("*");
//		Sheets.Spreadsheets.ListResponse response = request.execute();
//		System.out.println(response.getSheets());
//	}

	/**
	 * Prints the names and majors of students in a sample spreadsheet:
	 * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
	 * 
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static List<List<Object>> getDados(String spreadsheetId, String range) {
		ValueRange response = null;
		loadGSheetService();
		try {
			response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.getValues();
	}

	private static void loadGSheetService() {
		if (service == null) {
			try {
				NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
				service = new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
						.setApplicationName(APPLICATION_NAME).build();
			} catch (GeneralSecurityException | IOException e) {
				e.printStackTrace();
			} 
		}
	}
}