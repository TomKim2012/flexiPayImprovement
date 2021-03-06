package com.workpoint.icpak.client.ui.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.workpoint.icpak.client.ui.component.AutoCompleteField;
import com.workpoint.icpak.client.ui.component.DropDownList;
import com.workpoint.icpak.client.ui.component.RowWidget;

public class AggregationGridRow extends RowWidget {

	private static AggrationGridRowUiBinder uiBinder = GWT
			.create(AggrationGridRowUiBinder.class);

	interface AggrationGridRowUiBinder extends
			UiBinder<Widget, AggregationGridRow> {
	}

	@UiField
	HTMLPanel row;

	private Long modelId;
	private DataModel model;
	private List<ColumnConfig> configs = null;
	private Map<ColumnConfig, HasValue> columnWigetMap = new HashMap<ColumnConfig, HasValue>();

	private AggregationGrid grid;

	public AggregationGridRow() {
		initWidget(uiBinder.createAndBindUi(this));
		setRow(row);
		setShowRemove(true);
	}

	public AggregationGridRow(AggregationGrid aggregationGrid, DataModel data,
			List<ColumnConfig> columnConfigs) {
		this();
		this.configs = columnConfigs;
		this.grid = aggregationGrid;
		this.model = data;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onLoad() {
		super.onLoad();
		List<Widget> widgets = new ArrayList<Widget>();

		if (configs != null)
			for (ColumnConfig config : configs) {
				Widget widget = config.createWidget(model.get(config.getKey()));
				if(widget instanceof AutoCompleteField){
					((AutoCompleteField)widget).setParentRow(this);
				}
				if(widget instanceof DropDownList){
					((DropDownList)widget).setParentRow(this);
				}
				columnWigetMap.put(config, (HasValue) widget);
				if (config.isAggregationColumn()) {
					grid.addValueChangeHandler(config, widget);
				} else {
					if (widget instanceof HasValueChangeHandlers) {
						HasValueChangeHandlers<Object> hasValueChangeHandlers = (HasValueChangeHandlers<Object>) widget;
						hasValueChangeHandlers
								.addValueChangeHandler(valueChangedHandler);
					}
				}
				widgets.add(widget);
			}
		createRow(widgets);
	}

	ValueChangeHandler<Object> valueChangedHandler = new ValueChangeHandler<Object>() {
		@Override
		public void onValueChange(ValueChangeEvent<Object> event) {
			Object value = event.getValue();
			if (value != null) {
				// grid.createRowLast();
			}
		}
	};

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public DataModel getModel() {
		return model;
	}

	public void setModel(DataModel model){
		setModel(model, false);
	}
	
	public void setModel(DataModel model, boolean mergeNow) {
		this.model = model;
		if(mergeNow){
			for (ColumnConfig column : columnWigetMap.keySet()) {
				String key = column.getKey();
				columnWigetMap.get(column).setValue(model.get(key));
			}
		}
	}

	public DataModel getData() {
		if (model == null) {
			model = new DataModel();
		}

		for (ColumnConfig column : columnWigetMap.keySet()) {
			String key = column.getKey();
			model.set(key, columnWigetMap.get(column).getValue());
		}

		return model;
	}

	public List<String> getErrors() {
		DataModel model = getData();
		if (model.isEmpty()) {
			return null;
		}

		List<String> errors = new ArrayList<String>();
		for (ColumnConfig config : configs) {
			if (config.isMandatory() && model.get(config.getKey()) == null) {
				String error = "Column " + config.getDisplayName()
						+ " is mandatory";
				System.err.println(error);
				errors.add(error);
			}
		}

		if (errors.isEmpty()) {
			return null;
		}

		return errors;
	}

}
