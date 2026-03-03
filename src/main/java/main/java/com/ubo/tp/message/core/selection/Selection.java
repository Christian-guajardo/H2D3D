package main.java.com.ubo.tp.message.core.selection;

import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Session de l'application.
 *
 * @author S.Lucas
 */
public class Selection implements ISelection {
	protected User mSelectedUser;
	protected Channel mSelectedChannel;

	protected List<ISelectionObserver> mObservers = new ArrayList<>();

	@Override
	public void addObserver(ISelectionObserver observer) {
		this.mObservers.add(observer);
	}

	@Override
	public void removeObserver(ISelectionObserver observer) {
		this.mObservers.remove(observer);
	}

	@Override
	public AbstractMessageAppObject getmSelected() {
		if (mSelectedUser == null) {
			return mSelectedChannel;
		}else{
			return mSelectedUser;
		}
	}


}
