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

import com.wordnik.swagger.annotations.ApiOperation;
import com.workpoint.icpak.shared.model.CPDDto;
import com.workpoint.icpak.shared.model.CPDFooterDto;
import com.workpoint.icpak.shared.model.CPDSummaryDto;
import com.workpoint.icpak.shared.model.MemberCPDDto;

@Produces(MediaType.APPLICATION_JSON)
public interface CPDResource extends BaseResource {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public List<CPDDto> getAll(@QueryParam("offset") Integer offset,
			@QueryParam("limit") Integer limit,
			@QueryParam("startDate") Long startDate,
			@QueryParam("endDate") Long endDate);

	@GET
	@Path("/cpdFormatted")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<CPDDto> getAllWithDates(@QueryParam("offset") Integer offset,
			@QueryParam("limit") Integer limit,
			@QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate);

	@GET
	@Path("/yearSummaries")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<CPDFooterDto> getYearSummaries(
			@QueryParam("startDate") Long startDate,
			@QueryParam("endDate") Long endDate);

	@GET
	@Path("/cpdmemberSummary")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<MemberCPDDto> getAllMemberSummary(
			@QueryParam("searchTerm") String searchTerm,
			@QueryParam("offset") Integer offset,
			@QueryParam("limit") Integer limit);

	@GET
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	public CPDSummaryDto getCPDSummary(@QueryParam("startDate") Long startDate,
			@QueryParam("endDate") Long endDate);

	@GET
	@Path("/{cpdId}")
	public CPDDto getById(@PathParam("cpdId") String cpdId);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public CPDDto create(CPDDto cpd);

	@PUT
	@Path("/{cpdId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public CPDDto update(@PathParam("cpdId") String cpdId, CPDDto cpd);

	@DELETE
	@Path("/{cpdId}")
	public void delete(@PathParam("cpdId") String cpdId);

	@GET
	@Path("/filteredcount")
	public Integer getCount(@QueryParam("startDate") Long startDate,
			@QueryParam("endDate") Long endDate);

	@GET
	@Path("/summaryCount")
	public Integer getMemberSummaryCount(
			@QueryParam("searchTerm") String searchTerm);

	@GET
	@Path("/countCPDSearch")
	@Consumes(MediaType.APPLICATION_JSON)
	public Integer getCPDsearchCount(@QueryParam("searchTerm") String searchTerm);

	@GET
	@Path("/SearchCPD")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<CPDDto> searchCPd(@QueryParam("offset") int offset,
			@QueryParam("limit") int limit,
			@QueryParam("searchTerm") String searchTerm,
			@QueryParam("startDate") Long startDate,
			@QueryParam("endDate") Long endDate);

}
