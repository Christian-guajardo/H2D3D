package main.java.com.ubo.tp.message.core.selection;

import main.java.com.ubo.tp.message.datamodel.AbstractMessageAppObject;

public interface ISelectionObserver {
	void notify(AbstractMessageAppObject selectedObject);
}
