package main.java.com.ubo.tp.message.core.selection;

import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;
import main.java.com.ubo.tp.message.datamodel.User;

import java.util.UUID;

public interface ISelection {

	void addObserver(ISelectionObserver observer);

	void removeObserver(ISelectionObserver observer);

	AbstractMessageAppObject getmSelected();

}
