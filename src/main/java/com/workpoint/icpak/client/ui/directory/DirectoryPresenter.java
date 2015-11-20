package com.workpoint.icpak.client.ui.directory;

import java.util.List;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.delegates.client.ResourceDelegate;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.workpoint.icpak.client.ui.component.DropDownList;
import com.workpoint.icpak.client.ui.component.PagingConfig;
import com.workpoint.icpak.client.ui.component.PagingLoader;
import com.workpoint.icpak.client.ui.component.PagingPanel;
import com.workpoint.icpak.client.ui.component.TableView;
import com.workpoint.icpak.client.ui.component.TableView.Towns;
import com.workpoint.icpak.client.ui.events.ProcessingEvent;
import com.workpoint.icpak.client.ui.events.TableActionEvent;
import com.workpoint.icpak.client.ui.events.TableActionEvent.TableActionHandler;
import com.workpoint.icpak.shared.api.DirectoryResource;
import com.workpoint.icpak.shared.model.DirectoryDto;
import com.workpoint.icpak.client.place.NameTokens;
import com.workpoint.icpak.client.service.AbstractAsyncCallback;

public class DirectoryPresenter
		extends Presenter<DirectoryPresenter.MyDirectoryView, DirectoryPresenter.MyDirectoryProxy>
		implements TableActionHandler {
	public interface MyDirectoryView extends View {

		void bindResults(List<DirectoryDto> result);

		PagingPanel getPagingPanel();

		HasValueChangeHandlers<String> getSearchValueChangeHander();
		
		DropDownList<Towns> getTownList();

		String getSearchValue();

		String getTownName();

	}

	protected final ResourceDelegate<DirectoryResource> directoryResourceDelegate;

	@ProxyCodeSplit
	@NameToken(NameTokens.directory)
	@NoGatekeeper
	public interface MyDirectoryProxy extends ProxyPlace<DirectoryPresenter> {
	}

	ValueChangeHandler<String> directoryValueChangeHandler = new ValueChangeHandler<String>() {
		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			searchDirectory(getView().getSearchValue().trim());
		}
	};
	
	@Inject
	DirectoryPresenter(EventBus eventBus, MyDirectoryView view,
			ResourceDelegate<DirectoryResource> directoryResourceDelegate, MyDirectoryProxy proxy) {
		super(eventBus, view, proxy);
		this.directoryResourceDelegate = directoryResourceDelegate;

	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(TableActionEvent.TYPE, this);
		getView().getPagingPanel().setLoader(new PagingLoader() {
			@Override
			public void onLoad(int offset, int limit) {
				loadDirectory(offset, limit);
			}
		});
		getView().getSearchValueChangeHander().addValueChangeHandler(directoryValueChangeHandler);
		getView().getTownList().addValueChangeHandler(new ValueChangeHandler<TableView.Towns>() {

			@Override
			public void onValueChange(ValueChangeEvent<Towns> towns) {
				searchDirectory(getView().getTownName().trim());
			}
		});
	}

	@Override
	public void onTableAction(TableActionEvent event) {

	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadCount();
	}

	private void loadCount() {
		directoryResourceDelegate.withCallback(new AbstractAsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				PagingPanel panel = getView().getPagingPanel();
				panel.setTotal(result);
				PagingConfig config = panel.getConfig();

				loadDirectory(config.getOffset(), config.getLimit());
			}

		}).getCount();
	}

	private void loadDirectory(int offset, int limit) {

		directoryResourceDelegate.withCallback(new AbstractAsyncCallback<List<DirectoryDto>>() {

			@Override
			public void onSuccess(List<DirectoryDto> results) {
				getView().bindResults(results);
			}

		}).getAll(offset, limit);
	}

	public void searchDirectory(final String searchTerm) {
		fireEvent(new ProcessingEvent());
		directoryResourceDelegate.withCallback(new AbstractAsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				PagingPanel panel = getView().getPagingPanel();
				panel.setTotal(result);
				PagingConfig config = panel.getConfig();
				searchDirectory(searchTerm, config.getOffset(), config.getLimit());
			}
		}).getSearchCount(searchTerm);
	}

	public void searchDirectory(String searchTerm, int offset, int limit) {
		directoryResourceDelegate.withCallback(new AbstractAsyncCallback<List<DirectoryDto>>() {

			@Override
			public void onSuccess(List<DirectoryDto> results) {
				getView().bindResults(results);
			}

		}).search(searchTerm, offset, limit);
	}

	private String[] towns = { "all", "Nairobi", "BONDO", "Bungoma", "BURU BURU, NAIROBI", "Busia", "City Square",
			"Eldoret", "EMBU", "GPO NAIROBI", "Juba", "KAKAMEGA", "Kapsabet", "Karatina", "Kericho", "Kerugoya",
			"KIAMBU", "Kigali", "Kigali, Rwanda", "KISERIAN", "Kisii", "Kisumu", "Kitale", "Kitui", "KNH", "Lamu",
			"Limuru", "Luanda", "Machakos", "Malindi", "Meru", "Mombasa", "Mumias", "Murang'a", "Muranga", "Nairobi",
			"Nairobi,Kenya", "Naivasha", "Nakuru", "Nanyuki", "Naro Moru", "NGARA NAIROBI", "Niarobi", "Nyahururu",
			"Nyamira", "Nyeri", "OLKALOU", "RUARAKA, NAIROBI", "Ruaraka- Nairobi", "SARIT CENTRE", "Siaya", "SOTIK",
			"Suna", "Tala", "Thika", "Ugunja", "Vienna Australia", "Voi", "Webuye" };
}