package main.java.com.ubo.tp.message.core.selection;

import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.UUID;

/**
 * Interface représentant la session active.
 *
 * @author S.Lucas
 */
public interface ISelection {

	/**
	 * Ajoute un observateur à la session.
	 *
	 * @param observer
	 */
	void addObserver(ISelectionObserver observer);

	/**
	 * Retire un observateur à la session.
	 *
	 * @param observer
	 */
	void removeObserver(ISelectionObserver observer);


	AbstractMessageAppObject getmSelected();

}
