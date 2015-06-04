package com.workpoint.icpak.shared.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.workpoint.icpak.shared.model.UserDto;

@Path("users")
public interface UsersResource{

//		@GET
//		@Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//		public RestAction<List<User>> getAll(@QueryParam("offset") Integer offset,
//				@QueryParam("limit") Integer limit);
//		
//		@GET
//		@Path("/{userId}")
//		@Produces(MediaType.APPLICATION_JSON)
//		public RestAction<User> getById(
//				@PathParam("userId") String userId);
//		
		@GET
		@Path("/auth")
		@Produces(MediaType.APPLICATION_JSON)
		public UserDto login(
				@QueryParam("username") String username,
				@QueryParam("password") String password);
		
//		@POST
//		@Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//		public RestAction<User> create(User user);
		
		
		/**
		 * To test use this uri:
		 * <p/>
		 * curl -v -F 'filename=POvBCBE-PO-NRB-1_1.pdf' -F 'file=@/home/duggan/Downloads/PO_BCBE-PO-NRB-1_1.pdf;type=application/pdf' http://localhost:8080/icpak/api/users/xIXcSQNcXmqMDrth/profile
		 * <p/>
		 * @param userId
		 * @param inputStream
		 * @param fileDisposition
		 * @return
		 * @throws IOException
		 */
//		@POST
//		@Path("/{userId}/profile")
//		@Consumes(MediaType.MULTIPART_FORM_DATA)
//		@Produces(MediaType.APPLICATION_JSON)
//		public Response create(@PathParam("userId") String userId,
//				@FormDataParam("file") FormDataBodyPart body,
//				@FormDataParam("file") InputStream inputStream,
//			    @FormDataParam("file") FormDataContentDisposition fileDisposition);

//		@GET
//		@Path("/{userId}/profile")
//		@Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_OCTET_STREAM)
//		public RestAction<Void> getProfile(
//				@PathParam("userId") String userId);
//
//		@PUT
//		@Path("/{userId}")
//		@Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//		public RestAction<User> update(
//				@PathParam("userId") String userId,User user);		
//		@PUT
//		@Path("/{userId}/password")
//		@Consumes(MediaType.APPLICATION_JSON)
//		@Produces(MediaType.APPLICATION_JSON)
//		public RestAction<Void> updatePassword(
//				@QueryParam("newpassword") String newPassword,
//				@PathParam("userId") String userId);
//
//		@DELETE
//		@Path("/{userId}")
//		@Produces(MediaType.APPLICATION_JSON)
//		public RestAction<Void> delete(
//				@PathParam("userId") String userId);

}
