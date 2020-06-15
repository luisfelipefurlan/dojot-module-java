package com.cpqd.app.auth;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import com.cpqd.app.config.Config;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class KeycloakClient extends TenantsOwner {

	private static KeycloakClient mInstance;

	private KeycloakClient() {
	}

	public static synchronized KeycloakClient getInstance() {

		if (mInstance == null) {
			mInstance = new KeycloakClient();
		}
		return mInstance;
	}

	/**
	 * Get an acces_token from keycloak
	 * 
	 * @throws UnirestException
	 */
	public String getAccessToken() throws UnirestException {

		Config config = Config.getInstance();

		HttpResponse<JsonNode> response = Unirest
				.post(config.getKeycloakBasePath() + "/realms/master/protocol/openid-connect/token")
				.field("username", config.getKeycloakUsername()).field("password", config.getKeycloakPassword())
				.field("client_id", config.getKeycloakClientId()).field("grant_type", config.getKeycloakGrantType())
				.asJson();
		return (String) response.getBody().getObject().get("access_token");

	}

	@Override
	public ArrayList<String> getTenants() {
		StringBuffer url = new StringBuffer(Config.getInstance().getKeycloakBasePath());
		url.append("/admin/realms");
		ArrayList<String> resTenants = new ArrayList<>();
		try {
			HttpResponse<JsonNode> request = Unirest.get(url.toString())
					.header("authorization", "Bearer " + getAccessToken()).asJson();
			JSONArray jsonArrayResponse = request.getBody().getArray();

			for (int i = 0; i < jsonArrayResponse.length(); i++) {
				resTenants.add((String) (jsonArrayResponse.getJSONObject(i)).get("realm"));
			}

		} catch (UnirestException exception) {
			return resTenants;
		} catch (JSONException exception) {
			return resTenants;
		}
		System.out.println("resTenants:::: " + resTenants);
		return resTenants;
	}

}
