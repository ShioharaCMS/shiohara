/*
 * Copyright (C) 2016-2018 Alexandre Oliveira <alexandre.oliveira@viglet.com> 
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

package com.viglet.shiohara.api.history;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viglet.shiohara.persistence.model.site.ShSite;
import com.fasterxml.jackson.annotation.JsonView;
import com.viglet.shiohara.api.ShJsonView;
import com.viglet.shiohara.persistence.model.history.ShHistory;
import com.viglet.shiohara.persistence.model.object.ShObject;
import com.viglet.shiohara.persistence.repository.history.ShHistoryPageableRepository;
import com.viglet.shiohara.persistence.repository.history.ShHistoryRepository;
import com.viglet.shiohara.persistence.repository.object.ShObjectRepository;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api/v2/history")
@Api(tags = "History", description = "History API")
public class ShHistoryAPI {

	@Autowired
	private ShObjectRepository shObjectRepository;
	@Autowired
	private ShHistoryRepository shHistoryRepository;
	@Autowired
	private ShHistoryPageableRepository shHistoryPageableRepository;

	@GetMapping
	@JsonView({ ShJsonView.ShJsonViewObject.class })
	public List<ShHistory> shHistoryList() throws Exception {
		return shHistoryRepository.findAll();
	}

	@GetMapping("/object/{globalId}/{page}")
	@JsonView({ ShJsonView.ShJsonViewObject.class })
	public List<ShHistory> shHistoryByObject(@PathVariable String globalId, @PathVariable int page) throws Exception {
		Pageable pageable = PageRequest.of(page, 50);
		if (shObjectRepository.findById(globalId).isPresent()) {
			ShObject shObject = shObjectRepository.findById(globalId).orElse(null);
			if (shObject != null) {
				if (shObject instanceof ShSite) {
					ShSite shSite = (ShSite) shObject;
					return shHistoryPageableRepository.findByShSiteOrderByDateDesc(shSite.getId(), pageable);
				} else {
					return shHistoryPageableRepository.findByShObjectOrderByDateDesc(shObject.getId(), pageable);
				}
			}
		}
		return null;
	}


	@GetMapping("/object/{globalId}/count")
	@JsonView({ ShJsonView.ShJsonViewObject.class })
	public int shHistoryByObjectCount(@PathVariable String globalId) throws Exception {
		if (shObjectRepository.findById(globalId).isPresent()) {
			ShObject shObject = shObjectRepository.findById(globalId).orElse(null);
			if (shObject != null) {
				if (shObject instanceof ShSite) {
					ShSite shSite = (ShSite) shObject;
					return shHistoryRepository.countByShSite(shSite.getId());
				} else {
					return shHistoryRepository.countByShObject(shObject.getId());
				}
			}
		}
		return 0;
	}
}
