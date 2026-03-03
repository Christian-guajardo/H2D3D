package main.java.com.ubo.tp.message.core.selection;



import java.util.UUID;

/**
 * Interface d'observation de la session.
 *
 * @author S.Lucas
 */
public interface ISelectionObserver {

	void notify(UUID selectedUUID);


}
