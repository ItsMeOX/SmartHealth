package com.example.smarthealth;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadUrl {
    public String readTheUrl(String placeURL, String jsonBody, String fieldMask, boolean hasFieldMask) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpUrlConnection = null;
        Log.d("here", String.valueOf(hasFieldMask));

        try {
            Log.d("here2", "here2");
            // Open a connection to the URL (POST request)
            URL url = new URL(placeURL);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            Log.d("here3", "here3");
            httpUrlConnection.setRequestMethod("POST");
            Log.d("here4", "here4");
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            Log.d("here5", "here5");
            if (hasFieldMask)
            {
                Log.d("here6",fieldMask);
                //"places.displayName,places.formattedAddress,places.location"
                httpUrlConnection.setRequestProperty("X-Goog-FieldMask", fieldMask);
            }
            Log.d("here7", "here7");
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
