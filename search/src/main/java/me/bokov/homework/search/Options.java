package me.bokov.homework.search;

import me.bokov.homework.common.TextUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public final class Options {

    private static final Options INSTANCE = new Options ();

    private File indexFile = new File (System.getProperty ("user.dir"), "IndexFile");
    private File queryExpansionOntologyFile = null;
    private Charset indexFileEncoding = StandardCharsets.ISO_8859_1;
    private boolean interactive = false;
    private Set<String> query = new HashSet<> ();

    private Options () {

    }

    public static Options getInstance () {
        return INSTANCE;
    }

    private void printHelp () {

        System.out.println ("Usage:                                                                               ");
        System.out.println ();
        System.out.println ("-e <encoding>                                                                        ");
        System.out.println ("--encoding <encoding>            Index file uses <encoding> encoding                 ");
        System.out.println ();
        System.out.println ("-i <file>                                                                            ");
        System.out.println ("--index-file <file>              <file> is the index file                            ");
        System.out.println ();
        System.out.println ("-q <word>                                                                            ");
        System.out.println ("--query <word>                   Query for <word>                                    ");
        System.out.println ();
        System.out.println ("-x <file>                                                                            ");
        System.out.println ("--query-expansion <file>         Enable query expansion, <file> contains the ontology");
        System.out.println ();
        System.out.println ("-h                                                                                   ");
        System.out.println ("--help                           Print help message                                  ");

    }

    public void parseArgs (String[] args) {

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals ("-it") || args[i].equals ("--interactive")) {
                this.interactive = true;
            } else if (args[i].equals ("-h") || args[i].equals ("--help")) {
                printHelp ();
                System.exit (0);
            }

            if (i > 0) {

                switch (args[i - 1]) {
                    case "-i":
                    case "--index-file":
                        this.indexFile = new File (args[i]);
                        break;
                    case "-q":
                    case "--query":
                        this.query.add (TextUtils.tokenToWord (args[i]));
                        break;
                    case "-e":
                    case "--encoding":
                        this.indexFileEncoding = Charset.forName (args[i]);
                        break;
                    case "-x":
                    case "--query-expansion":
                        this.queryExpansionOntologyFile = new File (args[i]);
                        break;
                }

            }

        }

    }

    public File getIndexFile () {
        return indexFile;
    }

    public Charset getIndexFileEncoding () {
        return indexFileEncoding;
    }

    public boolean isInteractive () {
        return interactive;
    }

    public Set<String> getQuery () {
        return query;
    }

    public File getQueryExpansionOntologyFile () {
        return queryExpansionOntologyFile;
    }

}
