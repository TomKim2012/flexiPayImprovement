package com.workpoint.icpak.client.ui.cpd.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.workpoint.icpak.client.place.NameTokens;
import com.workpoint.icpak.client.ui.component.ActionLink;
import com.workpoint.icpak.client.ui.component.PagingPanel;
import com.workpoint.icpak.client.ui.cpd.admin.summary.table.CPDSummaryTable;
import com.workpoint.icpak.client.ui.cpd.admin.summary.table.row.CPDSummaryTableRow;
import com.workpoint.icpak.client.ui.cpd.admin.table.CPDAdminTable;
import com.workpoint.icpak.client.ui.cpd.admin.table.row.CPDAdminTableRow;
import com.workpoint.icpak.client.ui.cpd.form.RecordCPD;
import com.workpoint.icpak.client.ui.cpd.header.CPDHeader;
import com.workpoint.icpak.client.ui.cpd.member.table.CPDMemberTable;
import com.workpoint.icpak.client.ui.cpd.member.table.footer.CPDMemberFooterRow;
import com.workpoint.icpak.client.ui.cpd.member.table.row.CPDMemberTableRow;
import com.workpoint.icpak.client.ui.util.DateRange;
import com.workpoint.icpak.client.ui.util.DateUtils;
import com.workpoint.icpak.client.ui.util.NumberUtils;
import com.workpoint.icpak.shared.model.CPDDto;
import com.workpoint.icpak.shared.model.CPDFooterDto;
import com.workpoint.icpak.shared.model.CPDSummaryDto;
import com.workpoint.icpak.shared.model.Listable;
import com.workpoint.icpak.shared.model.MemberCPDDto;
import com.workpoint.icpak.shared.model.MemberDto;

public class CPDManagementView extends ViewImpl implements CPDManagementPresenter.ICPDManagementView {

	private final Widget widget;
	@UiField
	HTMLPanel container;
	@UiField
	CPDAdminTable tblCPDReturns;
	@UiField
	CPDAdminTable tblCpdArchive;
	@UiField
	SpanElement spnArchiveCount;
	@UiField
	SpanElement spnReturnCount;
	@UiField
	ActionLink aBack;

	@UiField
	RecordCPD frmViewReturns;
	@UiField
	RecordCPD frmViewArchive;
	@UiField
	CPDHeader headerContainer;
	@UiField
	LIElement licpdReturns;
	@UiField
	LIElement liMemberCPD;
	@UiField
	LIElement liReturnArchive;
	@UiField
	Anchor aCPDReturns;
	@UiField
	Anchor aMemberCPD;
	@UiField
	Anchor aReturnArchive;
	@UiField
	ActionLink aCreate;
	@UiField
	DivElement divCPDReturns;
	@UiField
	DivElement divMemberTable;
	@UiField
	DivElement divCPDArchive;
	@UiField
	CPDSummaryTable tblSummaryTable;
	@UiField
	CPDMemberTable tblMemberTable;
	@UiField
	DivElement panelBreadcrumb;
	@UiField
	SpanElement spnMemberName;

	public interface Binder extends UiBinder<Widget, CPDManagementView> {
	}

	@Inject
	public CPDManagementView(final Binder binder) {
		widget = binder.createAndBindUi(this);

		headerContainer.setTitles("Total CPD Requests", "Total Processed", "Total Pending");

		aBack.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// History.back();
			}
		});

	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	public class Year implements Listable {
		private Date pickDate;

		public Year(Date passedDate) {
			pickDate = passedDate;
		}

		@Override
		public String getName() {
			return DateUtils.YEARFORMAT.format(pickDate);
		}

		@Override
		public String getDisplayName() {
			return DateUtils.YEARFORMAT.format(pickDate);
		}

	}

	@Override
	public void bindResults(List<CPDDto> result, String loadType) {
		tblCPDReturns.clearRows();
		tblCpdArchive.clearRows();

		if (loadType.equals("ALLRETURNS") || loadType.equals("cpdReturns")) {
			for (CPDDto dto : result) {
				tblCPDReturns.createRow(new CPDAdminTableRow(dto));
			}

		} else if (loadType.equals("ALLARCHIVE") || loadType.equals("returnArchive")) {
			for (CPDDto dto : result) {
				tblCpdArchive.createRow(new CPDAdminTableRow(dto));
			}
		}
	}

	@Override
	public void showDetailedView() {

	}

	@Override
	public PagingPanel getReturnsPagingPanel() {
		return tblCPDReturns.getPagingPanel();
	}

	@Override
	public PagingPanel getArchivePagingPanel() {
		return tblCpdArchive.getPagingPanel();
	}

	@Override
	public PagingPanel getCPDSummaryPagingPanel() {
		return tblSummaryTable.getPagingPanel();
	}

	@Override
	public void bindSummary(CPDSummaryDto summary) {
		spnReturnCount.setInnerText(NumberUtils.NUMBERFORMAT.format(summary.getTotalReturns()) + "");
		spnArchiveCount.setInnerText(NumberUtils.NUMBERFORMAT.format(summary.getTotalArchive()) + "");
	}

	@Override
	public void setInitialDates(DateRange thisYear, Date endDate) {
		tblCPDReturns.setInitialDates(DateUtils.getDateByRange(thisYear, true), endDate);
		tblMemberTable.setInitialDates(DateUtils.getDateByRange(thisYear, true), endDate);
	}

	@Override
	public HasClickHandlers getFilterButton() {
		return tblCPDReturns.getFilterButton();
	}

	@Override
	public Date getEndDate() {
		return tblCPDReturns.getEndDate();
	}

	@Override
	public Date getStartDate() {
		return tblCPDReturns.getStartDate();
	}

	@Override
	public HasValueChangeHandlers<String> getReturnsSearchValueChangeHander() {
		return tblCPDReturns.getSearchField();
	}

	@Override
	public HasValueChangeHandlers<String> getSummarySearchValueChangeHander() {
		return tblMemberTable.getSearchField();
	}

	@Override
	public String getReturnsSearchValue() {
		return tblCPDReturns.getSearchValue();
	}

	@Override
	public HasKeyDownHandlers getTxtSearch() {
		return tblCPDReturns.getSearchKeyDownHandler();
	}

	@Override
	public HasKeyDownHandlers getMemberSummaryKeyDownHandler() {
		return tblSummaryTable.getSearchKeyDownHandler();
	}

	@Override
	public String getMemberSummaryTxtSearch() {
		return tblSummaryTable.getSearchValue();
	}

	public CPDMemberTable getMemberSummaryTable() {
		return tblMemberTable;
	}

	@Override
	public void setTab(String page, String refId) {
		resetAllTabs();
		if (page.equals("cpdReturns")) {
			licpdReturns.addClassName("active");
			divCPDReturns.addClassName("active");

			if (!refId.isEmpty()) {
				frmViewReturns.removeStyleName("hide");
				tblCPDReturns.addStyleName("hide");
			} else {
				frmViewReturns.addStyleName("hide");
				tblCPDReturns.removeStyleName("hide");
			}
		} else if (page.equals("memberCPD")) {
			liMemberCPD.addClassName("active");
			divMemberTable.addClassName("active");

			if (!refId.isEmpty()) {
				tblMemberTable.removeStyleName("hide");
				tblSummaryTable.addStyleName("hide");
				panelBreadcrumb.removeClassName("hide");
			} else {
				tblMemberTable.addStyleName("hide");
				tblSummaryTable.removeStyleName("hide");
				panelBreadcrumb.addClassName("hide");
			}

		} else if (page.equals("returnArchive")) {
			liReturnArchive.addClassName("active");
			divCPDArchive.addClassName("active");

			if (!refId.isEmpty()) {
				frmViewArchive.removeStyleName("hide");
				tblCpdArchive.addStyleName("hide");
			} else {
				frmViewArchive.addStyleName("hide");
				tblCpdArchive.removeStyleName("hide");
			}

		}
	}

	public void resetAllTabs() {
		licpdReturns.removeClassName("active");
		liMemberCPD.removeClassName("active");
		liReturnArchive.removeClassName("active");

		divCPDReturns.removeClassName("active");
		divMemberTable.removeClassName("active");
		divCPDArchive.removeClassName("active");
	}

	@Override
	public void bindSingleResults(CPDDto result, String loadType) {
		if (loadType.equals("cpdReturns")) {
			frmViewReturns.showForm(true);
			frmViewReturns.setCPD(result);
			frmViewReturns.setAdminMode(true);
			frmViewReturns.setBackHref("#cpdmgt;p=cpdReturns");
			frmViewReturns.setMemberName(result.getFullNames() + " - " + result.getMemberRegistrationNo());
		} else if (loadType.equals("returnArchive")) {
			frmViewArchive.showForm(true);
			frmViewArchive.setCPD(result);
			frmViewArchive.setAdminMode(true);
			frmViewArchive.setBackHref("#cpdmgt;p=returnArchive");
			frmViewArchive.setMemberName(result.getFullNames() + " - " + result.getMemberRegistrationNo());
		}
	}

	@Override
	public void bindMemberSummary(List<MemberCPDDto> result) {
		tblSummaryTable.clearRows();
		for (MemberCPDDto memberCPD : result) {
			tblSummaryTable.createRow(new CPDSummaryTableRow(memberCPD));
		}
	}

	@Override
	public void bindIndividualCPDFooter(List<CPDFooterDto> result) {
		tblMemberTable.clearFooter();
		for (CPDFooterDto footer : result) {
			tblMemberTable.createFooter(new CPDMemberFooterRow(footer));
		}
	}

	@Override
	public void bindIndividualResults(List<CPDDto> result) {
		tblMemberTable.clearRows();
		tblMemberTable.setNoRecords(result.size());
		for (CPDDto dto : result) {
			tblMemberTable.createRow(new CPDMemberTableRow(dto));
		}
	}

	@Override
	public PagingPanel getIndividualMemberPagingPanel() {
		return tblMemberTable.getPagingPanel();
	}

	@Override
	public void setIndividualMemberInitialDates(Date startDate, Date endDate) {
		tblMemberTable.setInitialDates(startDate, endDate);
	}

	public HasClickHandlers getRecordButton() {
		return aCreate;
	}

	@Override
	public HasKeyDownHandlers getArchiveSummaryKeyDownHandler() {
		return tblCpdArchive.getSearchKeyDownHandler();
	}

	@Override
	public String getArchiveSummaryTxtSearch() {
		return tblCpdArchive.getSearchValue();
	}

	public void setMemberFullNames(String fullNames) {
		spnMemberName.setInnerText(fullNames);
	}

	public HasClickHandlers getBackButton() {
		return aBack;
	}

	@Override
	public void bindMemberDetails(MemberDto member) {
		if (member != null) {
			spnMemberName.setInnerText(member.getFullName() + " - " + member.getMemberNo());
		}
	}

}
