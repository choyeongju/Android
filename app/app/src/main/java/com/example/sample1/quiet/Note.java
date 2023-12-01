package com.example.noteLib.quiet;


import com.example.noteLib.noteshow.Note_show;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

public class Note implements Serializable {

    private String noteTitle;
    private String userNickname;
    private boolean isprivate;
    private String nameOfReadingroom;
    private String nameOfBookshelf;
    private String explainNote;
    private String NoteUri;
    private Date uploadDate;

    //pdf 추가 구현

    private  ArrayList<String> tagList;
//     private static String tagProfessor;
//     private static String tagSubject;

    public Note(){}

    public Note(String noteTitle, String userNickname, boolean isprivate, @Nullable ArrayList<String> tagList, String nameOfReadingroom, String nameOfBookshelf, String explainNote, String NoteUri)
    {
        if (tagList != null)
            setTagList(tagList);
        setNoteTitle(noteTitle);
        setUserNickname(userNickname);
        setIsPublic(isprivate);
        setNameOfReadingroom(nameOfReadingroom);
        setNameOfBookshelf(nameOfBookshelf);
        setExplainNote(explainNote);
        setUploadDate(uploadDate);
        setNoteUri(NoteUri);
    }
    public void setNoteTitle(String noteTitle){this.noteTitle = noteTitle;}

    public void setUserNickname(String userNickname){this.userNickname = userNickname;}
    public void setTagList(ArrayList<String> tagList){this.tagList = tagList;}
    public void setIsPublic(boolean isPublic){this.isprivate = isprivate;}
    public void setNameOfReadingroom(String nameOfReadingroom){this.nameOfReadingroom = nameOfReadingroom;}
    public void setNameOfBookshelf(String nameOfBookshelf){this.nameOfBookshelf = nameOfBookshelf;}
    public void setExplainNote(String explainNote){this.explainNote = explainNote;}
    public void setUploadDate(Date uploadDate){this.uploadDate = uploadDate;}
    public void setNoteUri(String NoteUri){this.NoteUri = NoteUri;}


    public String getNoteTitle(){return noteTitle;}
    public String getUserNickname(){return userNickname;}
    public ArrayList<String> getTagList(){return tagList;}
    public boolean getIsPrivate(){return isprivate;}
    public String getNameOfReadingroom(){return nameOfReadingroom;}
    public String getNameOfBookshelf(){return nameOfBookshelf;}
    public String getExplainNote(){return explainNote;}
    public Date getUploadDate(){return uploadDate;}
    public String getNoteUri(){return NoteUri;}
}



