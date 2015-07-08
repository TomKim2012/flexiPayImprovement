package com.workpoint.icpak.shared.api;

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
import javax.ws.rs.core.MediaType;

import com.workpoint.icpak.shared.model.events.EventDto;


@Path("events")
public interface EventsResource {

		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public List<EventDto> getAll(@QueryParam("offset") Integer offset,
				@QueryParam("limit") Integer limit);
		
		@Path("/{eventId}/bookings")
		public BookingsResource bookings(@PathParam("eventId") String eventId);

		@GET
		@Path("/{eventId}")
		@Produces(MediaType.APPLICATION_JSON)
		public EventDto getById( 
				@PathParam("eventId") String eventId);

		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public EventDto create(EventDto dto);

		@PUT
		@Path("/{eventId}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public EventDto update( 
				@PathParam("eventId") String eventId, EventDto dto);

		@DELETE
		@Path("/{eventId}")
		@Produces(MediaType.APPLICATION_JSON)
		public void delete(
				@PathParam("eventId") String eventId);
		
}
