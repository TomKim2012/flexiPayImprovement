package com.workpoint.icpak.client.ui;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.common.client.IndirectProvider;
import com.gwtplatform.common.client.StandardProvider;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.workpoint.icpak.client.service.ServiceCallback;
import com.workpoint.icpak.client.ui.admin.AdminHomePresenter;
import com.workpoint.icpak.client.ui.error.ErrorPresenter;
import com.workpoint.icpak.client.ui.events.AdminPageLoadEvent;
import com.workpoint.icpak.client.ui.events.ClientDisconnectionEvent;
import com.workpoint.icpak.client.ui.events.ClientDisconnectionEvent.ClientDisconnectionHandler;
import com.workpoint.icpak.client.ui.events.ErrorEvent;
import com.workpoint.icpak.client.ui.events.ErrorEvent.ErrorHandler;
import com.workpoint.icpak.client.ui.events.ProcessingCompletedEvent;
import com.workpoint.icpak.client.ui.events.ProcessingCompletedEvent.ProcessingCompletedHandler;
import com.workpoint.icpak.client.ui.events.ProcessingEvent;
import com.workpoint.icpak.client.ui.events.ProcessingEvent.ProcessingHandler;
import com.workpoint.icpak.client.ui.header.HeaderPresenter;
import com.workpoint.icpak.client.ui.upload.attachment.ShowAttachmentEvent;
import com.workpoint.icpak.client.ui.upload.attachment.ShowAttachmentEvent.ShowAttachmentHandler;
import com.workpoint.icpak.client.ui.upload.href.IFrameDataPresenter;

public class MainPagePresenter extends
		Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy>
		implements ErrorHandler, ProcessingCompletedHandler, ProcessingHandler,
		ShowAttachmentHandler, ClientDisconnectionHandler {

	public interface MyView extends View {

		void showProcessing(boolean processing, String message);

		void setAlertVisible(String subject, String action, String url);

		void showDisconnectionMessage(String message);

		void clearDisconnectionMsg();
	}

	@ProxyCodeSplit
	public interface MyProxy extends Proxy<MainPagePresenter> {
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>> HEADER_content = new Type<RevealContentHandler<?>>();

	@ContentSlot
	public static final Type<RevealContentHandler<?>> CONTENT_SLOT = new Type<RevealContentHandler<?>>();

	@Inject
	HeaderPresenter headerPresenter;

	IndirectProvider<ErrorPresenter> errorFactory;

	@Inject
	PlaceManager placeManager;

	@Inject
	IFrameDataPresenter presenter;

	@Inject
	public MainPagePresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, Provider<ErrorPresenter> provider) {
		super(eventBus, view, proxy);
		this.errorFactory = new StandardProvider<ErrorPresenter>(provider);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(ErrorEvent.TYPE, this);
		addRegisteredHandler(ProcessingEvent.TYPE, this);
		addRegisteredHandler(ProcessingCompletedEvent.TYPE, this);
		addRegisteredHandler(ShowAttachmentEvent.TYPE, this);
		addRegisteredHandler(ClientDisconnectionEvent.TYPE, this);
	}

	@Override
	protected void onReset() {
		super.onReset();
		setInSlot(HEADER_content, headerPresenter);
		getView().clearDisconnectionMsg();
		// System.err.println("Main Page - Reset called......");
	}

	@Override
	public void onError(final ErrorEvent event) {
		// addToPopupSlot(null);
		// Window.alert(event.getMessage() + "---" + event.getSource());

		errorFactory.get(new ServiceCallback<ErrorPresenter>() {
			@Override
			public void processResult(ErrorPresenter result) {
				String message = event.getMessage();

				result.setMessage(message, event.getId());

				// AppManager.showPopUp("Error Occured", result.asWidget(),
				// null,
				// "Cancel");
			}
		});
	}

	@Override
	public void setInSlot(Object slot, PresenterWidget<?> content) {
		super.setInSlot(slot, content);
		if (slot == CONTENT_SLOT) {
			if (content != null && content instanceof AdminHomePresenter) {
				fireEvent(new AdminPageLoadEvent(true));
			} else {
				fireEvent(new AdminPageLoadEvent(false));
			}
		}
	}

	@Override
	public void onProcessing(ProcessingEvent event) {
		getView().showProcessing(true, event.getMessage());
	}

	@Override
	public void onProcessingCompleted(ProcessingCompletedEvent event) {
		getView().showProcessing(false, null);
	}

	@Override
	public void onShowAttachment(ShowAttachmentEvent event) {
		Window.open(event.getUri(), "_blank", null);
	}

	@Override
	protected void onUnbind() {
		super.onUnbind();
		headerPresenter.unbind();
	}

	@Override
	public void onClientDisconnection(ClientDisconnectionEvent event) {
		getView().showDisconnectionMessage(event.getMessage());
	}

}
