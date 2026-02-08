package com.example.studentaid;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying chat messages with WhatsApp-style differentiation
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<GuidanceMessage> messages;
    private String currentUserRole;

    public MessageAdapter(List<GuidanceMessage> messages, String currentUserRole) {
        this.messages = messages;
        this.currentUserRole = currentUserRole;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        GuidanceMessage message = messages.get(position);
        String senderRole = message.getSenderRole();
        boolean isMyMessage = senderRole.equals(currentUserRole);

        // Set message text
        holder.tvMessage.setText(message.getMessage());

        // Set timestamp
        holder.tvTimestamp.setText(formatTimestamp(message.getTimestamp()));

        // ✅ FIX: USE CORRECT LAYOUT PARAMS - FrameLayout.LayoutParams
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.messageCard.getLayoutParams();

        // TEACHER MESSAGES = LEFT SIDE, BLUE
        if (senderRole.equals("Teacher")) {
            // Align to LEFT
            params.gravity = Gravity.START;
            holder.messageCard.setLayoutParams(params);

            // BLUE background and text
            holder.messageCard.setCardBackgroundColor(Color.parseColor("#E3F2FD")); // Light Blue
            holder.tvSenderName.setText(message.getSenderName() + " (Teacher)");
            holder.tvSenderName.setTextColor(Color.parseColor("#0D47A1")); // Dark Blue
            holder.tvMessage.setTextColor(Color.parseColor("#1565C0")); // Blue
            holder.tvTimestamp.setTextColor(Color.parseColor("#1976D2")); // Blue

            // STUDENT MESSAGES = RIGHT SIDE, GREEN
        } else {
            // Align to RIGHT
            params.gravity = Gravity.END;
            holder.messageCard.setLayoutParams(params);

            // GREEN background and text
            holder.messageCard.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // Light Green
            holder.tvSenderName.setText(message.getSenderName() + " (Student)");
            holder.tvSenderName.setTextColor(Color.parseColor("#1B5E20")); // Dark Green
            holder.tvMessage.setTextColor(Color.parseColor("#2E7D32")); // Green
            holder.tvTimestamp.setTextColor(Color.parseColor("#388E3C")); // Green
        }

        // HIGHLIGHT "YOU" FOR OWN MESSAGES
        if (isMyMessage) {
            holder.tvSenderName.setText("You (" + senderRole + ")");
            holder.tvSenderName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.tvSenderName.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Format timestamp to show only time (HH:mm)
     */
    private String formatTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "";
        }
        try {
            String[] parts = timestamp.split(" ");
            if (parts.length > 1) {
                String time = parts[1];
                return time.substring(0, 5); // Show HH:mm only
            }
        } catch (Exception e) {
            return timestamp;
        }
        return timestamp;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView messageCard;
        TextView tvSenderName;
        TextView tvMessage;
        TextView tvTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageCard = itemView.findViewById(R.id.messageCard);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
