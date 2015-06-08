package com.workpoint.icpak.client.ui.eventsandseminars;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;
import com.workpoint.icpak.client.place.NameTokens;
import com.workpoint.icpak.client.ui.admin.TabDataExt;
import com.workpoint.icpak.client.ui.home.HomePresenter;
import com.workpoint.icpak.client.ui.security.LoginGateKeeper;

public class EventsPresenter extends
		Presenter<EventsPresenter.IEventsView, EventsPresenter.IEventsProxy> {

	public interface IEventsView extends View {

	}

	@ProxyCodeSplit
	@NameToken(NameTokens.events)
	@UseGatekeeper(LoginGateKeeper.class)
	public interface IEventsProxy extends TabContentProxyPlace<EventsPresenter> {
	}

	
	@TabInfo(container = HomePresenter.class)
	static TabData getTabLabel(LoginGateKeeper adminGatekeeper) {
		TabDataExt data = new TabDataExt("Events and Seminars", "fa fa-tags",
				2, adminGatekeeper, true);
		return data;
	}

	@Inject
	public EventsPresenter(final EventBus eventBus, final IEventsView view,
			final IEventsProxy proxy) {
		super(eventBus, view, proxy, HomePresenter.SLOT_SetTabContent);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}

	protected void save() {
	}

	@Override
	protected void onReset() {
		super.onReset();
	}

}
