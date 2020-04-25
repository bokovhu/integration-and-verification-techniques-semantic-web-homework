package me.bokov.homework.common;

import javax.xml.stream.*;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

public class ArticleIndexSerializer {

    public void serialize (
            ArticleIndex index,
            Writer writer
    ) throws Exception {

        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newDefaultFactory ();
        XMLStreamWriter xmlWriter = xmlOutputFactory.createXMLStreamWriter (writer);

        xmlWriter.writeStartDocument ();

        xmlWriter.writeStartElement ("i");

        xmlWriter.writeStartElement ("a");

        for (Article article : index.getArticles ()) {
            xmlWriter.writeStartElement ("a");
            xmlWriter.writeAttribute ("f", article.getId ());
            xmlWriter.writeAttribute ("l", article.getLocalId () + "");
            xmlWriter.writeAttribute ("y", article.getYear () + "");
            xmlWriter.writeAttribute ("m", article.getMonth () + "");
            xmlWriter.writeAttribute ("c", article.getCategory ());

            xmlWriter.writeStartElement ("t");
            xmlWriter.writeCData (article.getTitle ());
            xmlWriter.writeEndElement ();

            xmlWriter.writeStartElement ("c");
            xmlWriter.writeCData (article.getContent ());
            xmlWriter.writeEndElement ();

            xmlWriter.writeEndElement ();
        }

        xmlWriter.writeEndElement (); // </a>

        xmlWriter.writeStartElement ("w");

        for (Map.Entry<String, Word> wordEntry : index.getWords ().entrySet ()) {

            xmlWriter.writeStartElement ("w");
            xmlWriter.writeAttribute ("t", wordEntry.getKey ());
            xmlWriter.writeAttribute ("l", wordEntry.getValue ().getLocalId () + "");

            List<WordMention> mentions = index.getWordMentions ()
                    .getOrDefault (wordEntry.getKey (), Collections.emptyList ());
            for (WordMention wm : mentions) {

                xmlWriter.writeStartElement ("m");
                xmlWriter.writeAttribute ("a", wm.getArticle ().getLocalId () + "");
                xmlWriter.writeAttribute ("c", wm.getNumberOfOccurences () + "");
                xmlWriter.writeEndElement ();

            }

            xmlWriter.writeEndElement ();
        }

        xmlWriter.writeEndElement (); // </w>

        xmlWriter.writeEndElement (); // </i>

        xmlWriter.writeEndDocument ();

    }

    private String readCData (XMLStreamReader reader) throws Exception {

        String cdata = null;

        boolean stop = false;

        while (reader.hasNext () && !stop) {

            int type = reader.next ();

            switch (type) {
                case XMLStreamReader.CDATA:
                case XMLStreamConstants.CHARACTERS:
                    cdata = reader.getText ();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    stop = true;
                    break;
            }

        }

        return cdata;

    }

    private XMLArticle readArticle (XMLStreamReader reader) throws Exception {

        XMLArticle xmlArticle = new XMLArticle ();

        xmlArticle.setFullId (reader.getAttributeValue (null, "f"));
        xmlArticle.setLocalId (
                Integer.parseInt (reader.getAttributeValue (null, "l"))
        );
        xmlArticle.setYear (
                Integer.parseInt (reader.getAttributeValue (null, "y"))
        );
        xmlArticle.setMonth (
                Integer.parseInt (reader.getAttributeValue (null, "m"))
        );
        xmlArticle.setCategory (reader.getAttributeValue (null, "c"));

        boolean stop = false;

        while (reader.hasNext () && !stop) {

            int type = reader.next ();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:

                    final String startedElementName = reader.getLocalName ();

                    if (startedElementName.equals ("c")) {
                        xmlArticle.setContent (readCData (reader));
                    } else if (startedElementName.equals ("t")) {
                        xmlArticle.setTitle (readCData (reader));
                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:

                    stop = true;

                    break;
            }

        }

        return xmlArticle;

    }

    private List<XMLArticle> readArticles (XMLStreamReader reader) throws Exception {

        List<XMLArticle> result = new ArrayList<> ();

        boolean stop = false;

        while (reader.hasNext () && !stop) {

            int type = reader.next ();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:

                    final String startedElementName = reader.getLocalName ();

                    if (startedElementName.equals ("a")) {
                        result.add (readArticle (reader));
                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:

                    stop = true;

                    break;
            }

        }

        return result;

    }

    private List<XMLWordMention> readWordMentions (XMLStreamReader reader) throws Exception {

        List<XMLWordMention> result = new ArrayList<> ();

        boolean stop = false;

        while (reader.hasNext () && !stop) {

        }

        return result;

    }

    private XMLWord readWord (XMLStreamReader reader) throws Exception {

        XMLWord result = new XMLWord ();

        result.setText (reader.getAttributeValue (null, "t"));
        result.setLocalId (
                Integer.parseInt (reader.getAttributeValue (null, "l"))
        );

        boolean stop = false;

        while (reader.hasNext () && !stop) {

            int type = reader.next ();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:

                    final String startedElementName = reader.getLocalName ();

                    if (startedElementName.equals ("m")) {

                        XMLWordMention mention = new XMLWordMention ();
                        mention.setArticleLocalId (
                                Integer.parseInt (reader.getAttributeValue (null, "a"))
                        );
                        mention.setCount (
                                Integer.parseInt (reader.getAttributeValue (null, "c"))
                        );

                        result.getWordMentions ().add (mention);

                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:

                    final String endedElementName = reader.getLocalName ();

                    if (endedElementName.equals ("w")) {

                        stop = true;

                    }

                    break;
            }

        }

        return result;

    }

    private List<XMLWord> readWords (XMLStreamReader reader) throws Exception {

        List<XMLWord> result = new ArrayList<> ();

        boolean stop = false;

        while (reader.hasNext () && !stop) {

            int type = reader.next ();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:

                    final String startedElementName = reader.getLocalName ();

                    if (startedElementName.equals ("w")) {

                        result.add (readWord (reader));

                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:

                    stop = true;

                    break;
            }

        }

        return result;

    }

    public ArticleIndex deserialize (Reader reader) throws Exception {

        XMLInputFactory xmlInputFactory = XMLInputFactory.newDefaultFactory ();
        XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader (reader);

        boolean rootStarted = false;

        List<XMLArticle> xmlArticles = new ArrayList<> ();
        List<XMLWord> xmlWords = new ArrayList<> ();

        while (xmlReader.hasNext ()) {

            int eventType = xmlReader.next ();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:

                    final String startedElementName = xmlReader.getLocalName ();

                    if (!rootStarted) {

                        if (startedElementName.equals ("i")) {
                            rootStarted = true;
                        }

                    } else {

                        if (startedElementName.equals ("a")) {
                            xmlArticles.addAll (readArticles (xmlReader));
                        } else if (startedElementName.equals ("w")) {
                            xmlWords.addAll (readWords (xmlReader));
                        }

                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    break;
                case XMLStreamReader.ATTRIBUTE:
                    break;
                case XMLStreamReader.CDATA:
                    break;
            }

        }

        ArticleIndex articleIndex = new ArticleIndex ();

        Map<Integer, Article> articleMap = new HashMap<> ();

        for (XMLArticle xmlA : xmlArticles) {

            Article article = new Article ();

            article.setId (xmlA.getFullId ());
            article.setLocalId (xmlA.getLocalId ());
            article.setYear (xmlA.getYear ());
            article.setMonth (xmlA.getMonth ());
            article.setCategory (xmlA.getCategory ());
            article.setTitle (xmlA.getTitle ());
            article.setContent (xmlA.getContent ());

            articleMap.put (article.getLocalId (), article);

        }

        articleIndex.setArticles (new ArrayList<> (articleMap.values ()));

        Map<String, Word> wordMap = new HashMap<> ();
        Map<String, List<WordMention>> wordMentionMap = new HashMap<> ();

        for (XMLWord xmlWord : xmlWords) {

            Word word = new Word ();
            word.setLocalId (xmlWord.getLocalId ());
            word.setText (xmlWord.getText ());

            wordMap.put (word.getText (), word);

            List<WordMention> wordMentions = new ArrayList<> ();

            for (XMLWordMention xmlWordMention : xmlWord.getWordMentions ()) {

                WordMention wordMention = new WordMention ();
                wordMention.setWord (word);
                wordMention.setArticle (articleMap.get (xmlWordMention.getArticleLocalId ()));
                wordMention.setNumberOfOccurences (xmlWordMention.getCount ());

                wordMentions.add (wordMention);

            }

            wordMentionMap.put (word.getText (), wordMentions);

        }

        articleIndex.setWords (wordMap);
        articleIndex.setWordMentions (wordMentionMap);

        return articleIndex;

    }

}
