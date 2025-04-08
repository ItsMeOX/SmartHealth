package com.example.smarthealth.Camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;
import com.example.smarthealth.chatbot.ChatBot;
import com.example.smarthealth.chatbot.Message;
import com.example.smarthealth.chatbot.MessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatBotFragmentTest extends Fragment {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    View view;

    ChatBot chatBot = new ChatBot();

    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chatbot_fragment, container, false);

        messageList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
        welcomeTextView = view.findViewById(R.id.welcome_text);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_btn);


        // setup recycle view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener(v -> {
            String userMessage = messageEditText.getText().toString();
            // Toast.makeText(this, userMessage, Toast.LENGTH_SHORT).show();
            addToChat(userMessage, Message.SENT_BY_ME);

            // Clear Message after sending
            messageEditText.setText("");
            callAPI(userMessage);
            welcomeTextView.setVisibility(View.GONE);


        });
        return view;

    }

    // UI for the Chat
    void addToChat(String message, String sentBy) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });

    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT);
    }


    // Call API Method for chatBot Implementation,
    // this does not have Chatbot Memory and the system Implementation
    void callAPI(String question) {
        //okhttp

        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));
        chatBot.addMessage("user", question);

        JSONObject Jsonprompt = chatBot.getPrompt();
        RequestBody body = RequestBody.create(Jsonprompt.toString(), JSON);
        Request request = new Request.Builder()
                .url(chatBot.getAPI_URL())
                .header("Authorization", "Bearer " + chatBot.getAPI_Key())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        // Parse the JSON response
                        jsonObject = new JSONObject(response.body().string());

                        // Get the "choices" array
                        JSONArray choicesArray = jsonObject.getJSONArray("choices");

                        // Extract the content from the "message" object inside the first choice
                        String result = choicesArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        // Trim the result if needed and add it to the conversation history
                        chatBot.addMessage("assistant", result.trim());

                        // Optionally, you can also update the UI or perform other actions with the result
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{

                    addResponse("Failed to load " + response.body().string());
                }
            }
        });
    }








}
