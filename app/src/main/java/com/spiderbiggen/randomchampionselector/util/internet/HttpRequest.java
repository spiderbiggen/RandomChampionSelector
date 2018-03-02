package com.spiderbiggen.randomchampionselector.util.internet;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequest implements Closeable {

    private static final String TAG = HttpRequest.class.getSimpleName();
    private HttpURLConnection connection;
    private HttpRequestMode requestMode = HttpRequestMode.GET;

    public HttpRequest(String url) throws IOException {
        this(new URL(url));
    }

    public HttpRequest(URL url) throws IOException {
        Log.d(TAG, "HttpRequest: " + url);
        connection = (HttpURLConnection) url.openConnection();
    }

    /**
     * Sets requestMode.
     *
     * @param requestMode the new value of requestMode
     * @return current request
     */
    public HttpRequest setRequestMode(HttpRequestMode requestMode) {
        this.requestMode = requestMode;
        return this;
    }

    public HttpRequest connect() throws IOException {
        connection.setRequestMethod(requestMode.name());
        return this;
    }

    public int getResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    public InputStream getResponseStream() throws IOException {
        connection.connect();
        // Read the input stream into a String
        return connection.getInputStream();
    }

    public String getResponse() throws IOException {
        StringBuilder buffer = new StringBuilder();
        try (InputStream inputStream = getResponseStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        }
        return buffer.length() == 0 ? null : buffer.toString();
    }

    public <T> T getResponse(Gson gson, Type t) throws IOException {
        try (InputStream stream = getResponseStream();
             InputStreamReader reader = new InputStreamReader(stream)) {
            return gson.fromJson(reader, t);
        }
    }

    public HttpRequest sendJson(JSONObject object) throws IOException {
        connection.setDoOutput(true);
        byte[] bytes = object.toString().getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        // Send request
        try (OutputStream raw = connection.getOutputStream();
             DataOutputStream writer = new DataOutputStream(raw)) {
            writer.write(bytes);
        }
        return this;
    }

    @Override
    public void close() {
        connection.disconnect();
    }

    public enum HttpRequestMode {
        GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH
    }
}
