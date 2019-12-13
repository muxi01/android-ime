package com.android.http.imeservice;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by xialixin on 2019/12/13.
 */

public class httpImeService extends NanoHTTPD {

    private Map<String, Object> objUriStore_ = new HashMap<>();

    public httpImeService(String hostname, int port) throws IOException {
        super(hostname, port);
        this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
    }

    public JSONObject onRequest(JSONObject req)
    {
        return null;
    }
    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> body = new HashMap<>();
        try {
            session.parseBody(body);
        } catch (IOException|ResponseException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "get body failed");
        }
        JSONObject req = null;
        try {
            req = new JSONObject(body.get("postData"));
        } catch (JSONException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "get post data failed");
        }
        System.out.println(req.toString());
        JSONObject resp = this.onRequest(req);
        if (resp != null) {
            return newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8", resp.toString());
        } else {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "server internal error. response is null");
        }
    }
}
