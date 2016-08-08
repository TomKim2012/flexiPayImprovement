package com.workpoint.icpak.client.ui.payment.collective.collectivepayments;

import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.workpoint.icpak.client.ui.component.ActionLink;
import com.workpoint.icpak.client.ui.profile.paysubscription.PaymentSubscription;

public class CollectivePaymentsView extends ViewImpl implements CollectivePaymentsPresenter.MyView {
	interface Binder extends UiBinder<Widget, CollectivePaymentsView> {
	}

	@UiField
	PaymentSubscription divPaymentWidget;
	@UiField
	HTMLPanel panelSubscription;
	@UiField
	HTMLPanel panelWizardContainer;

	@UiField
	HTMLPanel panelPayment;
	@UiField
	ActionLink aProceed;
	@UiField
	ActionLink aBackToPage1;

	@UiField
	Element elBackHeading;

	@Inject
	CollectivePaymentsView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
		divPaymentWidget.showCollectivePaymentSection(true);
		// showSubscriptionPanel(false);
		aBackToPage1.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSubscriptionPanel(true);
			}
		});

	}

	public HasClickHandlers getProceedButton() {
		return aProceed;
	}

	public PaymentSubscription getSubscriptionWidget() {
		return divPaymentWidget;
	}

	@Override
	public void setInSlot(Object slot, IsWidget content) {
		if (slot == CollectivePaymentsPresenter.PAYMENTS_SLOT) {
			panelPayment.clear();
			if (content != null) {
				panelPayment.add(content);
			}
			super.setInSlot(slot, content);
		}
	}

	@Override
	public void showSubscriptionPanel(boolean show) {
		if (show) {
			panelSubscription.removeStyleName("hide");
			panelPayment.addStyleName("hide");
			elBackHeading.addClassName("hide");
			panelWizardContainer.setStyleName("col-md-6 col-md-offset-3 wizard-container set-full-height");
		} else {
			panelPayment.removeStyleName("hide");
			panelSubscription.addStyleName("hide");
			elBackHeading.removeClassName("hide");
			panelWizardContainer.setStyleName("col-md-8 col-md-offset-2 wizard-container set-full-height");
		}
	}

}