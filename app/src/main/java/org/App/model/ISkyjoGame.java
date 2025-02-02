package org.App.model;



import java.util.List;


/**
 * Interface representing the Skyjo game.
 */
public interface ISkyjoGame {

    /**
     * Gets the list of players in the game.
     *
     * @return a list of players.
     */
    List<Player> getPlayers();

    /**
     * Picks a card from the deck.
     *
     * @return the picked card.
     */
    ICard pickCard();

    /**
     * Picks a card from the discard pile.
     *
     * @return the picked card from the discard pile.
     */
    ICard pickDiscard();

    /**
     * Adds a card to the discard pile.
     *
     * @param card the card to be added to the discard pile.
     */
    void addToDiscard(ICard card);

    /**
     * Gets the current player whose turn it is.
     *
     * @return the current player.
     */
    Player getActualPlayer();

    /**
     * Advances to the next player's turn.
     */
    void nextPlayer();

    /**
     * Checks if the game is finished.
     *
     * @return true if the game is finished, false otherwise.
     */
    boolean isFinished();

    /**
     * Gets the list of cards in the pick pile.
     *
     * @return a list of cards in the pick pile.
     */
    List<ICard> getPick();

    /**
     * Gets the list of cards in the discard pile.
     *
     * @return a list of cards in the discard pile.
     */
    List<ICard> getDiscard();

    /**
     * Starts the game.
     */
    void startGame();

    /**
     * Exchanges a card for a player.
     *
     * @param player the player exchanging the card.
     * @param newCard the new card to be placed.
     * @param cardIndex the index of the card to be exchanged.
     */
    void exchangeCard(Player player, ICard newCard, int cardIndex);

    /**
     * Reveals a card for a player.
     *
     * @param player the player revealing the card.
     * @param cardIndex the index of the card to be revealed.
     */
    void revealCard(Player player, int cardIndex);

    /**
     * Reveals the initial set of cards for all players.
     */
    void revealInitialCards();
}