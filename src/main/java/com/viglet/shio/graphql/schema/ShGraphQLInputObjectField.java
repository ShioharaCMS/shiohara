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
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viglet.shio.persistence.model.post.ShPostAttr;
import com.viglet.shio.persistence.model.post.type.ShPostType;
import com.viglet.shio.persistence.model.post.type.ShPostTypeAttr;
import com.viglet.shio.persistence.repository.post.type.ShPostTypeAttrRepository;
import com.viglet.shio.persistence.service.post.ShPostAttrService;
import com.viglet.shio.utils.ShPostUtils;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLScalarType;

/**
 * GraphQL Input Object Field.
 *
 * @author Alexandre Oliveira
 * @since 0.3.7
 */
@Component
public class ShGraphQLInputObjectField {
	@Autowired
	private ShPostTypeAttrRepository shPostTypeAttrRepository;
	@Autowired
	private ShPostUtils shPostUtils;
	@Autowired
	private ShPostAttrService shPostAttrService;

	
	public void createInputObjectField(GraphQLInputObjectType.Builder builder, String name, GraphQLInputType type,
			String description) {
		builder.field(newInputObjectField().name(name).description(description).type(type));
	}

	public void createInputObjectField(GraphQLInputObjectType.Builder builder, String name, String type,
			String description) {

		GraphQLScalarType scalarType = null;

		if (type.equals(ShGraphQLConstants.FIELD_TYPE_GRAPHQL_DATE_TIME)) {
			scalarType = ExtendedScalars.DateTime;
			this.createInputObjectFieldCondition(builder, name, null, scalarType, description);
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_NOT, scalarType,
					"All values that are not equal to given value.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_IN, scalarType,
					"All values that are not contained in given list.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_LT, scalarType,
					"All values less than the given value.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_LTE, scalarType,
					"All values less than or equal the given value.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_GT, scalarType,
					"All values greater than the given value.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_GTE, scalarType,
					"All values greater than the given value.");
		} else if (type.equals(ShGraphQLConstants.FIELD_TYPE_GRAPHQL_STRING)
				|| type.equals(ShGraphQLConstants.FIELD_TYPE_GRAPHQL_ID)) {
			if (type.equals(ShGraphQLConstants.FIELD_TYPE_GRAPHQL_STRING))
				scalarType = GraphQLString;
			else if (type.equals(ShGraphQLConstants.FIELD_TYPE_GRAPHQL_ID))
				scalarType = GraphQLID;

			this.createInputObjectFieldCondition(builder, name, null, scalarType, description);
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_NOT, scalarType,
					"All values that are not equal to given value.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_IN, scalarType,
					"All values that are contained in given list.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_NOT_IN, scalarType,
					"All values that are not contained in given list.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_CONTAINS, scalarType,
					"All values containing the given string.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_NOT_CONTAINS, scalarType,
					"All values not containing the given string.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_STARTS_WITH, scalarType,
					"All values starting with the given string.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_NOT_STARTS_WITH,
					scalarType, "All values not starting with the given string.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_ENDS_WITH, scalarType,
					"All values ending with the given string.");
			this.createInputObjectFieldCondition(builder, name, ShGraphQLConstants.CONDITION_NOT_ENDS_WITH, scalarType,
					"All values not ending with the given string");
		}
	}

	public void createInputObjectFieldCondition(GraphQLInputObjectType.Builder builder, String name, String condition,
			GraphQLScalarType scalarType, String description) {
		builder.field(newInputObjectField()
				.name(condition == null ? name
						: String.format("%s%s%s", name, ShGraphQLConstants.CONDITION_SEPARATOR, condition))
				.description(description).type(scalarType));
	}

	public void fieldWhereCondition(ShPostType shPostType, List<Map<String, String>> posts,
			Entry<String, Object> whereArgItem, String field, String action) {
		ShPostTypeAttr shPostTypeAttr = shPostTypeAttrRepository.findByShPostTypeAndName(shPostType,
				field.toUpperCase());
		List<ShPostAttr> shPostAttrs = shPostAttrService.findByShPostTypeAttrAndValueAndCondition(shPostTypeAttr,
				whereArgItem.getValue().toString(), action);
		for (ShPostAttr shPostAttr : shPostAttrs) {
			Map<String, String> postAttrsDefault = shPostUtils.postAttrGraphQL(shPostAttr.getShPost());
			posts.add(postAttrsDefault);
		}
	}
}
