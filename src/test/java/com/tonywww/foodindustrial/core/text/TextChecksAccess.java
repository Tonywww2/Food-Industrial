package com.tonywww.foodindustrial.core.text;

import java.util.List;

/** Test access to the package-private single-entry checks. */
public final class TextChecksAccess {
    private TextChecksAccess() {
    }

    public static void check(String key, String zh, String en, List<String> problems) {
        TextChecks.checkZh(key, zh, problems);
        TextChecks.checkEn(key, en, problems);
    }
}
