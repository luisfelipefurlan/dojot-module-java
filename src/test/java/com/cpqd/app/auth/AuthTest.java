package com.cpqd.app.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cpqd.app.config.Config;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Unirest.class })
public class AuthTest {

	@Before
	public void setup() {
		PowerMockito.mockStatic(Unirest.class);
	}

	private void mockKeycloakAccessToken(JsonNode jsonNode) throws UnirestException {

		HttpRequestWithBody httpRequestWithBodyMock = mock(HttpRequestWithBody.class);
		MultipartBody multipartBodyMock = mock(MultipartBody.class);

		Config config = Config.getInstance();

		HttpResponse<JsonNode> httpResponseMock = mock(HttpResponse.class);
		when(Unirest.post(config.getKeycloakBasePath() + "/realms/master/protocol/openid-connect/token"))
				.thenReturn(httpRequestWithBodyMock);
		when(httpRequestWithBodyMock.field("username", config.getKeycloakUsername())).thenReturn(multipartBodyMock);
		when(multipartBodyMock.field("password", config.getKeycloakPassword())).thenReturn(multipartBodyMock);
		when(multipartBodyMock.field("client_id", config.getKeycloakClientId())).thenReturn(multipartBodyMock);
		when(multipartBodyMock.field("grant_type", config.getKeycloakGrantType())).thenReturn(multipartBodyMock);
		when(multipartBodyMock.asJson()).thenReturn(httpResponseMock);
		when(httpResponseMock.getBody()).thenReturn(jsonNode);
	}

	@Test
	public void shouldListTenantsFromKeycloak() throws UnirestException {

		// given

		JsonNode tokenJsonNode = new JsonNode(null);
		tokenJsonNode.getObject().put("access_token", "my_token");

		JsonNode tenantsJsonNode = new JsonNode("[]");
		tenantsJsonNode.getArray().put(new JSONObject().put("realm", "master").put("some_field", "some_value"));
		tenantsJsonNode.getArray().put(new JSONObject().put("realm", "my_realm").put("some_field", "some_value"));

		GetRequest getRequestMock = mock(GetRequest.class);

		HttpResponse<JsonNode> httpResponseMock = mock(HttpResponse.class);

		Config config = Config.getInstance();

		// when

		mockKeycloakAccessToken(tokenJsonNode);

		when(Unirest.get(config.getKeycloakBasePath() + "/admin/realms")).thenReturn(getRequestMock);
		when(getRequestMock.header("authorization", "Bearer " + tokenJsonNode.getObject().get("access_token")))
				.thenReturn(getRequestMock);
		when(getRequestMock.asJson()).thenReturn(httpResponseMock);
		when(httpResponseMock.getBody()).thenReturn(tenantsJsonNode);

		ArrayList<String> tenants = Auth.getInstance().getTenants();

		// then

		for (int i = 0; i < tenants.size(); i++) {
			assertEquals("The tenants list does not match with the expected",
					(String) tenantsJsonNode.getArray().getJSONObject(i).get("realm"), tenants.get(i));
		}

	}

	@Test
	public void shouldRetunAnEmptyListWhenKeycloakIsUnreachable() throws UnirestException {

		// given

		JsonNode tokenJsonNode = new JsonNode(null);
		tokenJsonNode.getObject().put("access_token", "my_token");

		JsonNode tenantsJsonNode = new JsonNode("[]");
		tenantsJsonNode.getArray().put(new JSONObject().put("realm", "master").put("some_field", "some_value"));
		tenantsJsonNode.getArray().put(new JSONObject().put("realm", "my_realm").put("some_field", "some_value"));

		GetRequest getRequestMock = mock(GetRequest.class);

		HttpResponse<JsonNode> httpResponseMock = mock(HttpResponse.class);

		Config config = Config.getInstance();

		// when

		mockKeycloakAccessToken(tokenJsonNode);

		when(Unirest.get(config.getKeycloakBasePath() + "/admin/realms")).thenReturn(getRequestMock);
		when(getRequestMock.header("authorization", "Bearer " + tokenJsonNode.getObject().get("access_token")))
				.thenReturn(getRequestMock);
		when(getRequestMock.asJson()).thenThrow(UnirestException.class);
		when(httpResponseMock.getBody()).thenReturn(tenantsJsonNode);

		ArrayList<String> tenants = Auth.getInstance().getTenants();

		// then

		assertTrue(tenants.isEmpty());

	}

}
