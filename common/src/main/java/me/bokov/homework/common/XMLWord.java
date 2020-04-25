package me.bokov.homework.common;

import java.util.ArrayList;
import java.util.List;

public class XMLWord {

    private String text;
    private int localId;
    private List<XMLWordMention> wordMentions = new ArrayList<> ();

    public String getText () {
        return text;
    }

    public void setText (String text) {
        this.text = text;
    }

    public int getLocalId () {
        return localId;
    }

    public void setLocalId (int localId) {
        this.localId = localId;
    }

    public List<XMLWordMention> getWordMentions () {
        return wordMentions;
    }

    public void setWordMentions (List<XMLWordMention> wordMentions) {
        this.wordMentions = wordMentions;
    }

}
