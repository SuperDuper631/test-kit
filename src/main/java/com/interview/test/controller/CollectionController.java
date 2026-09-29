package com.interview.test.controller;

import com.interview.test.model.CollectionCheckRequest;
import com.interview.test.model.CollectionCheckResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/collection")
public class CollectionController {


	@PostMapping("/check")
	public CollectionCheckResponse check(@RequestBody CollectionCheckRequest request) {
		return null;
	}
}