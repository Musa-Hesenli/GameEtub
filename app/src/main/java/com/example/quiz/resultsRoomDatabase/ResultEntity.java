package com.example.quiz.resultsRoomDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "results")
public class ResultEntity {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "quiz_name")
    private String quiz_name;

    @ColumnInfo(name = "right_answers")
    private String right_answers;

    @ColumnInfo(name = "wrong_answers")
    private String wrong_answers;

    @ColumnInfo(name = "total_point")
    private String total_point;

    public String getQuiz_name() {
        return quiz_name;
    }

    public String getRight_answers() {
        return right_answers;
    }

    public String getWrong_answers() {
        return wrong_answers;
    }

    public String getTotal_point() {
        return total_point;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuiz_name(String quiz_name) {
        this.quiz_name = quiz_name;
    }

    public void setRight_answers(String right_answers) {
        this.right_answers = right_answers;
    }

    public void setWrong_answers(String wrong_answers) {
        this.wrong_answers = wrong_answers;
    }

    public void setTotal_point(String total_point) {
        this.total_point = total_point;
    }
}
