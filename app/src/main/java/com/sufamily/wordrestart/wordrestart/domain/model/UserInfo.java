package com.sufamily.wordrestart.wordrestart.domain.model;

/**
 * Created by N1245 on 2015-03-10.
 */
public class UserInfo {
    private String nickname;                // nickname
    private String level;                   // 현재 진행중인 level
    private int flashcardCountOfDay;        // 새 단어 답기할 단어수
    private int flashcardFrequence;         // flashcard refresh 주기
    private int flashcardSoundType;         // flashcard tts 소리 노출 타입
    private int testcardFrequence;          // testcard refresh 주기

    private String inputDictionaryDate;     // 담기한 날짜 (YYYY-MM-DD)
    private int inputDictionaryLastSeq;     // 새 단어 담기할 wordSeq

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getFlashcardCountOfDay() {
        return flashcardCountOfDay;
    }

    public void setFlashcardCountOfDay(int flashcardCountOfDay) {
        this.flashcardCountOfDay = flashcardCountOfDay;
    }

    public int getFlashcardFrequence() {
        return flashcardFrequence;
    }

    public void setFlashcardFrequence(int flashcardFrequence) {
        this.flashcardFrequence = flashcardFrequence;
    }

    public int getFlashcardSoundType() {
        return flashcardSoundType;
    }

    public void setFlashcardSoundType(int flashcardSoundType) {
        this.flashcardSoundType = flashcardSoundType;
    }

    public String getInputDictionaryDate() {
        return inputDictionaryDate;
    }

    public void setInputDictionaryDate(String inputDictionaryDate) {
        this.inputDictionaryDate = inputDictionaryDate;
    }

    public int getInputDictionaryLastSeq() {
        return inputDictionaryLastSeq;
    }

    public void setInputDictionaryLastSeq(int inputDictionaryLastSeq) {
        this.inputDictionaryLastSeq = inputDictionaryLastSeq;
    }

    public int getTestcardFrequence() {
        return testcardFrequence;
    }

    public void setTestcardFrequence(int testcardFrequence) {
        this.testcardFrequence = testcardFrequence;
    }
}
