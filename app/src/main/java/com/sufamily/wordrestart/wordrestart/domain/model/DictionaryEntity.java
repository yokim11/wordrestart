package com.sufamily.wordrestart.wordrestart.domain.model;

/**
 * Created by N1245 on 2015-03-06.
 */
public class DictionaryEntity {
    private int word_seq;
    private String level;
    private String word;
    private String meaning;

    public int getWord_seq() {
        return word_seq;
    }

    public void setWord_seq(int word_seq) {
        this.word_seq = word_seq;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
