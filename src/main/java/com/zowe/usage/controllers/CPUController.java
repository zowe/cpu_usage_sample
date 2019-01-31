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

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.zos.sdsf.core.ISFActive;
import com.zowe.usage.utilities.CPUUtilities;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/cpu")
@Api(value = "CPU Information", tags = {
		"CPU Operations" }, description = "Operations providing insights to system performance")
public class CPUController {

	File logFile;
	Logger logger = LoggerFactory.getLogger(CPUController.class);

	@Inject
	CPUUtilities utils;

	@GetMapping("/snapshot")
	@ApiOperation(value = "Get a snapshot of CPU Usage", tags = { "CPU Operations" })
	public @ResponseBody List<String> getUsage() {
		return CPUUtilities.getCurrentSystemResourceUsage();
	}

	@GetMapping("/breakdown")
	@ApiOperation(value = "Get breakdown of current system CPU usage", notes = "This API returns the current system CPU usage, along with some other current system statistics.", tags = {
			"CPU Operations" }, response = ObjectNode.class)
	public @ResponseBody ObjectNode getCPUInformation() {
		logger.info("getCPU called");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		List<ISFActive> activeAddressSpaces = CPUUtilities.runSDSFDisplayActiveRequest();
		node.set("system", extractSystemWideInformation(activeAddressSpaces)); //$NON-NLS-1$
		if (activeAddressSpaces.size() > 0) {
			node.set("addressSpaces", extractAddressSpaceInformation(activeAddressSpaces)); //$NON-NLS-1$
		}
		return node;
	}

	private static ObjectNode extractSystemWideInformation(List<ISFActive> activeAdressSpaces) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		ISFActive firstAddressSpace = activeAdressSpaces.get(0);
		node.put("systemName", firstAddressSpace.getValue("SYSNAME")); //$NON-NLS-1$ //$NON-NLS-2$
		node.put("CPUUsage", firstAddressSpace.getValue("SCPU")); //$NON-NLS-1$ //$NON-NLS-2$
		node.put("ZAAPUsage", firstAddressSpace.getValue("SZAAP")); //$NON-NLS-1$ //$NON-NLS-2$
		node.put("ZIIPUsage", firstAddressSpace.getValue("SZIIP")); //$NON-NLS-1$ //$NON-NLS-2$
		node.put("pagingRate", firstAddressSpace.getValue("SPAGING")); //$NON-NLS-1$ //$NON-NLS-2$

		double totalEXCPRate = 0.0;
		for (ISFActive addressSpace : activeAdressSpaces) {
			String EXCPRate = addressSpace.getValue("EXCPRT"); //$NON-NLS-1$
			totalEXCPRate += Double.valueOf(EXCPRate.replace(",", "")).doubleValue(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		node.put("totalEXCPRate", String.valueOf(totalEXCPRate)); //$NON-NLS-1$

		return node;
	}

	private static ArrayNode extractAddressSpaceInformation(List<ISFActive> activeAdressSpaces) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode addressSpaceObjects = mapper.createArrayNode();
		for (ISFActive addressSpace : activeAdressSpaces) {
			ObjectNode node = mapper.createObjectNode();
			node.put("jobId", addressSpace.getValue("JOBID")); //$NON-NLS-1$ //$NON-NLS-2$
			node.put("CPU", addressSpace.getValue("CPUPR")); //$NON-NLS-1$ //$NON-NLS-2$
			node.put("EXCPRate", addressSpace.getValue("EXCPRT")); //$NON-NLS-1$ //$NON-NLS-2$
			addressSpaceObjects.add(node);
		}
		return addressSpaceObjects;
	}
}
