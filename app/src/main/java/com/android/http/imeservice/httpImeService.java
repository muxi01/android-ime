package com.android.http.imeservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import fi.iki.elonen.NanoHTTPD;

public class httpImeService extends Service {

    final  String TAG ="httpImeService";
    private httpServer https =null;
    @Override
    public void onCreate() {
        https =new httpServer("0.0.0.0",9600);
        super.onCreate();
        Log.d(TAG,"  ----->  onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class httpServer extends NanoHTTPD {

        private Map<String, Object> objUriStore_ = new HashMap<>();

        public httpServer(String hostname, int port)
        {
            super(hostname,port);
            try
            {
                this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
            }catch (Exception e)
            {
                Log.d(TAG,"new httpAgent failed  "+e);
            }
        }

        private JSONObject buildResponse(String status)
        {
            JSONObject ret = new JSONObject();
            try {
                ret.put("status", status);
            } catch (JSONException e) {
                // 不太可能会进这里
                e.printStackTrace();
            }
            return ret;
        }

        public JSONObject onRequest(JSONObject req)
        {
            String context ="";
            JSONObject resp =new JSONObject();
            try {
                context =req.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(context.length() > 0)
            {
                Intent intent =new Intent();
                intent.setAction(Utf7ImeService.IME_MESSAGE);
                intent.putExtra("msg",context);
                sendBroadcast(intent);
                return buildResponse("success");
            }
            return buildResponse("no context");
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
            Log.d(TAG,"req = "+ req.toString());
            JSONObject resp = this.onRequest(req);
            if (resp != null) {
                return newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8", resp.toString());
            } else {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "server internal error. response is null");
            }
        }
    }
}