package main.java.com.ubo.tp.message.core.selection;



import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;

import java.util.UUID;

/**
 * Interface d'observation de la session.
 *
 * @author S.Lucas
 */
public interface ISelectionObserver {
	void notify(AbstractMessageAppObject selectedObject);
}
