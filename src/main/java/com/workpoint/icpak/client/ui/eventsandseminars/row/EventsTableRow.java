package com.workpoint.icpak.client.ui.eventsandseminars.row;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.workpoint.icpak.client.ui.component.RowWidget;
import com.workpoint.icpak.shared.model.events.EventDto;

import static com.workpoint.icpak.client.ui.util.DateUtils.*;
import static com.workpoint.icpak.client.ui.util.NumberUtils.*;

public class EventsTableRow extends RowWidget {

	private static ActivitiesTableRowUiBinder uiBinder = GWT
			.create(ActivitiesTableRowUiBinder.class);

	interface ActivitiesTableRowUiBinder extends
			UiBinder<Widget, EventsTableRow> {
	}

	@UiField
	HTMLPanel row;
	@UiField
	HTMLPanel divDate;
	@UiField
	HTMLPanel divEventName;
	@UiField
	HTMLPanel divDelegates;
	@UiField
	HTMLPanel divEventLocation;
	@UiField
	HTMLPanel divPaidAmount;
	@UiField
	HTMLPanel divUnPaidAmount;
	@UiField
	HTMLPanel divCPDHours;
	@UiField
	Anchor aEventName;
	

	public EventsTableRow() {
		initWidget(uiBinder.createAndBindUi(this));
		
		String url = "#events;eventId=254";
		aEventName.setHref(url);
	}


	public EventsTableRow(EventDto event) {
		this();
		
		String dates = event.getStartDate()==null? "" : DATEFORMAT.format(event.getStartDate())+"-"+
				event.getEndDate()==null? "" : DATEFORMAT.format(event.getEndDate());
		divDate.add(new InlineLabel(dates));
		divDelegates.add(new InlineLabel(event.getDelegateCount()+""));
		divEventLocation.add(new InlineLabel(event.getVenue()));
		divPaidAmount.add(new InlineLabel(NUMBERFORMAT.format(event.getTotalPaid())+""));
		divUnPaidAmount.add(new InlineLabel(NUMBERFORMAT.format(event.getTotalUnpaid())+""));
		divCPDHours.add(new InlineLabel(event.getCpdHours()+""));
		aEventName.setText(event.getName());
		aEventName.setHref("#events;eventId="+event.getRefId());
	}

}
