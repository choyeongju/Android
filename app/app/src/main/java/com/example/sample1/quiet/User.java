package com.example.noteLib.quiet;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String ID;
    private String PW;
    private String PW_hint;
    private String PW_hint_input;
    private String department;
    private String nickname;
    private ArrayList<String> favorites = null;

    public User(){}
    public User(String ID, String PW, String PW_hint, String PW_hint_input, String department, String nickname, ArrayList<String> favorites)
    {
        this.department = department;
        this.nickname = nickname;
        this.ID = ID;
        this.PW = PW;
        this.PW_hint = PW_hint;
        this.PW_hint_input = PW_hint_input;
        this.favorites = favorites;
    }
    public String getDepartment() {return department;}
    public String getNickname() {return nickname;}
    public String getID() {return ID;}
    public String getPW() {return PW;}
    public String getPW_hint() {return PW_hint;}
    public String getPW_hint_input() {return PW_hint_input;}
    public ArrayList<String> getFavorites(){return favorites;}

    public void setDepartment(String department) {this.department = department;}
    public void setNickname(String nickname) {this.nickname = nickname;}
    public void setID(String ID) {this.ID = ID;}
    public void setPW(String PW) {this.PW = PW;}
    public void setPW_hint(String PW_hint) {this.PW_hint = PW_hint;}
    public void setPW_hint_input(String PW_hint_input) {this.PW_hint_input = PW_hint_input;}
    public void setFavorites(ArrayList<String> favorites) {this.favorites = favorites;}
}

