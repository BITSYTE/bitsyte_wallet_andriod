package com.wallet.bitsyte.bitsite_wallet.Connection;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.wallet.bitsyte.bitsite_wallet.Events.RetunDataEvent;
import com.wallet.bitsyte.bitsite_wallet.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by rafit on 30/08/2017.
 */

public class CommunicationDB {

    //private static final String URLSERVER = "https://www.hidoctor.mx/"; //pruebas
    private static final String URLSERVER = "https://wallet.mxcorp.net/"; //pruebas
    private static String URLusers = "";
    private static String URLcourses = "";
    private Context mContext;
    private String Route;
    Map<String, Object> parametros = new LinkedHashMap<>();
    private RetunDataEvent retunDataEvent;
    private SharedPreferences pref;
    private Dialog dialog;
    private boolean showDialog = true;

    private PrintWriter writer;
    private OutputStream outputStream;
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    FileInputStream fileInputStream = null;
    URL connectURL;
    String Title = "file";
    String Description = "file";


    public void OnReturnData(RetunDataEvent retunDataEvent) {
        this.retunDataEvent = retunDataEvent;
    }

    public CommunicationDB(Context context) {
        this.mContext = context;

        boundary = "===" + System.currentTimeMillis() + "===";
    }

    public void Login(String email, String password,String device_id,String version) {


        final JSONObject jO = new JSONObject();
        JSONObject jO2 = new JSONObject();
        try {
            jO.put("email",email);
            jO.put("password",password);

            jO2.put("device_id",device_id);
            jO2.put("type","andrioid");
            jO2.put("version",version);

            jO.put("device",jO2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // showDialog = false;

        new Thread(new Runnable() {
            public void run() {
               // sendHTTPData(URLSERVER+"api/v1/login",jO);
                try {
                    executeMultipartPost(URLSERVER+"api/v1/login",jO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



        //AsyncTaskRunnerpost runnerLoc = new AsyncTaskRunnerpost();
        //runnerLoc.execute("api/v1/login",jO+"");
    }
    public void Regiter(String email, String password) {
        parametros = new LinkedHashMap<>();
        parametros.put("usernameOrEmail", email);
        parametros.put("pass", password);

        JSONObject jO = new JSONObject();
        JSONObject jO2 = new JSONObject();
        try {
            jO.put("first_name","Pedro");
            jO.put("last_name","Luna");
            jO.put("email","pedro@mxcorp.net");
            jO.put("password","holamundo");
            jO.put("password_confirmation","holamundo");
            jO.put("password_confirmation","holamundo");

            jO2.put("device_id","HSQT90XSD12AS1OP");
            jO2.put("type","andrioid");
            jO2.put("version","8.0");

            jO.put("device",jO2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // showDialog = false;
        AsyncTaskRunnerpost runnerLoc = new AsyncTaskRunnerpost();
        runnerLoc.execute("api/v1/register",jO+"");
    }

    private class AsyncTaskRunnerpost extends AsyncTask<String, String, String> {

        private String resp;
        private String route1 = "";

        @Override
        protected String doInBackground(String... params) {

            HttpsURLConnection urlConnection;
            String url;
            String data = params[1];
            Log.e("data",data);

            String result = null;
            try {
                //Connect
                urlConnection = (HttpsURLConnection) ((new URL(URLSERVER+params[0]).openConnection()));

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //urlConnection.connect();


                urlConnection.setRequestMethod("POST");
                urlConnection.connect(); // Note the connect() here
                OutputStream os = urlConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(data.toString());
                osw.flush();
                osw.close();

                StringBuilder sb = null;
                String line = null;

                BufferedReader br = new BufferedReader(new InputStreamReader( urlConnection.getInputStream(),"utf-8"));
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                System.out.println(""+sb.toString());


                //Write
                OutputStream outputStream = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                writer.write(data);
                writer.close();
                outputStream.close();

                BufferedReader bufferedReader;
                //StringBuilder sb;

                Log.e("BASEDE DATOS", "URL "+urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();
                result = sb.toString();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e("BASEDE DATOS", "Error: "+e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("BASEDE DATOS", "Error: "+e);
            }

            return result;

        }

        @Override
        protected void onPostExecute(String resp) {
            try {
                Log.w("Respuesta", resp+"..");

                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent(resp, route1);
                }
            } catch (Exception e) {
                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent("{}", "error");
                }
                e.printStackTrace();
                Log.e("BASEDE DATOS", "Algo se rompio"+e);
            }

        }

        @Override
        protected void onPreExecute() {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    public String sendHTTPData(String urlpath, JSONObject json) {
        HttpsURLConnection connection = null;
        try {
            Log.e("urlpath",urlpath);
            URL url=new URL(urlpath);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(json.toString());
            streamWriter.flush();
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response + "\n");
                }
                bufferedReader.close();

                Log.d("test1", stringBuilder.toString());
                return stringBuilder.toString();
            } else {
                Log.e("test2", connection.getResponseMessage());
                return null;
            }
        } catch (Exception exception){
            Log.e("test3", exception.toString());
            return null;
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    Handler Eventhandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(android.os.Message msg) {
            try {

                if (msg.arg1 == 1) {
                    Toast.makeText(mContext,(String)msg.obj,
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    });

    public void executeMultipartPost(String url,JSONObject json) throws Exception {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] data = bos.toByteArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            //ByteArrayBody bab = new ByteArrayBody(data, getString(R.string.file_path));

            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

            //postRequest.addHeader("access_token", auth);


            //passes the results to a string builder/entity
            StringEntity se = new StringEntity(json.toString());

            //sets the post request as the resulting string
            postRequest.setEntity(se);

            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");


            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            JSONObject object=null;
            System.out.println("Response: " + s);
            try {
                object = new JSONObject(s+"");
                JSONObject json2 = object.getJSONObject("errors");

                android.os.Message msg = new android.os.Message();
                msg.arg1 = 1;
                msg.obj  = json2+"";
                Eventhandler.sendMessage(msg);



            }catch (Exception e){e.printStackTrace();}



        } catch (Exception e) {
            // handle exception here
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }



}
