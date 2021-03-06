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
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.icpak.rest.dao.helper.ApplicationFormDaoHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workpoint.icpak.shared.api.CategoriesResource;
import com.workpoint.icpak.shared.model.ApplicationCategoryDto;


@Path("categories")
@Api(value="categories", description="Handles CRUD for Categories")
public class CategoryResourceImpl implements CategoriesResource{

	@Inject ApplicationFormDaoHelper helper;

	private String getUri() {
		return "";
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Retrieve all active Categories")
	public List<ApplicationCategoryDto> getAll() {
		
		List<ApplicationCategoryDto> dtos = helper.getAllCategories();
		return dtos;
	}
	
	@GET
	@Path("/{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get a category by categoryId", response=ApplicationCategoryDto.class, consumes=MediaType.APPLICATION_JSON)
	public ApplicationCategoryDto getById(	
			@ApiParam(value="CategoryDto Id of the category to fetch", required=true) 
			@PathParam("categoryId") String categoryId) {
		
		String uri = getUri();
		ApplicationCategoryDto dto = helper.getCategoryById(categoryId).toDto();
		dto.setUri(uri);
		return dto;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Create a new category", response=ApplicationCategoryDto.class, consumes=MediaType.APPLICATION_JSON)
	public ApplicationCategoryDto create(ApplicationCategoryDto category) {
		
		helper.createCategory(category);
		String uri = getUri()+"/"+category.getRefId();
		category.setUri(uri);
		
		return category;
	}

	@PUT
	@Path("/{categoryId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update an existing category", response=ApplicationCategoryDto.class, 
	consumes=MediaType.APPLICATION_JSON, produces=MediaType.APPLICATION_JSON)
	public ApplicationCategoryDto update(
			@ApiParam(value="CategoryDto Id of the category to update", required=true)
			@PathParam("categoryId") String categoryId, 
			ApplicationCategoryDto category) {
		helper.updateCategory(categoryId, category);
		category.setUri(getUri());
		
		return category;
	}

	@DELETE
	@Path("/{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Delete an existing category")
	public void delete(
			@ApiParam(value="CategoryDto Id of the category to delete", required=true) 
			@PathParam("categoryId") String categoryId) {
		
		helper.deleteCategory(categoryId);
	}

}
