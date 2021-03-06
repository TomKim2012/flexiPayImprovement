package com.icpak.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.icpak.rest.dao.helper.BookingsDaoHelper;
import com.icpak.rest.models.event.Booking;
import com.sun.jersey.api.core.HttpContext;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workpoint.icpak.shared.api.BookingsResource;
import com.workpoint.icpak.shared.model.events.BookingDto;
import com.workpoint.icpak.shared.model.events.BookingSummaryDto;
import com.workpoint.icpak.shared.model.events.DelegateDto;

@Api(value = "", description = "Handles CRUD for event Bookings")
public class BookingsResourceImpl implements BookingsResource {

	@Inject
	BookingsDaoHelper helper;

	private final String eventId;
	@Context
	HttpContext httpContext;

	@Inject
	public BookingsResourceImpl(@Assisted String eventId) {
		this.eventId = eventId;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Retrieve all active bookings")
	public List<BookingDto> getAll(@ApiParam(value = "Starting point to fetch") @QueryParam("offset") Integer offset,
			@ApiParam(value = "No of Items to fetch") @QueryParam("limit") Integer limit) {
		String uri = "";
		List<BookingDto> dtos = helper.getAllBookings(uri, eventId, offset, limit, "");
		return dtos;
	}

	@GET
	@Path("/count")
	public Integer getCount() {
		return helper.getCount(eventId);
	}

	@GET
	@Path("/{bookingId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Get a booking by bookingId", response = Booking.class, consumes = MediaType.APPLICATION_JSON)
	public BookingDto getById(
			@ApiParam(value = "Booking Id of the booking to fetch", required = true) @PathParam("bookingId") String bookingId) {

		String uri = "";
		BookingDto dto = helper.getBookingById(eventId, bookingId);
		return dto;
	}

	@GET
	@Path("/summary/{eventRefId}/{isRefresh}")
	@Produces(MediaType.APPLICATION_JSON)
	public BookingSummaryDto getBookingSummary(@PathParam("eventRefId") String eventRefId,
			@PathParam("isRefresh") String isRefresh) {
		BookingSummaryDto dto = helper.getBookingStats(eventRefId, isRefresh);
		return dto;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create a new booking", response = Booking.class, consumes = MediaType.APPLICATION_JSON)
	public BookingDto create(BookingDto dto) {
		String uri = "";
		helper.createBooking(eventId, dto);
		uri = uri + "/" + dto.getRefId();
		dto.setUri(uri);
		return dto;
	}

	@POST
	@Path("/instantBooking")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Make Payment for a booking", consumes = MediaType.APPLICATION_JSON)
	public BookingDto createBookingByMemberNo(DelegateDto delegate) {
		return helper.createBooking(eventId, delegate);
	}

	@POST
	@Path("/{bookingId}/payment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Make Payment for a booking", consumes = MediaType.APPLICATION_JSON)
	public BookingDto makePayment(
			@ApiParam(value = "Booking for which payment is being made") @PathParam("bookingId") String bookingId,
			@ApiParam(value = "Payment Mode") @QueryParam("paymentMode") String paymentMode,
			@ApiParam(value = "Payment referenceNo") @QueryParam("paymentRef") String paymentRef) {
		String uri = "";
		BookingDto dto = helper.processPayment(eventId, bookingId, paymentMode, paymentRef);

		return dto;
	}

	@PUT
	@Path("/{bookingId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Update an existing booking", response = Booking.class, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public BookingDto update(
			// @ApiParam(value="Event for which booking is being updated")
			// @PathParam("eventId") String eventId,
			@ApiParam(value = "Booking Id of the booking to update", required = true) @PathParam("bookingId") String bookingId,
			BookingDto dto) {

		String uri = "";
		dto = helper.updateBooking(eventId, bookingId, dto);
		return dto;
	}

	@DELETE
	@Path("/{bookingId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete an existing booking")
	public void delete(
			// @ApiParam(value="Event from which booking is being deleted")
			// @PathParam("eventId") String eventId,
			@ApiParam(value = "Booking Id of the booking to delete", required = true) @PathParam("bookingId") String bookingId) {
		helper.deleteBooking(eventId, bookingId);
	}

	@PUT
	@Path("/{bookingId}/delegates/{delegateId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public DelegateDto updateDelegate(@PathParam("bookingId") String bookingId,
			@PathParam("delegateId") String delegateId, DelegateDto delegate) {
		return helper.updateDelegate(delegateId, delegate);
	}

	@POST
	@Path("/{bookingId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Send Pro invoice mail and sms")
	public void sendAlert(@PathParam("bookingId") String bookingId) {
		helper.sendProInvoice(bookingId);
		helper.sendDelegateSMS(bookingId);
	}

	@POST
	@Path("/cancel/{bookingId}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean cancelBooking(@PathParam("bookingId") String bookingId) {
		return helper.cancelBooking(bookingId);
	}

	@POST
	@Path("/resendProforma/{emails}/{bookingRefId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Send Proforma Invoice mail and sms")
	public void resendProforma(@PathParam("emails") String newEmails, @PathParam("bookingRefId") String bookingRefId) {
		// helper.sendProInvoice(newEmail);
	}

	public Integer getBookingCount(String searchTerm, String accomodationRefId, String bookingStatus) {
		return helper.getDelegatesCount(eventId, searchTerm, accomodationRefId, bookingStatus);
	}
}