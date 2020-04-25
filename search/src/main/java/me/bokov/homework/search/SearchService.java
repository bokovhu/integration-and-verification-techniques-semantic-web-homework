package me.bokov.homework.search;

import me.bokov.homework.common.Article;
import me.bokov.homework.common.ArticleIndex;
import me.bokov.homework.common.WordMention;

import java.util.*;
import java.util.stream.Collectors;

public class SearchService {

    private static final SearchService INSTANCE = new SearchService ();

    private SearchService () {

    }

    public static SearchService getInstance () {
        return INSTANCE;
    }

    private <T> Set<T> intersection (Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<> ();
        for (T t : a) {
            if (b.contains (t)) {
                result.add (t);
            }
        }
        return result;
    }

    public List<ResultItem> executeQuery (
            ArticleIndex index,
            Set<String> terms
    ) {

        List<ResultItem> results = new ArrayList<> ();

        Set<String> searchTerms = QueryExpansionService.getInstance ()
                .expandSearchTerms (terms);

        Map<Integer, List<WordMention>> wordMentionsByArticle = new HashMap<> ();
        Map<Integer, Article> articlesByLocalId = new HashMap<> ();

        index.getWordMentions ()
                .entrySet ()
                .stream ()
                .filter (e -> searchTerms.contains (e.getKey ()))
                .flatMap (e -> e.getValue ().stream ())
                .forEach (
                        wm -> wordMentionsByArticle.computeIfAbsent (
                                wm.getArticle ().getLocalId (),
                                (key) -> new ArrayList<> ()
                        ).add (wm)
                );
        wordMentionsByArticle.keySet ()
                .forEach (
                        localId -> articlesByLocalId.put (
                                localId,
                                index.getArticles ()
                                        .stream ()
                                        .filter (a -> a.getLocalId () == localId)
                                        .findFirst ()
                                        .orElse (null)
                        )
                );

        wordMentionsByArticle.forEach ((key, value) -> results.add (
                new ResultItem (
                        articlesByLocalId.get (key),
                        value.stream ()
                                .map (WordMention::getWord)
                                .collect (Collectors.toSet ())
                )
        ));

        return results;

    }

}
