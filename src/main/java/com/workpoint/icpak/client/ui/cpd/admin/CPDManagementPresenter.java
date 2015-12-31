package com.workpoint.icpak.client.ui.cpd.admin;

import static com.workpoint.icpak.client.ui.util.StringUtils.isNullOrEmpty;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.delegates.client.ResourceDelegate;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.workpoint.icpak.client.place.NameTokens;
import com.workpoint.icpak.client.security.CurrentUser;
import com.workpoint.icpak.client.service.AbstractAsyncCallback;
import com.workpoint.icpak.client.ui.admin.TabDataExt;
import com.workpoint.icpak.client.ui.component.PagingConfig;
import com.workpoint.icpak.client.ui.component.PagingLoader;
import com.workpoint.icpak.client.ui.component.PagingPanel;
import com.workpoint.icpak.client.ui.events.EditModelEvent;
import com.workpoint.icpak.client.ui.events.EditModelEvent.EditModelHandler;
import com.workpoint.icpak.client.ui.events.ProcessingCompletedEvent;
import com.workpoint.icpak.client.ui.events.ProcessingEvent;
import com.workpoint.icpak.client.ui.events.TableActionEvent;
import com.workpoint.icpak.client.ui.events.TableActionEvent.TableActionHandler;
import com.workpoint.icpak.client.ui.home.HomePresenter;
import com.workpoint.icpak.client.ui.popup.GenericPopupPresenter;
import com.workpoint.icpak.client.ui.security.AdminGateKeeper;
import com.workpoint.icpak.client.ui.security.LoginGateKeeper;
import com.workpoint.icpak.client.ui.util.DateRange;
import com.workpoint.icpak.client.ui.util.DateUtils;
import com.workpoint.icpak.shared.api.MemberResource;
import com.workpoint.icpak.shared.model.CPDDto;
import com.workpoint.icpak.shared.model.CPDSummaryDto;
import com.workpoint.icpak.shared.model.MemberCPDDto;
import com.workpoint.icpak.shared.model.TableActionType;

public class CPDManagementPresenter
		extends
		Presenter<CPDManagementPresenter.ICPDManagementView, CPDManagementPresenter.ICPDManagementProxy>
		implements EditModelHandler, TableActionHandler {

	public interface ICPDManagementView extends View {
		void bindResults(List<CPDDto> result, String loadType);

		void showDetailedView();

		PagingPanel getReturnsPagingPanel();

		void bindSummary(CPDSummaryDto summary);

		void setInitialDates(DateRange thisyear, Date date);

		HasClickHandlers getFilterButton();

		Date getStartDate();

		Date getEndDate();

		HasValueChangeHandlers<String> getSearchValueChangeHander();

		HasKeyDownHandlers getTxtSearch();

		String getSearchValue();

		void setTab(String page, String refId);

		void bindSingleResults(CPDDto result, String loadType);

		void bindMemberSummary(List<MemberCPDDto> result);

		PagingPanel getArchivePagingPanel();

		PagingPanel getCPDSummaryPagingPanel();

	}

	@ProxyCodeSplit
	@NameToken(NameTokens.cpdmgt)
	@UseGatekeeper(LoginGateKeeper.class)
	public interface ICPDManagementProxy extends
			TabContentProxyPlace<CPDManagementPresenter> {
	}

	@TabInfo(container = HomePresenter.class)
	static TabData getTabLabel(AdminGateKeeper adminGatekeeper) {
		TabDataExt data = new TabDataExt("C.P.D Management",
				"fa fa-graduation-cap", 5, adminGatekeeper, true);
		return data;
	}

	protected final ResourceDelegate<MemberResource> memberDelegate;
	protected final CurrentUser currentUser;

	ValueChangeHandler<String> cpdValueChangeHandler = new ValueChangeHandler<String>() {
		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			searchCPD(getView().getSearchValue().trim());
		}
	};

	KeyDownHandler keyHandler = new KeyDownHandler() {
		@Override
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				if (!isNullOrEmpty(getView().getSearchValue().trim())) {
					searchCPD(getView().getSearchValue().trim());
				}
			}
		}
	};
	protected String searchTerm = "";

	@Inject
	public CPDManagementPresenter(final EventBus eventBus,
			final ICPDManagementView view, final ICPDManagementProxy proxy,
			final ResourceDelegate<MemberResource> memberDelegate,
			final CurrentUser currentUser) {

		super(eventBus, view, proxy, HomePresenter.SLOT_SetTabContent);
		this.memberDelegate = memberDelegate;
		this.currentUser = currentUser;
	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(EditModelEvent.TYPE, this);
		addRegisteredHandler(TableActionEvent.TYPE, this);

		getView().getReturnsPagingPanel().setLoader(new PagingLoader() {
			@Override
			public void onLoad(int offset, int limit) {
				onLoad(offset, limit);
			}
		});

		getView().getFilterButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startDate = getView().getStartDate();
				endDate = getView().getEndDate();
				loadCPD(getView().getReturnsPagingPanel().getConfig()
						.getOffset(), getView().getReturnsPagingPanel()
						.getConfig().getLimit(), "CPDRETURNS");
			}
		});

		getView().getSearchValueChangeHander().addValueChangeHandler(
				cpdValueChangeHandler);

		getView().getTxtSearch().addKeyDownHandler(keyHandler);

		getView().getReturnsPagingPanel().setLoader(new PagingLoader() {
			@Override
			public void onLoad(int offset, int limit) {
				loadData(startDate, endDate, page);
			}
		});

		getView().getArchivePagingPanel().setLoader(new PagingLoader() {
			@Override
			public void onLoad(int offset, int limit) {
				loadData(startDate, endDate, page);
			}
		});

		getView().getCPDSummaryPagingPanel().setLoader(new PagingLoader() {
			@Override
			public void onLoad(int offset, int limit) {
				loadCPDSummaryData(searchTerm, offset, pageLimit);
			}
		});

	}

	@Inject
	GenericPopupPresenter popup;
	private Date startDate;
	private Date endDate;
	private String page;
	protected int pageLimit = 10;

	protected void saveRecord(CPDDto dto) {
		fireEvent(new ProcessingEvent());
		if (dto.getMemberRefId() != null) {
			memberDelegate.withCallback(new AbstractAsyncCallback<CPDDto>() {
				@Override
				public void onSuccess(CPDDto result) {
					fireEvent(new ProcessingCompletedEvent());
					History.back();
				}
			}).cpd(dto.getMemberRefId()).update(dto.getRefId(), dto);

		} else {
			Window.alert("MemberRefId cannot be null!");
		}

	}

	private void searchCPD(final String searchTerm) {
		fireEvent(new ProcessingEvent());
		memberDelegate.withCallback(new AbstractAsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer count) {
				fireEvent(new ProcessingCompletedEvent());
				PagingPanel pagingPanel = getView().getReturnsPagingPanel();
				pagingPanel.setTotal(count);
				PagingConfig pagingConfig = pagingPanel.getConfig();
				getCPDSearchResults(pagingConfig.getOffset(), pageLimit,
						searchTerm);
			}
		}).cpd("ALL").getCPDsearchCount(searchTerm);
	}

	private void getCPDSearchResults(int offset, int limit, String searchTerm) {
		fireEvent(new ProcessingEvent());
		memberDelegate
				.withCallback(new AbstractAsyncCallback<List<CPDDto>>() {
					@Override
					public void onSuccess(List<CPDDto> result) {
						// getView().bindResults(result);
						fireEvent(new ProcessingCompletedEvent());
					}
				})
				.cpd("ALL")
				.searchCPd(offset, limit, searchTerm,
						getView().getStartDate().getTime(),
						getView().getEndDate().getTime());
	}

	@Override
	protected void onReveal() {
		super.onReveal();

	}

	protected void loadData(Date startDate, Date endDate, final String loadType) {
		fireEvent(new ProcessingEvent());

		memberDelegate.withCallback(new AbstractAsyncCallback<CPDSummaryDto>() {
			@Override
			public void onSuccess(CPDSummaryDto summary) {
				getView().bindSummary(summary);
			}
		}).cpd("ALL").getCPDSummary(startDate.getTime(), endDate.getTime());

		memberDelegate.withCallback(new AbstractAsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer aCount) {
				fireEvent(new ProcessingCompletedEvent());
				PagingPanel panel = null;
				if (loadType.equals("ALLRETURNS")) {
					panel = getView().getReturnsPagingPanel();
					panel.setTotal(aCount);
				} else if (loadType.equals("ALLARCHIVE")) {
					panel = getView().getArchivePagingPanel();
					panel.setTotal(aCount);
				}
				PagingConfig config = panel.getConfig();
				loadCPD(config.getOffset(), pageLimit, loadType);
			}
		}).cpd(loadType).getCount(startDate.getTime(), endDate.getTime());

	}

	protected void loadCPD(int offset, int limit, final String loadType) {
		fireEvent(new ProcessingEvent());

		memberDelegate.withCallback(new AbstractAsyncCallback<List<CPDDto>>() {
			@Override
			public void onSuccess(List<CPDDto> result) {
				fireEvent(new ProcessingCompletedEvent());
				getView().bindResults(result, loadType);

			}
		}).cpd(loadType)
				.getAll(offset, limit, startDate.getTime(), endDate.getTime());
	}

	protected void loadCPD(String refId, final String loadType) {
		fireEvent(new ProcessingEvent());

		memberDelegate.withCallback(new AbstractAsyncCallback<CPDDto>() {
			@Override
			public void onSuccess(CPDDto result) {
				fireEvent(new ProcessingCompletedEvent());
				getView().bindSingleResults(result, loadType);
			}
		}).cpd("ALL").getById(refId);
	}

	String getApplicationRefId() {
		String applicationRefId = currentUser.getUser() == null ? null
				: currentUser.getUser().getApplicationRefId();
		return applicationRefId;
	}

	@Override
	public void prepareFromRequest(PlaceRequest request) {
		super.prepareFromRequest(request);
		page = request.getParameter("p", "cpdReturns");
		String refId = request.getParameter("refId", "");

		// Set Tab to Front End
		getView().setTab(page, refId);

		// If there is no refId - Load Table Data else load individual CPD
		// Record
		if (refId.isEmpty()) {
			getView().setInitialDates(DateRange.THISYEAR, new Date());
			this.startDate = DateUtils
					.getDateByRange(DateRange.THISYEAR, false);
			this.endDate = new Date();

			if (page.equals("cpdReturns")) {
				loadData(startDate, endDate, "ALLRETURNS");
			} else if (page.equals("returnArchive")) {
				loadData(startDate, endDate, "ALLARCHIVE");
			} else if (page.equals("memberCPD")) {
				loadCPDSummaryCount("");
			}
		} else {
			// Load Individual CPD
			loadCPD(refId, page);
		}
	}

	private void loadCPDSummaryCount(final String searchTerm) {
		fireEvent(new ProcessingEvent());
		memberDelegate.withCallback(new AbstractAsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer aCount) {
				fireEvent(new ProcessingCompletedEvent());
				PagingPanel panel = getView().getCPDSummaryPagingPanel();
				panel.setTotal(aCount);
				PagingConfig config = panel.getConfig();
				loadCPDSummaryData(searchTerm, config.getOffset(), pageLimit);
			}
		}).cpd("ALL").getMemberSummaryCount(searchTerm);
	}

	private void loadCPDSummaryData(String searchTerm, Integer offset,
			Integer limit) {
		fireEvent(new ProcessingEvent());
		memberDelegate
				.withCallback(new AbstractAsyncCallback<List<MemberCPDDto>>() {
					@Override
					public void onSuccess(List<MemberCPDDto> result) {
						getView().bindMemberSummary(result);
						fireEvent(new ProcessingCompletedEvent());
					}
				}).cpd("ALL").getAllMemberSummary(searchTerm, offset, limit);
	}

	@Override
	public void onEditModel(EditModelEvent event) {
	}

	@Override
	public void onTableAction(TableActionEvent event) {
		if (event.getAction() == TableActionType.APPROVECPD) {
			final CPDDto dto = (CPDDto) event.getModel();
			saveRecord(dto);
		}
	}

}
