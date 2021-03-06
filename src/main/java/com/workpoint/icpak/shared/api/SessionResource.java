/*
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.workpoint.icpak.shared.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.workpoint.icpak.shared.model.auth.CurrentUserDto;


@Path("/session")
@Produces(MediaType.APPLICATION_JSON)
public interface SessionResource {
	
    @DELETE
    void logout();

    @GET
    CurrentUserDto getCurrentUser();

//    @POST
//    @Path("/remember-me")
//    void rememberMe(@CookieParam("LoggedInCookie") Cookie cookie);
}
