package com.interview.test.controller;

import com.interview.test.model.CollectionCheckRequest;
import com.interview.test.model.CollectionCheckResponse;
import com.interview.test.service.CollectionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collection")
public class CollectionController {
	private final CollectionService collectionService;

	public CollectionController(CollectionService collectionService) {
		this.collectionService = collectionService;
	}

	@PostMapping("/check")
	public CollectionCheckResponse check(@RequestBody CollectionCheckRequest request) {
		return collectionService.check(request);
	}
}