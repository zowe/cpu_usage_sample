/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */
package com.zowe.usage.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/application")
@Api(tags = { "Application Operations" })
public class InformationController {
	@GetMapping("/health")
	@ApiOperation(value = "Application health InformationController", tags = { "Application Operations" })
	public @ResponseBody String getHealth() {
		return "Up - RESTAPIs running";
	}

	@GetMapping("/info")
	public @ResponseBody String getApplicationInformation() {
		return "Provides CPU Usage information";
	}
}
