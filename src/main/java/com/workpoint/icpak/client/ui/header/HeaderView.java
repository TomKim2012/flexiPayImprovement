package com.workpoint.icpak.client.ui.header;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.workpoint.icpak.client.ui.component.ActionLink;
import com.workpoint.icpak.client.ui.events.ToggleSideBarEvent;
import com.workpoint.icpak.client.util.AppContext;
import com.workpoint.icpak.shared.model.UserDto;

public class HeaderView extends ViewImpl implements HeaderPresenter.IHeaderView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, HeaderView> {
	}

	boolean isSelected = false;

	@UiField
	Anchor aLogout;
	@UiField
	ActionLink aToggleSideBar;
	@UiField
	SpanElement spnAlertMessage;
	@UiField
	SpanElement spnLoggedInUser;
	@UiField
	DivElement divAlert;
	@UiField
	Element imgLogo;

	boolean isShown = false;
	static int hideAlertInterval = 1000 * 5; // 5 secs

	Timer timer = new Timer() {
		@Override
		public void run() {
			if (isShown) {
				hideAlert();
			}
		}
	};

	@Inject
	public HeaderView(final Binder binder) {
		widget = binder.createAndBindUi(this);

		aToggleSideBar.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AppContext.fireEvent(new ToggleSideBarEvent(true));
			}
		});
	}

	protected void hideAlert() {
		divAlert.addClassName("hide");
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	public HasClickHandlers getALogout() {
		return aLogout;
	}

	@Override
	public void setLoggedInUser(UserDto currentUser) {
		String firstName = "";

		if (currentUser.getSurname() != null) {
			firstName = currentUser.getSurname();
		} else {
			String[] names = currentUser.getFullName().split(" ");
			if (names.length > 2) {
				firstName = names[0] + " " + names[1];
			}
		}

		spnLoggedInUser.setInnerText("Welcome "
				+ firstName
				+ (AppContext.isCurrentUserMember() ? " (m/No: "
						+ currentUser.getMemberNo() + ")" : ""));
	}

	public void showPopUpMessage(String message) {
		showPopUpMessage(message, true);
	}

	public void showPopUpMessage(String message, boolean isSuccess) {
		isShown = true;
		divAlert.removeClassName("hide");
		String html = "";
		if (isSuccess) {
			html = "<span class='fa fa-ok'></span> ";
			html = html + message;
			divAlert.removeClassName("alert-danger");
			divAlert.addClassName("alert-success");
		} else {
			html = "<span class='fa fa-exclamation-circle' aria-hidden='true'></span> ";
			html = html + message;
			divAlert.removeClassName("alert-success");
			divAlert.addClassName("alert-danger");
		}
		spnAlertMessage.setInnerHTML(html);
		timer.schedule(hideAlertInterval);
	}

	@Override
	public void showSmallLogo(String message) {
		if (message.equals("show")) {
			imgLogo.addClassName("img-small");
		} else {
			imgLogo.removeClassName("img-small");
		}
	}

}
