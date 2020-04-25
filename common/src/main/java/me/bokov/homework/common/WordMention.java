package me.bokov.homework.common;

public final class WordMention {

    private Word word;
    private Article article;
    private int numberOfOccurences;

    public Word getWord () {
        return word;
    }

    public void setWord (Word word) {
        this.word = word;
    }

    public Article getArticle () {
        return article;
    }

    public void setArticle (Article article) {
        this.article = article;
    }

    public int getNumberOfOccurences () {
        return numberOfOccurences;
    }

    public void setNumberOfOccurences (int numberOfOccurences) {
        this.numberOfOccurences = numberOfOccurences;
    }

}
