package me.bokov.homework.common;

public final class TextUtils {

    private TextUtils () {

    }

    public static String tokenToWord (String token) {

        return token.strip ()
                .replaceAll ("[.:,;\\-_'\"!%\\\\/=()^*?\\[\\]<>#&@{}$|]", "")
                .toLowerCase ();

    }

}
