package me.bokov.homework.indexer;

import me.bokov.homework.common.ArticleIndex;
import me.bokov.homework.common.ArticleIndexSerializer;

import java.io.FileWriter;

public class Main {

    public static void main (String[] args) {

        Options.getInstance ().parseArgs (args);

        ArticleIndex index = ArticleIndexService.getInstance ()
                .makeIndex ();

        ArticleIndexSerializer indexSerializer = new ArticleIndexSerializer ();

        try (FileWriter indexWriter = new FileWriter (Options.getInstance ().getIndexFile ())) {
            indexSerializer.serialize (index, indexWriter);
        } catch (Exception ex) {
            throw new RuntimeException (ex);
        }

    }

}
