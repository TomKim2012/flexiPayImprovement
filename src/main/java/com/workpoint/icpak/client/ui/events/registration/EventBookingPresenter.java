package com.workpoint.icpak.client.ui.events.registration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.delegates.client.ResourceDelegate;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.workpoint.icpak.client.place.NameTokens;
import com.workpoint.icpak.client.service.AbstractAsyncCallback;
import com.workpoint.icpak.shared.api.CountriesResource;
import com.workpoint.icpak.shared.api.EventsResource;
import com.workpoint.icpak.shared.api.InvoiceResource;
//import com.workpoint.icpak.shared.api.EventsResource;
import com.workpoint.icpak.shared.model.Country;
import com.workpoint.icpak.shared.model.InvoiceDto;
import com.workpoint.icpak.shared.model.events.BookingDto;
import com.workpoint.icpak.shared.model.events.EventDto;

//import com.workpoint.icpak.shared.model.events.BookingDto;

public class EventBookingPresenter extends
		Presenter<EventBookingPresenter.MyView, EventBookingPresenter.MyProxy> {

	public interface MyView extends View {
		boolean isValid();

		void setCountries(List<Country> countries);

		Anchor getANext();

		int getCounter();

		void next();

		void addError(String error);

		void setEvent(EventDto event);

		BookingDto getBooking();

		void bindBooking(BookingDto booking);

		void setMiddleHeight();

		void bindInvoice(InvoiceDto invoice);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.eventBooking)
	@NoGatekeeper
	public interface MyProxy extends ProxyPlace<EventBookingPresenter> {
	}

	@Inject
	PlaceManager placeManager;

	private String eventId;
	private String bookingId;

	private ResourceDelegate<CountriesResource> countriesResource;

	private ResourceDelegate<EventsResource> eventsResource;

	private ResourceDelegate<InvoiceResource> invoiceResource;

	@Inject
	public EventBookingPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy,
			ResourceDelegate<CountriesResource> countriesResource,
			ResourceDelegate<EventsResource> eventsResource,
			ResourceDelegate<InvoiceResource> invoiceResource) {
		super(eventBus, view, proxy);
		this.countriesResource = countriesResource;
		this.eventsResource = eventsResource;
		this.invoiceResource = invoiceResource;
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		getView().getANext().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (getView().isValid()) {

					if (getView().getCounter() == 1) {

						// User has selected a category and clicked submit
						BookingDto dto = getView().getBooking();
						dto.setEventRefId(eventId);
						submit(dto);

						// We navigate next after server side has generated an
						// account and submitted an email to user.
					} else {
						getView().next();
					}

				} else if (getView().getCounter() == 1) {
					getView().addError("Kindly specify at least one delegate");
				}

			}
		});
	}

	protected void submit(BookingDto dto) {
		if (bookingId != null) {

			eventsResource
					.withCallback(new AbstractAsyncCallback<BookingDto>() {
						@Override
						public void onSuccess(BookingDto booking) {
							bindBooking(booking);							
						}
					}).bookings(eventId).update(bookingId, dto);

		} else {
			eventsResource
					.withCallback(new AbstractAsyncCallback<BookingDto>() {
						@Override
						public void onSuccess(BookingDto booking) {
							bindBooking(booking);
						}
					}).bookings(eventId).create(dto);
		}
	}

	protected void bindBooking(BookingDto booking) {
		bookingId = booking.getRefId();
		getView().bindBooking(booking);
		getInvoice(booking.getInvoiceRef());
	}

	@Override
	public void prepareFromRequest(PlaceRequest request) {
		super.prepareFromRequest(request);
		eventId = request.getParameter("eventId", "9J4oKtW898RyOClP");
		bookingId = request.getParameter("bookingId", null);

		countriesResource.withCallback(
				new AbstractAsyncCallback<List<Country>>() {
					public void onSuccess(List<Country> countries) {
						Collections.sort(countries, new Comparator<Country>() {
							@Override
							public int compare(Country o1, Country o2) {
								return o1.getDisplayName().compareTo(
										o2.getDisplayName());
							}
						});

						getView().setCountries(countries);
					};
				}).getAll();

		eventsResource.withCallback(new AbstractAsyncCallback<EventDto>() {
			@Override
			public void onSuccess(EventDto event) {
				// Window.alert(">>>>>>>>>>>>Set event called" +
				// event.getName());
				getView().setEvent(event);
			}

			@Override
			public void onFailure(Throwable caught) {
				// Window.alert(">>>>>>>>Failure on loading data!!!!"
				// + caught.getMessage());
				// Window.alert(caught.getClass() + "<br>"
				// + caught.getStackTrace());

				StringBuffer buffer = new StringBuffer();
				for (StackTraceElement elem : caught.getStackTrace()) {
					buffer.append(elem.getLineNumber() + ">>"
							+ elem.getClassName() + ">>" + elem.getMethodName());
				}

				Window.alert(caught+" "+caught.getMessage() +" "+caught.getStackTrace().toString());

				super.onFailure(caught);
			}
		}).getById(eventId);

		if (bookingId != null) {
			eventsResource
					.withCallback(new AbstractAsyncCallback<BookingDto>() {
						@Override
						public void onSuccess(BookingDto booking) {
							getView().bindBooking(booking);
						}
					}).bookings(eventId).getById(bookingId);
		}

	}
	
	protected void getInvoice(String invoiceRef) {

		invoiceResource.withCallback(new AbstractAsyncCallback<InvoiceDto>() {
			@Override
			public void onSuccess(InvoiceDto invoice) {
				getView().bindInvoice(invoice);
				getView().next();
			}
		}).getInvoice(invoiceRef);

	}

	@Override
	protected void onReset() {
		super.onReset();
		getView().setMiddleHeight();
	}

}
