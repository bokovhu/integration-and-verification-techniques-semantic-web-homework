package me.bokov.homework.indexer;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Options {

    private static final Options INSTANCE = new Options ();

    private File dataDirectory = new File (System.getProperty ("user.dir"), "data");
    private File stopWordsFile = new File (System.getProperty ("user.dir"), "stopwords.txt");
    private File indexFile = new File (System.getProperty ("user.dir"), "IndexFile");
    private Charset dataCharset = StandardCharsets.ISO_8859_1;

    public static Options getInstance () {
        return INSTANCE;
    }

    private void printHelp () {

        System.out.println ("Usage:                                                                     ");
        System.out.println ();
        System.out.println ("-d <directory>                                                             ");
        System.out.println ("--data-directory <directory>     <directory> contains the articles to index");
        System.out.println ();
        System.out.println ("-s <file>                                                                  ");
        System.out.println ("--stop-words <file>              <file> contains the stop words            ");
        System.out.println ();
        System.out.println ("-o <file>                                                                  ");
        System.out.println ("--out-filename <file>            Write index to <file>                     ");
        System.out.println ();
        System.out.println ("-e <encoding>                                                              ");
        System.out.println ("--encoding <encoding>            Articles use <encoding> encoding          ");
        System.out.println ();
        System.out.println ("-h                                                                         ");
        System.out.println ("--help                           Print help message");


    }

    public void parseArgs (String[] args) {

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals ("-h") || args[i].equals ("--help")) {
                printHelp ();
                System.exit (0);
            }

            if (i > 0) {
                switch (args[i - 1]) {
                    case "-d":
                    case "--data-directory":
                        this.dataDirectory = new File (args[i]);
                        break;
                    case "-o":
                    case "--out-filename":
                        this.dataDirectory = new File (args[i]);
                        break;
                    case "-e":
                    case "--encoding":
                        this.dataCharset = Charset.forName (args[i]);
                        break;
                    case "-s":
                    case "--stop-words":
                        this.stopWordsFile = new File (args[i]);
                        break;
                }
            }

        }

    }

    public File getDataDirectory () {
        return this.dataDirectory;
    }

    public File getIndexFile () {
        return this.indexFile;
    }

    public Charset getDataCharset () {
        return dataCharset;
    }

    public File getStopWordsFile () {
        return stopWordsFile;
    }

}
