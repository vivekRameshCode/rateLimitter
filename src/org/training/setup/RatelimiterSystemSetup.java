/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package org.training.setup;

import static org.training.constants.RatelimiterConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import org.training.constants.RatelimiterConstants;
import org.training.service.RatelimiterService;


@SystemSetup(extension = RatelimiterConstants.EXTENSIONNAME)
public class RatelimiterSystemSetup
{
	private final RatelimiterService ratelimiterService;

	public RatelimiterSystemSetup(final RatelimiterService ratelimiterService)
	{
		this.ratelimiterService = ratelimiterService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		ratelimiterService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return RatelimiterSystemSetup.class.getResourceAsStream("/ratelimiter/sap-hybris-platform.png");
	}
}
