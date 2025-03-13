package org.App.network;

public class Protocol {
    // Types de messages
    public static final String PLAYER_JOIN = "JOIN";
    public static final String GAME_START = "START";
    public static final String CARD_PICK = "PICK";
    public static final String CARD_DISCARD = "DISCARD";
    public static final String CARD_REVEAL = "REVEAL";
    public static final String CARD_EXCHANGE = "EXCHANGE";
    public static final String PLAYER_TURN = "TURN";
    public static final String GAME_STATE = "STATE";
    public static final String GAME_END = "END";
    public static final String ERROR = "ERROR";
    public static final String PLAYER_LEFT = "LEFT";
    
    // Format: TYPE|playerId|[données supplémentaires selon le type]
    public static String formatMessage(String type, int playerId, String... data) {
        StringBuilder sb = new StringBuilder(type).append("|").append(playerId);
        for (String d : data) {
            sb.append("|").append(d);
        }
        return sb.toString();
    }
    
    public static String[] parseMessage(String message) {
        return message.split("\\|");
    }
}