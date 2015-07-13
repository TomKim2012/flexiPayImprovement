package com.workpoint.icpak.client.ui.users.save;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.workpoint.icpak.client.ui.AppManager;
import com.workpoint.icpak.shared.model.UserDto;
import com.workpoint.icpak.shared.model.UserGroup;

public class UserSavePresenter extends
		PresenterWidget<UserSavePresenter.IUserSaveView> {

	public interface IUserSaveView extends PopupView {

		void setType(TYPE type);

		HasClickHandlers getSaveUser();

		HasClickHandlers getSaveGroup();

		boolean isValid();

		UserDto getUser();

		void setUser(UserDto user);

		UserGroup getGroup();

		void setGroup(UserGroup group);

		void setGroups(List<UserGroup> groups);

		PopupPanel getPopUpPanel();

	}

	public enum TYPE {
		GROUP, USER
	}

	TYPE type;

	UserDto user;

	UserGroup group;

	@Inject
	public UserSavePresenter(final EventBus eventBus, final IUserSaveView view) {
		super(eventBus, view);
	}

	@Override
	protected void onBind() {
		super.onBind();

		getView().getSaveUser().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (getView().isValid()) {
					UserDto User = getView().getUser();
					// if(user!=null){
					// User.setId(user.getId());
					// }
					// SaveUserRequest request = new SaveUserRequest(User);
					// requestHelper.execute(request, new
					// TaskServiceCallback<SaveUserResponse>() {
					// @Override
					// public void processResult(SaveUserResponse result) {
					// user = result.getUser();
					// getView().setUser(user);
					// getView().hide();
					// fireEvent(new LoadUsersEvent());
					// }
					// });
				}
			}
		});

		getView().getSaveGroup().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (getView().isValid()) {
					UserGroup userGroup = getView().getGroup();

					// SaveGroupRequest request = new
					// SaveGroupRequest(userGroup);
					//
					// requestHelper.execute(request, new
					// TaskServiceCallback<SaveGroupResponse>() {
					// @Override
					// public void processResult(SaveGroupResponse result) {
					// group = result.getGroup();
					// getView().setGroup(group);
					// fireEvent(new LoadGroupsEvent());
					// getView().hide();
					// }
					// });
				}
			}
		});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		// GetGroupsRequest request = new GetGroupsRequest();
		// requestHelper.execute(request, new
		// TaskServiceCallback<GetGroupsResponse>() {
		// @Override
		// public void processResult(GetGroupsResponse result) {
		// List<UserGroup> groups = result.getGroups();
		// getView().setGroups(groups);
		// }
		// });
	}

	public void center() {
		int[] position = AppManager.calculatePosition(20, 45);
		getView().getPopUpPanel().setPopupPosition(position[1], position[0]);
	}

	public void setType(TYPE type, Object value) {
		this.type = type;
		getView().setType(type);
		if (value != null) {
			if (type == TYPE.USER) {
				user = (UserDto) value;
				getView().setUser(user);
			} else {
				group = (UserGroup) value;
				getView().setGroup(group);
			}
		}

	}
}