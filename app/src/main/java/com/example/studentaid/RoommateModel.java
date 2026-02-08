package com.example.studentaid;

public class RoommateModel {
    String name, grade, preferences;

    public RoommateModel(String name, String grade, String preferences) {
        this.name = name;
        this.grade = grade;
        this.preferences = preferences;
    }

    public String getName() { return name; }
    public String getGrade() { return grade; }
    public String getPreferences() { return preferences; }
}
