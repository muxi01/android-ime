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

    public  static final  String HTTP_MSG_ACTION_TYPE ="com.android.http.imeservice.HttpMsg";
    final  String TAG ="httpImeService";
    private httpAgent https =null;
    @Override
    public void onCreate() {
        try
        {
            https =new httpAgent("0.0.0.0",9600);
        }catch (Exception e)
        {
            https.stop();
            Log.d(TAG,"new httpAgent failed");
        }
        super.onCreate();
        Log.d(TAG,"  ----->  onCreate");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.d(TAG,"  ----->  onStartCommand");
        if(https != null)
        {
            try
            {
                https.start();
            }catch (Exception e)
            {
                Log.d(TAG,"http service launch failed");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(https != null)
        {
            https.stop();
            https=null;
        }
        Log.d(TAG,"  ----->  onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class httpAgent extends NanoHTTPD {

        private Map<String, Object> objUriStore_ = new HashMap<>();

        public httpAgent(String hostname, int port) throws IOException {
            super(hostname, port);
            this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
        }

        public JSONObject onRequest(JSONObject req)
        {
            Intent intent =new Intent();
            intent.setAction(HTTP_MSG_ACTION_TYPE);
            intent.putExtra("msg","来自http的消息");
            sendBroadcast(intent);
            return null;
        }
        @Override
        public Response serve(IHTTPSession session) {
            JSONObject req = null;
            this.onRequest(req);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "get body failed");
//            Map<String, String> body = new HashMap<>();
//            try {
//                session.parseBody(body);
//            } catch (IOException|ResponseException e) {
//                e.printStackTrace();
//                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "get body failed");
//            }
//            JSONObject req = null;
//            try {
//                req = new JSONObject(body.get("postData"));
//            } catch (JSONException e) {
//                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "get post data failed");
//            }
//            System.out.println(req.toString());
//            JSONObject resp = this.onRequest(req);
//            if (resp != null) {
//                return newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8", resp.toString());
//            } else {
//                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "server internal error. response is null");
//            }
        }
    }
}