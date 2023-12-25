package com.templatemela.smartpdfreader.model;

public class FAQItem {
    private String mAnswer;
    private boolean mIsExpanded = false;
    private String mQuestion;

    public FAQItem(String mQuestion, String mAnswer) {
        this.mQuestion = mQuestion;
        this.mAnswer = mAnswer;
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.mIsExpanded = expanded;
    }

    public String getQuestion() {
        return this.mQuestion;
    }

    public String getAnswer() {
        return this.mAnswer;
    }
}
