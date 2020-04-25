package me.bokov.homework.common;

import java.util.*;

public class ArticleIndex {

    private List<Article> articles = new ArrayList<> ();
    private Map<String, Word> words = new HashMap<> ();
    private Map<String, List<WordMention>> wordMentions = new HashMap<> ();
    private Set<String> stopWords = new HashSet<> ();

    public List<Article> getArticles () {
        return articles;
    }

    public void setArticles (List<Article> articles) {
        this.articles = articles;
    }

    public Map<String, Word> getWords () {
        return words;
    }

    public void setWords (Map<String, Word> words) {
        this.words = words;
    }

    public Map<String, List<WordMention>> getWordMentions () {
        return wordMentions;
    }

    public void setWordMentions (Map<String, List<WordMention>> wordMentions) {
        this.wordMentions = wordMentions;
    }

    public Set<String> getStopWords () {
        return stopWords;
    }

    public void setStopWords (Set<String> stopWords) {
        this.stopWords = stopWords;
    }

}
