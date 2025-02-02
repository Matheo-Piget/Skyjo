package org.App.view;

import org.App.model.ICard;

/**
 * The ICardView interface provides methods to get and set the value of a card,
 * as well as to retrieve the index of the card.
 */
public interface ICardView {
    ICard getValue();
    /**
     * Sets the value of the card.
     *
     * @param value the ICard instance to set as the value
     */
    void setValue(ICard value);

    /**
     * Gets the index of the card.
     *
     * @return the index of the card.
     */
    int getIndex();
}