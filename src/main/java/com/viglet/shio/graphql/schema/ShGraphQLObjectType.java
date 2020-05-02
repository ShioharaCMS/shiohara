/*
 * Copyright (C) 2016-2020 the original author or authors. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.viglet.shio.graphql.schema;

import static graphql.Scalars.GraphQLID;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphqlTypeComparatorRegistry.BY_NAME_REGISTRY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viglet.shio.persistence.model.post.type.ShPostType;
import com.viglet.shio.persistence.model.post.type.ShPostTypeAttr;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;

/**
 * GraphQL Object Type.
 *
 * @author Alexandre Oliveira
 * @since 0.3.7
 */
@Component
public class ShGraphQLObjectType {

	@Autowired
	private ShGraphQLUtils shGraphQLUtils;
	
	public GraphQLObjectType createObjectType(ShPostType shPostType) {
		Builder builderPlural = newObject().name(shPostType.getName()).description(shPostType.getDescription());

		this.createObjectTypeFields(shPostType, builderPlural);

		return builderPlural.comparatorRegistry(BY_NAME_REGISTRY).build();
	}

	private void createObjectTypeFields(ShPostType shPostType, Builder builder) {
		builder.field(newFieldDefinition().name(ShGraphQLConstants.ID).description("Identifier").type(GraphQLID));
		builder.field(newFieldDefinition().name(ShGraphQLConstants.TITLE).description("Title").type(GraphQLString));
		builder.field(newFieldDefinition().name(ShGraphQLConstants.DESCRIPTION).description("Description")
				.type(GraphQLString));
		builder.field(
				newFieldDefinition().name(ShGraphQLConstants.FURL).description("Friendly URL").type(GraphQLString));
		builder.field(
				newFieldDefinition().name(ShGraphQLConstants.MODIFIER).description("Modifier").type(GraphQLString));
		builder.field(
				newFieldDefinition().name(ShGraphQLConstants.PUBLISHER).description("Publisher").type(GraphQLString));
		builder.field(
				newFieldDefinition().name(ShGraphQLConstants.FOLDER).description("Folder Name").type(GraphQLString));

		for (ShPostTypeAttr shPostTypeAttr : shPostType.getShPostTypeAttrs()) {
			String postTypeAttrName = shGraphQLUtils.normalizedField(shPostTypeAttr.getName());
			builder.field(newFieldDefinition().name(postTypeAttrName).description(shPostTypeAttr.getDescription())
					.type(GraphQLString));
		}
	}
}
