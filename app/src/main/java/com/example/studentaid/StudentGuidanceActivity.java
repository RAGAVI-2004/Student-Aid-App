package com.example.studentaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Student-Teacher Guidance Chat with Voice Input & Text-to-Speech
 */
public class StudentGuidanceActivity extends AppCompatActivity {

    private static final String TAG = "StudentGuidance";

    private RecyclerView recyclerViewMessages;
    private EditText etMessage;
    private MaterialButton btnSend;
    private ImageButton btnMic, btnSpeaker;

    private DatabaseHelper dbHelper;
    private MessageAdapter messageAdapter;
    private List<GuidanceMessage> messageList;

    private int currentUserId;
    private String currentUserName;
    private String currentUserRole;

    private TextToSpeech textToSpeech;
    private boolean isTTSReady = false;

    // Voice input launcher
    private final ActivityResultLauncher<Intent> voiceInputLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> voiceResults = result.getData()
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (voiceResults != null && !voiceResults.isEmpty()) {
                        String voiceText = voiceResults.get(0);
                        etMessage.setText(voiceText);
                        Toast.makeText(this, "🎤 Voice captured", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_guidance);

        // Initialize views
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnMic = findViewById(R.id.btnMic);
        btnSpeaker = findViewById(R.id.btnSpeaker);

        dbHelper = new DatabaseHelper(this);
        messageList = new ArrayList<>();

        // ✅ Get current user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("StudentAidPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("loggedInUserId", -1);
        currentUserRole = prefs.getString("loggedInUserRole", "Student");
        currentUserName = dbHelper.getUserNameById(currentUserId);

        if (currentUserId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student-Teacher Guidance");
        }

        // Setup RecyclerView
        messageAdapter = new MessageAdapter(messageList, currentUserRole);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Load messages
        loadMessages();

        // Initialize TTS
        initializeTextToSpeech();

        // Button listeners
        btnSend.setOnClickListener(v -> sendMessage());
        btnMic.setOnClickListener(v -> startVoiceInput());
        btnSpeaker.setOnClickListener(v -> speakLastMessage());

        Log.d(TAG, "Guidance chat initialized for " + currentUserName + " (" + currentUserRole + ")");
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "TTS language not supported");
                } else {
                    isTTSReady = true;
                    Log.d(TAG, "TTS initialized");
                }
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "🎤 Speak your message...");

        try {
            voiceInputLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Voice input not available", e);
            Toast.makeText(this, "Voice input not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakLastMessage() {
        if (!isTTSReady) {
            Toast.makeText(this, "Text-to-Speech not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        if (messageList.isEmpty()) {
            Toast.makeText(this, "No messages to speak", Toast.LENGTH_SHORT).show();
            return;
        }

        GuidanceMessage lastMessage = messageList.get(messageList.size() - 1);
        String textToSpeak = lastMessage.getSenderName() + " says: " + lastMessage.getMessage();
        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ CORRECT: 4 parameters (senderId, senderName, senderRole, message)
        boolean success = dbHelper.insertGuidanceMessage(
                currentUserId,
                currentUserName,
                currentUserRole,
                message
        );

        if (success) {
            etMessage.setText("");
            loadMessages();
            recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getAllGuidanceMessages();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int msgId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MSG_ID));
                int senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SENDER_ID));
                String senderName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SENDER_NAME));
                String senderRole = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SENDER_ROLE));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MESSAGE));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TIMESTAMP));

                messageList.add(new GuidanceMessage(msgId, senderId, senderName,
                        senderRole, message, timestamp, null));

            } while (cursor.moveToNext());

            cursor.close();
        }

        messageAdapter.notifyDataSetChanged();
        Log.d(TAG, "Loaded " + messageList.size() + " messages");
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
