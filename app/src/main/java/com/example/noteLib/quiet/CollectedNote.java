package com.example.noteLib.quiet;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CollectedNote implements Parcelable {

        private String id;
        private String department;
        private String bookshelf;

        public CollectedNote(){}

        public CollectedNote(String id, String department, String bookshelf){
            this.id = id;
            this.department = department;
            this.bookshelf = bookshelf;
        }

    protected CollectedNote(Parcel in) {
        id = in.readString();
        department = in.readString();
        bookshelf = in.readString();
    }

    public static final Creator<CollectedNote> CREATOR = new Creator<CollectedNote>() {
        @Override
        public CollectedNote createFromParcel(Parcel in) {
            return new CollectedNote(in);
        }

        @Override
        public CollectedNote[] newArray(int size) {
            return new CollectedNote[size];
        }
    };

    public void setBookshelf(String bookshelf) {
        this.bookshelf = bookshelf;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookshelf() {
        return bookshelf;
    }

    public String getDepartment() {
        return department;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(department);
        parcel.writeString(bookshelf);
    }
}
