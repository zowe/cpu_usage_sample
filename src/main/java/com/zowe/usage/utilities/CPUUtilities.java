/**
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBM Corporation 2019
 */
package com.zowe.usage.utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.context.annotation.Configuration;

import com.ibm.zos.sdsf.core.ISFActive;
import com.ibm.zos.sdsf.core.ISFActiveRunner;
import com.ibm.zos.sdsf.core.ISFException;
import com.ibm.zos.sdsf.core.ISFRequestSettings;

@Configuration
public class CPUUtilities {

	private static final Logger log = Logger.getLogger(CPUUtilities.class.getName());

	public static List<String> getCurrentSystemResourceUsage() {
		List<String> resourceUsage = new ArrayList<String>();
		List<ISFActive> activeAddressSpaces = runSDSFDisplayActiveRequest();
		if (activeAddressSpaces.size() > 0) {
			ISFActive firstAddressSpace = activeAddressSpaces.get(0);
			addNumberValueIfAvailable(resourceUsage, firstAddressSpace, "SCPU"); //$NON-NLS-1$
			addNumberValueIfAvailable(resourceUsage, firstAddressSpace, "SZAAP"); //$NON-NLS-1$
			addNumberValueIfAvailable(resourceUsage, firstAddressSpace, "SZIIP"); //$NON-NLS-1$
			addNumberValueIfAvailable(resourceUsage, firstAddressSpace, "SPAGING"); //$NON-NLS-1$
		}

		return resourceUsage;
	}

	public static List<ISFActive> runSDSFDisplayActiveRequest() {
		ISFRequestSettings settings = new ISFRequestSettings();
		settings.addISFFilter("SYSNAME eq &SYSNAME"); //$NON-NLS-1$
		final ISFActiveRunner runner = new ISFActiveRunner(settings);
		List<ISFActive> activeJobs = new LinkedList<>();
		try {
			activeJobs = runner.exec();
		} catch (ISFException e) {
			log.severe("CPUUtilities.FailedDAPanelInfo " + e.getMessage()); //$NON-NLS-1$
		}
		return activeJobs;
	}

	private static void addNumberValueIfAvailable(List<String> obj, ISFActive as, String column) {
		String value = as.getValue(column);
		if (value != null) {
			obj.add(column + ": " + value);
		}
	}

}
