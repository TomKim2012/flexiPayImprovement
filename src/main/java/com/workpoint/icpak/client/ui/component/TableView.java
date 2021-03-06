package com.workpoint.icpak.client.ui.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.workpoint.icpak.client.ui.frontmember.model.MemberCategory;
import com.workpoint.icpak.shared.model.BookingStatus;
import com.workpoint.icpak.shared.model.Listable;
import com.workpoint.icpak.shared.model.PaymentMode;
import com.workpoint.icpak.shared.model.PaymentStatus;
import com.workpoint.icpak.shared.model.PaymentType;
import com.workpoint.icpak.shared.model.auth.ApplicationStatus;
import com.workpoint.icpak.shared.model.events.AccommodationDto;

public class TableView extends Composite {

	private static TableViewUiBinder uiBinder = GWT.create(TableViewUiBinder.class);

	interface TableViewUiBinder extends UiBinder<Widget, TableView> {
	}

	@UiField
	HTMLPanel overalContainer;
	@UiField
	HTMLPanel tblContainer;
	@UiField
	HTMLPanel panelHeader;
	@UiField
	FlowPanel panelBody;
	@UiField
	HTMLPanel panelFooter;
	@UiField
	HTMLPanel divSearch;
	@UiField
	HTMLPanel panelPaging;
	@UiField
	ActionLink aDownloadPdf;
	@UiField
	ActionLink aDownloadXls;
	@UiField
	ActionLink aDownloadOfflineSQL;

	@UiField
	ActionLink aDownloadAllCPDTranscripts;

	@UiField
	TextField txtSearch;
	@UiField
	ActionLink aFilter;
	@UiField
	ActionLink aSearch;
	@UiField
	ActionLink aAdvancedSearch;

	@UiField
	HTMLPanel panelSearch;
	@UiField
	HTMLPanel panelDates;
	@UiField
	HTMLPanel panelActionButtons;
	@UiField
	DateField dtStartDate;
	@UiField
	DateField dtEndDate;
	@UiField
	HTMLPanel panelTowns;

	@UiField
	HTMLPanel panelApplicationStatus;
	@UiField
	HTMLPanel panelPaymentStatus;
	@UiField
	HTMLPanel panelPaymentMode;
	@UiField
	HTMLPanel panelPaymentType;

	@UiField
	HTMLPanel panelAccomodation;
	@UiField
	HTMLPanel panelBookingStatus;

	@UiField
	DropDownList<Towns> listTowns;
	@UiField
	DropDownList<MemberCategory> listMemberCategory;

	@UiField
	DropDownList<ApplicationStatus> lstApplicationStatus;
	@UiField
	DropDownList<PaymentStatus> lstPaymentStatus;
	@UiField
	DropDownList<PaymentMode> lstPaymentMode;
	@UiField
	DropDownList<PaymentType> lstPaymentType;

	@UiField
	DropDownList<AccommodationDto> lstAccomodation;
	@UiField
	DropDownList<BookingStatus> lstBookingStatus;

	@UiField
	DivElement divTownList;
	@UiField
	DivElement divMemberCategory;
	@UiField
	DivElement divPeriodLabel;

	private boolean isAutoNumber = true;
	private int count = 0;

	public int getCount() {
		return count;
	}

	public TableView() {
		initWidget(uiBinder.createAndBindUi(this));
		setSearchSectionVisible(false);
		setDatesVisible(false);
		setTownListVisible(false);
		setMemberCategoryVisible(false);
		setApplicationAndPaymentVisible(false);
		showFinanceFilters(false);
		showDelegateFilters(false);
		aSearch.addStyleName("hide");
	}

	public void showDelegateFilters(boolean show) {
		if (show) {
			panelAccomodation.setVisible(true);
			panelBookingStatus.setVisible(true);
		} else {
			panelAccomodation.setVisible(false);
			panelBookingStatus.setVisible(false);
		}
	}

	public void setApplicationAndPaymentVisible(boolean show) {
		if (show) {
			panelPaymentStatus.setVisible(true);
			panelApplicationStatus.setVisible(true);
		} else {
			panelPaymentStatus.setVisible(false);
			panelApplicationStatus.setVisible(false);
		}
	}

	public void showFinanceFilters(boolean show) {
		if (show) {
			panelPaymentMode.setVisible(true);
			panelPaymentType.setVisible(true);
			aAdvancedSearch.setVisible(true);
		} else {
			panelPaymentType.setVisible(false);
			panelPaymentMode.setVisible(false);
			aAdvancedSearch.setVisible(false);
		}
	}

	public void setHeaders(List<String> names) {
		setHeaders(null, names);
	}

	public void setSearchFieldVisible(boolean show) {
		if (show) {
			panelSearch.setVisible(true);
		} else {
			panelSearch.setVisible(false);
		}
	}

	public void setDatesVisible(boolean show) {
		if (show) {
			panelDates.setVisible(true);
		} else {
			panelDates.setVisible(false);
		}
	}

	public void setDatesVisible(boolean show, boolean isLabelVisible) {
		setDatesVisible(show);
		if (isLabelVisible) {
			divPeriodLabel.removeClassName("hide");
			aFilter.removeStyleName("hide");
		} else {
			divPeriodLabel.addClassName("hide");
			aFilter.addStyleName("hide");
		}
	}

	public void setTowns(List<Towns> townNames) {
		listTowns.setItems(townNames, "All Towns");
	}

	public void setMemberCategories(List<MemberCategory> categories) {
		listMemberCategory.setItems(categories, "All Members");
	}

	public DropDownList<Towns> getTowns() {
		return listTowns;
	}

	public DropDownList<MemberCategory> getCategoryDropdown() {
		return listMemberCategory;
	}

	public String getCategorySelected() {
		return listMemberCategory.getValue().getName();
	}

	public void setActionsVisible(boolean show) {
		if (show) {
			panelActionButtons.setVisible(true);
		} else {
			panelActionButtons.setVisible(false);
		}
	}

	public Date getStartDate() {
		return dtStartDate.getValueDate();
	}

	public Date getEndDate() {
		return dtEndDate.getValueDate();
	}

	public void setHeaders(List<String> tdStyles, List<String> names) {
		List<Widget> widgets = new ArrayList<Widget>();
		for (String name : names) {
			InlineLabel label = new InlineLabel(name);
			widgets.add(label);
		}

		setHeaderWidgets(tdStyles, widgets);
	}

	public void setTableHeaders(List<TableHeader> headers) {
		panelHeader.clear();
		if (isAutoNumber) {
			headers.add(0, new TableHeader("#", 1.0));
		}

		// InlineLabel label = new InlineLabel(header.getTitleName());
		for (TableHeader header : headers) {
			// th
			HTMLPanel th = new HTMLPanel("");
			th.addStyleName("th");

			// Label
			InlineLabel label = new InlineLabel(header.getTitleName());
			th.add(label);

			// add to row
			if (header.getWidth() != null) {
				th.getElement().getStyle().setWidth(header.getWidth(), Unit.PCT);
			}

			if (header.getStyleName() != null) {
				th.addStyleName(header.getStyleName());
			}

			panelHeader.add(th);
		}
	}

	@Override
	public Widget asWidget() {
		return super.asWidget();
	}

	public void setHeaderWidgets(List<String> tdStyles, List<Widget> widgets) {
		panelHeader.clear();
		if (isAutoNumber) {
			InlineLabel label = new InlineLabel("#");
			widgets.add(0, label);
		}

		int i = isAutoNumber ? -1 : 0;
		for (Widget widget : widgets) {
			HTMLPanel th = new HTMLPanel("");
			th.addStyleName("th");
			if (i != -1 && tdStyles != null && i < tdStyles.size()) {
				String style = tdStyles.get(i);
				if (style != null && !style.trim().isEmpty())
					th.addStyleName(style);
			}
			th.add(widget);
			i++;
			panelHeader.add(th);
		}
	}

	public void addRow(Widget... widgets) {
		addRow(Arrays.asList(widgets));
	}

	public void addRow(List<String> tdStyles, Widget... widgets) {
		addRow(tdStyles, Arrays.asList(widgets));
	}

	public void addRow(List<Widget> widgets) {
		addRow(new ArrayList<String>(), widgets);
	}

	public void addRow(List<String> tdStyles, List<Widget> widgets) {
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName("tr");

		if (isAutoNumber) {
			row.add(getTd(new InlineLabel((++count) + "")));
		}

		int i = 0;
		for (Widget widget : widgets) {
			Widget td = getTd(widget);
			if (tdStyles.size() > i) {
				if (!tdStyles.get(i).isEmpty())
					td.addStyleName(tdStyles.get(i));
			}
			row.add(td);
			++i;
		}
		panelBody.add(row);
	}

	public void addRow(RowWidget rowWidget) {
		rowWidget.setAutoNumber(isAutoNumber());
		rowWidget.setRowNumber(++count);
		panelBody.add(rowWidget);
	}

	public int getRowCount() {
		return panelBody.getWidgetCount();
	}

	public Widget getRow(int row) {
		if (panelBody.getWidgetCount() > row)
			return panelBody.getWidget(row);

		return null;
	}

	private Widget getTd(Widget widget) {
		HTMLPanel td = new HTMLPanel("");
		td.addStyleName("td");
		td.add(widget);
		return td;
	}

	public void setStriped(Boolean status) {
		if (status) {
			tblContainer.addStyleName("table-striped");
		} else {
			tblContainer.removeStyleName("table-striped");
		}
	}

	public void setSearchSectionVisible(Boolean status) {
		if (status) {
			tblContainer.removeStyleName("border-top");
			divSearch.setVisible(true);
		} else {
			tblContainer.addStyleName("border-top");
			divSearch.setVisible(false);
		}
	}

	public void setTownListVisible(Boolean status) {
		if (status) {
			divTownList.removeClassName("hide");
		} else {
			divTownList.addClassName("hide");
		}
	}

	public void setMemberCategoryVisible(Boolean status) {
		if (status) {
			divMemberCategory.removeClassName("hide");
		} else {
			divMemberCategory.addClassName("hide");
		}
	}

	public void setBordered(Boolean status) {
		if (status) {
			tblContainer.addStyleName("table-bordered");
		} else {
			tblContainer.removeStyleName("table-bordered");
		}
	}

	public void setHovered(Boolean status) {
		tblContainer.removeStyleName("table-hover");
		if (status) {
			tblContainer.addStyleName("table-hover");
		} else {
			tblContainer.removeStyleName("table-hover");
		}
	}

	public void setIsGrid(Boolean status) {
		if (status) {
			overalContainer.getElement().setAttribute("id", "grid");
		} else {
			overalContainer.getElement().removeAttribute("id");
		}
	}

	public void clearRows() {
		panelBody.clear();
		resetCount();
	}

	public boolean isAutoNumber() {
		return isAutoNumber;
	}

	public void setAutoNumber(boolean isAutoNumber) {
		this.isAutoNumber = isAutoNumber;
	}

	public void createFooter(RowWidget footer) {
		panelFooter.add(footer);
	}

	public void clearFooter() {
		panelFooter.clear();
	}

	public ActionLink getOfflineSQLButton() {
		return aDownloadOfflineSQL;
	}

	public void setFooter(List<Widget> widgets) {
		panelFooter.clear();
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName("tr");

		if (isAutoNumber) {
			row.add(getTd(new InlineLabel()));
		}

		for (Widget widget : widgets) {
			row.add(getTd(widget));
		}
		panelFooter.add(row);
	}

	public void resetCount() {
		count = 0;
	}

	public void createHeader(String name, String width, String labelStyle, String thStyle) {
		HTMLPanel th = new HTMLPanel("");
		th.addStyleName("th");
		if (thStyle != null) {
			th.addStyleName(thStyle);
		}
		if (width != null) {
			th.setWidth(width);
		}
		InlineLabel label = new InlineLabel(name);
		label.setTitle(name);
		if (labelStyle != null) {
			label.addStyleName(labelStyle);
		}
		th.add(label);
		panelHeader.add(th);
	}

	public void createHeader(String name, String width) {
		createHeader(name, width, null, null);
	}

	public void createHeader(String name) {
		createHeader(name, null);
	}

	public void insert(RowWidget rowWidget, int beforeIndex) {
		panelBody.insert(rowWidget, beforeIndex);
	}

	public void setNoRecords(Integer count) {
		NoTableRecord noRecord = new NoTableRecord();
		panelBody.add(noRecord);
		if (count >= 1) {
			noRecord.removeFromParent();
		}
	}

	public void setInitialDates(Date startDate, Date endDate) {
		dtStartDate.setValue(startDate);
		dtEndDate.setValue(endDate);
	}

	public ActionLink getDownloadPdf() {
		return aDownloadPdf;
	}

	public ActionLink getDownloadXls() {
		return aDownloadXls;
	}

	public ActionLink getAllTranscriptsButton() {
		return aDownloadAllCPDTranscripts;
	}

	public ActionLink getFilterButton() {
		return aFilter;
	}

	public String getSearchValue() {
		return txtSearch.getValue();
	}

	public HasValueChangeHandlers<String> getSearchValueChangeHander() {
		return txtSearch;
	}

	public HasKeyDownHandlers getSearchKeyDownHandler() {
		return txtSearch;
	}

	public DropDownList<ApplicationStatus> getLstApplicationStatus() {
		return lstApplicationStatus;
	}

	public DropDownList<PaymentStatus> getLstPaymentStatus() {
		return lstPaymentStatus;
	}

	public ActionLink getaSearch() {
		return aSearch;
	}

	public class Towns implements Listable {
		private String townName;

		public Towns(String townName) {
			this.townName = townName;
		}

		@Override
		public String getName() {
			return townName;
		}

		@Override
		public String getDisplayName() {
			return townName;
		}
	}

	public HasClickHandlers getAdvancedFilter() {
		return aAdvancedSearch;
	}

	public DropDownList<PaymentMode> getLstPaymentMode() {
		return lstPaymentMode;
	}

	public DropDownList<PaymentType> getLstPaymentType() {
		return lstPaymentType;
	}

	public HasValueChangeHandlers<AccommodationDto> getAccomodationValueChangeHandler() {
		return lstAccomodation;
	}

	public HasValueChangeHandlers<BookingStatus> getBookingStatusValueChangeHandler() {
		return lstBookingStatus;
	}

	public DropDownList<BookingStatus> getLstBookingStatus() {
		return lstBookingStatus;
	}

	public DropDownList<AccommodationDto> getLstAccomodation() {
		return lstAccomodation;
	}

}