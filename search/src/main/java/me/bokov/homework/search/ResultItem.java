package me.bokov.homework.search;

import me.bokov.homework.common.Article;
import me.bokov.homework.common.Word;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public final class ResultItem implements Serializable, Comparable<ResultItem> {

    private final Article article;
    private final Set<Word> foundWords;

    public ResultItem (Article article, Set<Word> foundWords) {
        this.article = article;
        this.foundWords = new HashSet<> (foundWords);
    }

    public int score () {
        return foundWords.size ();
    }

    public Article getArticle () {
        return article;
    }

    public Set<Word> getFoundWords () {
        return foundWords;
    }

    @Override
    public int compareTo (ResultItem o) {
        return Integer.compare (this.score (), o.score ());
    }

}
