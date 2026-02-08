package com.example.studentaid;

public class RoommateRequest {
    public String name, gender, studentClass, room, email, desc;
    public int members;

    public RoommateRequest(String name, String gender, String studentClass,
                           String room, int members, String email, String desc) {
        this.name = name;
        this.gender = gender;
        this.studentClass = studentClass;
        this.room = room;
        this.members = members;
        this.email = email;
        this.desc = desc;
    }
}
