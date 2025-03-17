package com.example.smarthealth;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUrl {
//    public String readTheUrl(String placeURL) throws IOException {
//        String data = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpUrlConnection = null;
//        try {
//            URL url = new URL(placeURL);
//            httpUrlConnection = (HttpURLConnection) url.openConnection();
//
//            inputStream = httpUrlConnection.getInputStream();
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder stringBuffer = new StringBuilder();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//            data = stringBuffer.toString();
//            bufferedReader.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        } finally {
//            assert inputStream != null;
//            inputStream.close();
//            httpUrlConnection.disconnect();
//        }
//
//        return data;
//    }

    public String readTheUrl(String placeURL, String jsonBody) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            // Open a connection to the URL (POST request)
            URL url = new URL(placeURL);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            httpUrlConnection.setRequestProperty("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.location");
            httpUrlConnection.setDoOutput(true); // Allow writing to the connection

            // Write the request body (JSON body)
            OutputStream os = httpUrlConnection.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();
            os.close();

            // Read the response from the input stream
            inputStream = httpUrlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            // Log the response for debugging
            Log.d("API Response", data);

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
        }

        return data;
    }
}
