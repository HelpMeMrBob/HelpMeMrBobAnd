package com.helpme.helpmemrboband;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpClient {
    private int httpStatusCode; //HTTP 응답 상태 코드. (200, 401, 식별 불가능 -1)
    private String body;
    private static final String TAG = "SON";

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getBody() {
        return body;
    }

    private Builder builder;

    private void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public void request() {
        HttpURLConnection con = getConnection();
        setHeader(con);
        setBody(con);

        httpStatusCode = getStatusCode(con);
        body = readStream(con);

        con.disconnect();
    }

    private HttpURLConnection getConnection() {
        try {
            URL url = new URL(builder.getUrl());
            return (HttpURLConnection)url.openConnection();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setHeader(HttpURLConnection con) {
        setContentType(con);
        setRequestMethod(con);

        con.setConnectTimeout(30 * 1000);
        con.setReadTimeout(15 * 1000);
        con.setDoOutput(true);
        con.setDoInput(true);
    }

    private void setContentType(HttpURLConnection con) {
        con.setRequestProperty("content-Type", "application/x-www-form-urlencoded");
    }

    private void setRequestMethod(HttpURLConnection con) {
        try {
            con.setRequestMethod(builder.getMethod());
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    private void setBody(HttpURLConnection con) {
        String parameter = builder.getParameter();

        if (parameter != null && parameter.length() > 0) {
            OutputStream outputStream = null;
            try {
                outputStream = con.getOutputStream();
                outputStream.write(parameter.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getStatusCode(HttpURLConnection con) {
        try {
            return con.getResponseCode();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String readStream(HttpURLConnection con) {
        String result = "";
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static class Builder {
        private Map<String, String> parameters;
        private String method;
        private String url;

        public String getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }

        public Builder(String method, String url) {
            if (method == null) {
                method = "GET";
            }
            this.method = method;
            this.url = url;
            this.parameters = new HashMap<>();
        }

        public void addOrReplace(String key, String value) {
            parameters.put(key, value);
        }

        public void addAllParameters(Map<String, String> param) {
            parameters.putAll(param);
        }

        public String getParameter() {
            return generateParameters();
        }

        public String getParameter(String key) {
            return this.parameters.get(key);
        }

        private String generateParameters() {
            StringBuffer urlParameters = new StringBuffer();

            Iterator keys = getKeys();

            String key = "";
            while (keys.hasNext()) {
                key = (String)keys.next();
                urlParameters.append(String.format("%s=%s", key, parameters.get(key)));
                urlParameters.append("&");
            }
            String params = urlParameters.toString();
            if (params.length() > 0) {
                params = params.substring(0, params.length() - 1);
            }
            return params;
        }

        private Iterator getKeys() {
            return this.parameters.keySet().iterator();
        }

        public HttpClient create() {
            HttpClient client = new HttpClient();
            client.setBuilder(this);
            return client;
        }
    }
}
