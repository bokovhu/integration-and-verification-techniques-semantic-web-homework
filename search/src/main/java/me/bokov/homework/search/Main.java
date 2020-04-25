package me.bokov.homework.search;

import me.bokov.homework.common.ArticleIndex;
import me.bokov.homework.common.ArticleIndexSerializer;
import me.bokov.homework.common.Word;

import java.io.FileReader;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Main {

    private static <T> T measure (String label, Callable<T> callable) throws Exception {

        final long start = System.currentTimeMillis ();

        final T returnValue = callable.call ();

        System.out.println (label + " -> " + (System.currentTimeMillis () - start) + "ms");

        return returnValue;

    }

    public static void main (String[] args) throws Exception {

        Options.getInstance ().parseArgs (args);
        System.out.println ("Reading index from " + Options.getInstance ().getIndexFile ().getAbsolutePath () + " ...");

        ArticleIndexSerializer articleIndexSerializer = new ArticleIndexSerializer ();
        ArticleIndex articleIndex = null;

        try (FileReader fileReader = new FileReader (Options.getInstance ().getIndexFile ())) {

            articleIndex = articleIndexSerializer.deserialize (fileReader);

        } catch (Exception exc) {
            throw new RuntimeException (exc);
        }

        QueryExpansionService.getInstance ().init ();

        System.out.printf ("Results for query %s:\n", String.join (", ", Options.getInstance ().getQuery ()));
        List<ResultItem> results = SearchService.getInstance ()
                .executeQuery (
                        articleIndex,
                        Options.getInstance ().getQuery ()
                );
        results.sort (Comparator.comparing (ResultItem::score).reversed ());

        results.forEach (
                result -> System.out.printf (
                        "* [%d] (%s) - %d / %d / %s - %s\n",
                        result.score (),
                        result.getFoundWords ().stream ().map (Word::getText).collect (Collectors.joining (", ")),
                        result.getArticle ().getYear (),
                        result.getArticle ().getMonth (),
                        result.getArticle ().getCategory (),
                        result.getArticle ().getTitle ()
                )
        );

    }

}
