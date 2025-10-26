package org.project.ninjas.minyala.currency.bot.bot.util;

/***/
public enum Bank {
    /***/
    PRIVAT("ПриватБанк"),
    /***/
    MONO("МоноБанк"),
    /***/
    NBU("НБУ");

    private final String displayName;

    Bank(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return displayName.
     * */
    public String getDisplayName() {
        return displayName;
    }
}
