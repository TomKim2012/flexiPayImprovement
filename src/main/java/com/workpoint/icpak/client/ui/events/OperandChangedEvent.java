package com.workpoint.icpak.client.ui.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class OperandChangedEvent extends
		GwtEvent<OperandChangedEvent.OperandChangedHandler> {

	public static Type<OperandChangedHandler> TYPE = new Type<OperandChangedHandler>();
	private String sourceField;
	private Object newValue;
	private Long lineRefId;

	public interface OperandChangedHandler extends EventHandler {
		void onOperandChanged(OperandChangedEvent event);
	}

	public OperandChangedEvent(String sourceField, Object newValue) {
		this.sourceField = sourceField;
		this.newValue = newValue;
	}
	
	public OperandChangedEvent(String documentSpecificFieldName, Object newValue, Long lineRefId) {
		this.sourceField = documentSpecificFieldName;
		this.newValue = newValue;
		this.lineRefId = lineRefId;
	}

	public String getSourceField() {
		return sourceField;
	}

	@Override
	protected void dispatch(OperandChangedHandler handler) {
		handler.onOperandChanged(this);
	}

	@Override
	public Type<OperandChangedHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<OperandChangedHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, String sourceField, Object newValue) {
		source.fireEvent(new OperandChangedEvent(sourceField, newValue));
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

	public Long getLineRefId() {
		return lineRefId;
	}

	public void setLineRefId(Long lineRefId) {
		this.lineRefId = lineRefId;
	}
}
