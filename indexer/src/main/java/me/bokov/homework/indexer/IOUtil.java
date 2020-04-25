package me.bokov.homework.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class IOUtil {

    private IOUtil () {
        throw new UnsupportedOperationException ();
    }

    public static String readTextFileContents (File textFile) {

        try (
                FileReader fileReader = new FileReader (textFile, Options.getInstance ().getDataCharset ());
                BufferedReader bufferedReader = new BufferedReader (fileReader)
        ) {

            String line = null;
            List<String> lines = new ArrayList<> ();
            while ((line = bufferedReader.readLine ()) != null) {

                String stripped = line.strip ();
                if (!stripped.isBlank ()) {
                    lines.add (stripped);
                }

            }

            return String.join ("\n", lines);

        } catch (IOException ex) {
            throw new RuntimeException (ex);
        }

    }

}
