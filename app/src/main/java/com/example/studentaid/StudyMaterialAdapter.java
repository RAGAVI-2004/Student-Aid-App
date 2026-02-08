package com.example.studentaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter with expandable description feature
 */
public class StudyMaterialAdapter extends BaseAdapter {

    private Context context;
    private List<StudyMaterial> materialsList;

    public StudyMaterialAdapter(Context context, List<StudyMaterial> materialsList) {
        this.context = context;
        this.materialsList = materialsList;
    }

    @Override
    public int getCount() {
        return materialsList.size();
    }

    @Override
    public Object getItem(int position) {
        return materialsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_study_material, parent, false);
        }

        StudyMaterial material = materialsList.get(position);

        // Find all views
        TextView tvTitle = convertView.findViewById(R.id.tvMaterialTitle);
        TextView tvSubjectType = convertView.findViewById(R.id.tvSubjectType);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        TextView tvUploader = convertView.findViewById(R.id.tvUploader);
        TextView tvDate = convertView.findViewById(R.id.tvUploadDate);

        // Set title
        tvTitle.setText(material.getTitle());

        // Set subject and type
        tvSubjectType.setText(material.getSubject() + " • " + material.getType());

        // ✅ EXPANDABLE DESCRIPTION
        String description = material.getDescription();
        if (description != null && !description.isEmpty()) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);

            // ✅ TAP TO EXPAND/COLLAPSE
            tvDescription.setOnClickListener(v -> {
                if (tvDescription.getMaxLines() == 3) {
                    tvDescription.setMaxLines(Integer.MAX_VALUE);  // Expand
                } else {
                    tvDescription.setMaxLines(3);  // Collapse
                }
            });
        } else {
            tvDescription.setText("No description available");
            tvDescription.setVisibility(View.VISIBLE);
        }

        // Set uploader name
        String uploaderName = (material.getUser() != null)
                ? material.getUser().getName()
                : "Unknown";
        tvUploader.setText("📤 " + uploaderName);

        // Set upload date
        tvDate.setText("📅 " + material.getCreatedAt());

        return convertView;
    }
}
