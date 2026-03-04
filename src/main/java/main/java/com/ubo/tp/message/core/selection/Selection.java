package main.java.com.ubo.tp.message.core.selection;

import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.List;

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
	public void notifyObservers() {
		for (ISelectionObserver observer : mObservers) {
			observer.notify(getmSelected());
		}
	}

	@Override
	public AbstractMessageAppObject getmSelected() {
		if (mSelectedUser == null) {
			return mSelectedChannel;
		}else{
			return mSelectedUser;
		}
	}

	public void changeSelection(User mSelectedUser) {
		this.mSelectedUser = mSelectedUser;
		this.mSelectedChannel = null;
		notifyObservers();
	}
	public void changeSelection(Channel mSelectedChannel) {
		this.mSelectedChannel = mSelectedChannel;
		this.mSelectedUser = null;
		notifyObservers();
	}


}
