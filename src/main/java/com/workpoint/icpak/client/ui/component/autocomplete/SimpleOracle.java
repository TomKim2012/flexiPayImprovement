package com.workpoint.icpak.client.ui.component.autocomplete;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.workpoint.icpak.shared.model.Listable;

public abstract class SimpleOracle<T extends Listable> extends SuggestOracle {

	protected List<Suggestion> suggestions = new ArrayList<Suggestion>();
	
	public abstract void setValues(List<T> values);
}
