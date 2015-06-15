package com.hmmelton.robinhood.utils;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by harrison on 6/14/15.
 */
public class RobinhoodApi {

    private final String TAG = "RobinhoodApi";

    private final AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    HashMap<String, String> endpoints = new HashMap<String, String>(){{
            put("login", "https://api.robinhood.com/api-token-auth/");
            put("investment_profile", "https://api.robinhood.com/user/investment_profile/");
            put("accounts", "https://api.robinhood.com/accounts/");
            put("ach_iav_auth", "https://api.robinhood.com/ach/iav/auth/");
            put("ach_relationships", "https://api.robinhood.com/ach/relationships/");
            put("ach_transfers", "https://api.robinhood.com/ach/transfers/");
            put("applications", "https://api.robinhood.com/applications/");
            put("dividends", "https://api.robinhood.com/dividends/");
            put("edocuments", "https://api.robinhood.com/documents/");
            put("instruments", "https://api.robinhood.com/instruments/");
            put("margin_upgrades", "https://api.robinhood.com/margin/upgrades/");
            put("markets", "https://api.robinhood.com/markets/");
            put("notifications", "https://api.robinhood.com/notifications/");
            put("orders", "https://api.robinhood.com/orders/");
            put("password_reset", "https://api.robinhood.com/password_reset/request/");
            put("quotes", "https://api.robinhood.com/quotes/");
            put("document_requests", "https://api.robinhood.com/upload/document_requests/");
            put("user", "https://api.robinhood.com/user/");
            put("watchlists", "https://api.robinhood.com/watchlists/");
    }};

    Context session;

    String username;
    String password;
    String auth_token;

    public RobinhoodApi(Context context, String username, String password) {
        session = context;
        this.username = username;
        this.password = password;
        putHeaders();
    }

    private void putHeaders() {
        client.addHeader("Accept", "*/*");
        client.addHeader("Accept-Encoding", "gzip, deflate");
        client.addHeader("Accept-Language", "en;q=1, fr;q=0.9, de;q=0.8, ja;q=0.7, nl;q=0.6, it;q=0.5");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        client.addHeader("X-Robinhood-API-Version", "1.0.0");
        client.addHeader("Connection", "keep-alive");
        client.addHeader("User-Agent", "Robinhood/823 (iPhone; iOS 7.1.2; Scale/2.00)");
    }

    public void login() {
        String data = String.format("password=%s&username=%s", password, username);
        try {
            HttpEntity entity = new StringEntity(data);
            client.post(session, endpoints.get("login"), entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, responseString, throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonRes = new JSONObject(responseString);
                        auth_token = jsonRes.getString("token");
                        client.addHeader("Authorization", "Token " + auth_token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void investmentProfile(final RobinhoodApiCallback<String> callback) {
        client.get(endpoints.get("investment_profile"), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.onSuccess(responseString);
            }
        });
    }

    public void instruments(String stock, final RobinhoodApiCallback<String> callback) {
        RequestParams params = new RequestParams();
        params.add("query", stock.toUpperCase(Locale.US));
        client.get(endpoints.get("instruments"), params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JSONObject jsonRes = new JSONObject();
                try {
                    String results = jsonRes.getString("results");
                    callback.onSuccess(results);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void quoteData(String stock, final RobinhoodApiCallback<String> callback) {
        RequestParams params = new RequestParams();
        params.add("symbols", stock.toUpperCase(Locale.US));
        client.get(endpoints.get("quotes"), params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JSONObject jsonRes = new JSONObject();
                try {
                    String results = jsonRes.getString("results");
                    callback.onSuccess(results);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void placeOrder(JSONObject instrument, int quantity, double bidPrice, ) {

    }

    public interface RobinhoodApiCallback<E> {
        void onSuccess(E result);

        void onFailure(Throwable throwable);
    }

}
