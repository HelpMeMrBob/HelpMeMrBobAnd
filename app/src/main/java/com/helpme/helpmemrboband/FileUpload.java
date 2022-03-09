package com.helpme.helpmemrboband;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileUpload {

    private static final String TAG = "Kosmo123";

    private final String boundary;
    private final String tail;
    private static final String LINE_END = "\r\n";
    private static final String TWOHYPEN = "--";
    private HttpURLConnection httpConn;
    private String charset;
    private PrintWriter writer;
    private OutputStream outputStream;
    int maxBufferSize = 1024;

    public FileUpload(String requestURL, String charset) throws IOException {
        this.charset = charset;

        boundary = "===" + System.currentTimeMillis() + "===";
        tail = LINE_END + TWOHYPEN + boundary + TWOHYPEN + LINE_END;
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    public JSONArray upload(HashMap<String, String> params, HashMap<String, String> files)
            throws IOException
    {
        String paramsPart = "";
        String fileHeader = "";
        String filePart = "";
        long fileLength = 0;

        ArrayList<String> paramHeaders = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {

            String param = TWOHYPEN + boundary + LINE_END
                    + "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END
                    + "Content-Type: text/plain; charset=" + charset + LINE_END
                    + LINE_END
                    + entry.getValue() + LINE_END;
            paramsPart += param;
            paramHeaders.add(param);
        }

        ArrayList<File> filesAL = new ArrayList<>();
        ArrayList<String> fileHeaders = new ArrayList<>();

        for (Map.Entry<String, String> entry : files.entrySet()) {

            File file = new File(entry.getValue());
            fileHeader = TWOHYPEN + boundary + LINE_END
                    + "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + file.getName() + "\"" + LINE_END
                    + "Content-Type: " + URLConnection.guessContentTypeFromName(file.getAbsolutePath()) + LINE_END
                    + "Content-Transfer-Encoding: binary" + LINE_END
                    + LINE_END;
            fileLength += file.length() + LINE_END.getBytes(charset).length;
            filePart += fileHeader;

            fileHeaders.add(fileHeader);
            filesAL.add(file);
        }
        String partData = paramsPart + filePart;

        long requestLength = partData.getBytes(charset).length + fileLength + tail.getBytes(charset).length;
        httpConn.setRequestProperty("Content-length", "" + requestLength);
        httpConn.setFixedLengthStreamingMode((int) requestLength);
        httpConn.connect();

        outputStream = new BufferedOutputStream(httpConn.getOutputStream());
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

        for (int i = 0; i < paramHeaders.size(); i++) {
            writer.append(paramHeaders.get(i));
            writer.flush();
        }

//    	int totalRead = 0;
        int bytesRead;
        byte buf[] = new byte[maxBufferSize];
        for (int i = 0; i < filesAL.size(); i++) {
            writer.append(fileHeaders.get(i));
            writer.flush();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filesAL.get(i)));
            while ((bytesRead = bufferedInputStream.read(buf)) != -1) {

                outputStream.write(buf, 0, bytesRead);
                writer.flush();
//            	totalRead += bytesRead;
//            	if (progressListener != null) {
//                	float progress = (totalRead / (float) requestLength) * 100;
//                	progressListener.onProgressUpdate((int) progress);
//            	}
            }
            outputStream.write(LINE_END.getBytes());
            outputStream.flush();
            bufferedInputStream.close();
        }
        writer.append(tail);
        writer.flush();
        writer.close();

        JSONObject jObj = null;
        JSONArray jArr = null;
        StringBuilder sb = new StringBuilder();
        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"), 8);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " +
                    status + " " +
                    httpConn.getResponseMessage());
        }

        try {
            jArr = new JSONArray(sb.toString());
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        return jArr;
    }

}
