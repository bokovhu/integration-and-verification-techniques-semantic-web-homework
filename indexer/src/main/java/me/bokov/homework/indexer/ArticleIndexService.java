package me.bokov.homework.indexer;

import me.bokov.homework.common.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ArticleIndexService {

    private static final ArticleIndexService INSTANCE = new ArticleIndexService ();

    private static final String TERM_DELIMITERS = ".,;:?!\"()\t\r\n ";

    private int articleIdSequence = 0;
    private int wordIdSequence = 0;

    private Path rootPath;

    private ArticleIndexService () {

    }

    public static ArticleIndexService getInstance () {
        return INSTANCE;
    }

    private Article processArticleFile (File articleFile) {

        String articleTextContent = IOUtil.readTextFileContents (articleFile);
        String[] splittedByNewline = articleTextContent.split ("\n", 2);

        final String articleTitle = splittedByNewline[0];
        final Path articleRelativePath = rootPath.relativize (articleFile.toPath ());
        // <...something>/[category]/[year]/[month]/[article number].txt

        if (articleRelativePath.getNameCount () < 4) {
            throw new IllegalArgumentException ("Invalid data directory structure!");
        }

        final String articleNumber = articleRelativePath.getName (articleRelativePath.getNameCount () - 1).toString ();
        final Integer articleMonth = Integer.parseInt (
                articleRelativePath.getName (articleRelativePath.getNameCount () - 2).toString ()
        );
        final Integer articleYear = Integer.parseInt (
                articleRelativePath.getName (articleRelativePath.getNameCount () - 3).toString ()
        );
        final String articleCategory = articleRelativePath.getName (articleRelativePath.getNameCount () - 4)
                .toString ();

        Article article = new Article ();
        article.setId (articleRelativePath.toString ());
        article.setYear (articleYear);
        article.setMonth (articleMonth);
        article.setTitle (articleTitle);
        article.setContent (articleTextContent);
        article.setCategory (articleCategory);
        article.setLocalId (articleIdSequence++);

        return article;

    }

    private List<Article> processDirectory (File directory) {

        List<Article> result = new ArrayList<> ();

        File[] children = directory.listFiles ();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory ()) {
                    result.addAll (processDirectory (child));
                } else {
                    result.add (processArticleFile (child));
                }
            }
        }

        return result;

    }

    private ArticleIndex performIndexing (List<Article> articles, Set<String> stopWords) {

        ArticleIndex index = new ArticleIndex ();

        index.setArticles (articles);
        index.setStopWords (stopWords);

        for (Article article : articles) {

            StringTokenizer stringTokenizer = new StringTokenizer (article.getContent (), TERM_DELIMITERS);
            Map<String, Integer> wordOccurences = new HashMap<> ();
            while (stringTokenizer.hasMoreTokens ()) {

                String token = TextUtils.tokenToWord (stringTokenizer.nextToken ());

                if (token.isBlank ()) {
                    continue;
                }

                if (stopWords.contains (token)) {
                    continue;
                }

                wordOccurences.compute (
                        token,
                        (word, value) -> 1 + (value == null ? 0 : value)
                );

            }

            for (Map.Entry<String, Integer> occurenceEntry : wordOccurences.entrySet ()) {

                Word word = index.getWords ().computeIfAbsent (
                        occurenceEntry.getKey (),
                        text -> {
                            Word w = new Word ();
                            w.setLocalId (wordIdSequence++);
                            w.setText (text);
                            return w;
                        }
                );
                WordMention mention = new WordMention ();
                mention.setWord (word);
                mention.setArticle (article);
                mention.setNumberOfOccurences (occurenceEntry.getValue ());

                index.getWordMentions ().compute (
                        word.getText (),
                        (wordText, mentionList) -> {

                            List<WordMention> newValue = mentionList;
                            if (mentionList == null) {
                                newValue = new ArrayList<> ();
                            }
                            newValue.add (mention);

                            return newValue;

                        }
                );

            }

        }

        return index;

    }

    private void writeIndex () {

        try (FileWriter xmlFileWriter = new FileWriter (Options.getInstance ().getIndexFile ())) {


        } catch (Exception exc) {
            throw new RuntimeException (exc);
        }

    }

    private Set<String> readStopWords () {

        Set<String> stopWords = new HashSet<> ();

        try {

            final File stopWordsFile = Options.getInstance ().getStopWordsFile ();

            if (!stopWordsFile.isFile () || !stopWordsFile.exists ()) {
                System.err.println ("WARNING! Stop words file does not exist or is not a file!");
                return stopWords;
            }

            stopWords.addAll (
                    Arrays.stream (
                            IOUtil.readTextFileContents (stopWordsFile)
                                    .split ("\n")
                    )
                            .map (w -> w.strip ().toLowerCase ())
                            .collect (Collectors.toList ())
            );

        } catch (Exception exc) {
            throw new RuntimeException (exc);
        }

        return stopWords;

    }

    public ArticleIndex makeIndex () {

        this.rootPath = Options.getInstance ().getDataDirectory ().toPath ();

        Set<String> stopWords = readStopWords ();
        System.out.println ("Read " + stopWords.size () + " stop words");

        List<Article> articles = processDirectory (Options.getInstance ().getDataDirectory ());
        System.out.println ("Read " + articles.size () + " articles, starting indexing ...");

        ArticleIndex index = performIndexing (articles, stopWords);
        System.out.println ("Indexed " + index.getWordMentions ().size () + " words, creating index file ...");

        return index;

    }

}
