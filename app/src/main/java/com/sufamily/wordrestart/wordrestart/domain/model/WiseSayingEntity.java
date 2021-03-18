package com.sufamily.wordrestart.wordrestart.domain.model;

/**
 * Created by N1245 on 2015-03-16.
 */
public class WiseSayingEntity {
    private int wise_seq;
    private String sentence;
    private String speaker;

    public int getWise_seq() {
        return wise_seq;
    }

    public void setWise_seq(int wise_seq) {
        this.wise_seq = wise_seq;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getFullSentence() {
        return (this.sentence
                + "\n" + this.speaker + " (" + "출처-휴넷,행복한 경영이야기" + ")");
    }
}
