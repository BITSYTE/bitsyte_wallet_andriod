package com.wallet.bitsyte.bitsite_wallet.Connection;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.wallet.bitsyte.bitsite_wallet.Events.RetunDataEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by rafit on 30/08/2017.
 */

public class CommunicationDB {

    private static final String URLSERVER = "https://uxie.mxcorp.net/"; //pruebas
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

    public void Login(String email, String password) {
        parametros = new LinkedHashMap<>();
        parametros.put("usernameOrEmail", email);
        parametros.put("pass", password);
        // showDialog = false;
        AsyncTaskRunnerpost runnerLoc = new AsyncTaskRunnerpost();
        runnerLoc.execute("login");
    }

    

    private class AsyncTaskRunnerpost extends AsyncTask<String, String, String> {

        private String resp;
        private String route1 = "";

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection;
            String url;
            String data = params[1];
            String result = null;
            try {
                //Connect
                urlConnection = (HttpURLConnection) ((new URL(URLSERVER+params[0]).openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                //Write
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(data);
                writer.close();
                outputStream.close();

                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                result = sb.toString();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String resp) {
            try {
                Log.w("Respuesta", resp);

                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent(resp, route1);
                }
            } catch (Exception e) {
                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent("{}", "error");
                }
                Log.e("BASEDE DATOS", "Algo se rompio");
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

    private class AsyncTaskRunnerImage extends AsyncTask<String, String, String> {

        private String resp;
        private String route1 = "";

        @Override
        protected String doInBackground(String... params) {

            try {
                String iFileName = parametros.get("fileName") + "";
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                String Tag = "Imagen";
                try {
                    Log.e(Tag, "Starting Http File Sending to URL");

                    route1 = Route = params[0];
                    URL url = new URL(URLSERVER + Route);
                    // Open a HTTP connection to the URL
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    // Allow Inputs
                    conn.setDoInput(true);
                    // Allow Outputs
                    conn.setDoOutput(true);
                    // Don't use a cached copy.
                    conn.setUseCaches(false);
                    // Use a post method.
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Title);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Description);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + iFileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Log.e(Tag, "Headers are written");

                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStream.available();

                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file and write it into form...
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // close streams
                    fileInputStream.close();

                    dos.flush();
                    Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseCode()));
                    //->
                    //byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                    //conn.getOutputStream().write(postDataBytes);


                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line;
                    StringBuilder responce = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        responce.append(line);
                        resp = responce.toString();
                    }
                    in.close();

                    Log.e("IMAGEN", "THIS IS YOU RESP " + resp);

                    dos.close();
                } catch (Exception ex) {
                    Log.e(Tag, "URL error: " + ex.getMessage(), ex);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }


        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }


        @Override
        protected void onPostExecute(String resp) {
            try {
                Log.w("Respuesta", resp);

                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent(resp, route1);
                }
            } catch (Exception e) {
                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent("{}", "error");
                }
                Log.e("BASEDE DATOS", "Algo se rompio");
            }

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    private class AsyncTaskRunnerget extends AsyncTask<String, String, String> {

        private String resp;
        private String route1 = "";

        @Override
        protected String doInBackground(String... params) { // GET

            try {
                route1 = Route = params[0];
                URL mUrl = new URL(URLSERVER + Route);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");
                httpConnection.setUseCaches(false);
                httpConnection.setAllowUserInteraction(false);
                httpConnection.setConnectTimeout(10000);
                httpConnection.setReadTimeout(10000);

                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resp) { // GET

            if (route1.equals("getStatus")) {
                JSONObject jO = null;
                try {
                    jO = new JSONObject(resp);

                    URLusers = (new JSONObject(jO.getString("urls")).getString("users"));
                    URLcourses = (new JSONObject(jO.getString("urls")).getString("courses"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Log.e("Respuesta1", route1 + ":" + resp);

                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent(resp, route1);
                }
            } catch (Exception e) {
                if (retunDataEvent != null) {
                    retunDataEvent.onDataEvent("{}", "error");
                }
                Log.e("BASEDE DATOS", "Algo se rompio");
            }

        }

        @Override
        protected void onPreExecute() { // GET

        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSha1Hex(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

}
