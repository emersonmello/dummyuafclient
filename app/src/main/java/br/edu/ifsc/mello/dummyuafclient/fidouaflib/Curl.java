/*
 * Copyright 2015 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.edu.ifsc.mello.dummyuafclient.fidouaflib;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;


public class Curl {

    private static final int CONNECTION_TIMEOUT = 20000;
    private static final int READ_TIMEOUT = 20000;
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE = "application/json";
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";

    public static String getInSeparateThread(String url) {
        GetAsyncTask async = new GetAsyncTask();
        async.execute(url);
        while (!async.isDone()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return async.getResult();
    }

    public static String postInSeparateThread(String url, String header, String data) {
        PostAsyncTask async = new PostAsyncTask();
        async.execute(url, header, data);
        while (!async.isDone()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return async.getResult();
    }

    public static HttpResponse get(String url) {

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createConnection(url, GET_METHOD, false);

            int httpResult = urlConnection.getResponseCode();
            String response;
            if (httpResult == HttpURLConnection.HTTP_CREATED || httpResult == HttpURLConnection.HTTP_OK) {
                response = readStream(urlConnection.getInputStream());
            } else {
                response = readStream(urlConnection.getErrorStream());
            }
            return new HttpResponse(response, httpResult);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Log.d("CURL", "Security error initialising HTTPS connection");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CURL", "Unable to connect to the server");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return new HttpResponse("", 404);
    }

    protected static HttpResponse post(String url, String header, String payload) {

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createConnection(url, POST_METHOD, true);
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8");
            out.write(payload);
            out.close();

            int httpResult = urlConnection.getResponseCode();
            String response;
            if (httpResult == HttpURLConnection.HTTP_CREATED || httpResult == HttpURLConnection.HTTP_OK) {
                response = readStream(urlConnection.getInputStream());
            } else {
                response = readStream(urlConnection.getErrorStream());
            }

            return new HttpResponse(response, httpResult);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Log.d("CURL", "Security error initialising HTTPS connection");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CURL", "Unable to connect to the server");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return new HttpResponse("", 404);
    }


    protected static HttpURLConnection createConnection(String fullUrl, String method, boolean output) throws
            IOException, KeyManagementException, NoSuchAlgorithmException {
        URL url = new URL(fullUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(output);
        urlConnection.setRequestMethod(method);
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        urlConnection.connect();
        return urlConnection;
    }

    protected static String readStream(InputStream stream) throws IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    public static class HttpResponse {
        private final String payload;
        private final int httpStatusCode;

        public HttpResponse(String payload, int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            this.payload = payload;
        }

        public String getPayload() {
            return payload;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }
    }

}

class GetAsyncTask extends AsyncTask<String, Integer, String> {

    private String result = null;
    private boolean done = false;

    public boolean isDone() {
        return done;
    }

    public String getResult() {
        return result;
    }

    @Override
    protected String doInBackground(String... args) {
        result = Curl.get(args[0]).getPayload();
        done = true;
        return result;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        this.result = result;
        done = true;
    }
}

class PostAsyncTask extends AsyncTask<String, Integer, String> {

    private String result = null;
    private boolean done = false;

    public boolean isDone() {
        return done;
    }

    public String getResult() {
        return result;
    }

    @Override
    protected String doInBackground(String... args) {
        result = Curl.post(args[0], args[1], args[2]).getPayload();//(url, header, data)
        done = true;
        return result;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onPostExecute(String result) {
        this.result = result;
        done = true;
    }
}
