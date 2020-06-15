package com.cpqd.app.auth;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.cpqd.app.config.Config;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Auth extends TenantsOwner {

    private static Auth mInstance;

    private Auth() {}

    public static synchronized Auth getInstance() {
        if (mInstance == null) {
            mInstance = new Auth();
        }
        return mInstance;
    }

    @Override
    public ArrayList<String> getTenants(){
        StringBuffer url = new StringBuffer(Config.getInstance().getAuthAddress());
        url.append("/admin/tenants");
        ArrayList<String> resTenants = new ArrayList<>();
        try {
            HttpResponse<JsonNode> request = Unirest.get(url.toString()).header("authorization","Bearer " + Auth.getInstance().getToken(Config.getInstance().getInternalTenant())).asJson();
            JSONArray jsonArrayResponse = request.getBody().getObject().getJSONArray("tenants");
            for(int i = 0;i < jsonArrayResponse.length();i++) {
                resTenants.add(jsonArrayResponse.get(i).toString());
            }
        } catch(UnirestException exception) {
                return resTenants;
        } catch (JSONException exception){
                return resTenants;
        }
        System.out.println("resTenants:::: " + resTenants);
        return resTenants;
    }
}
