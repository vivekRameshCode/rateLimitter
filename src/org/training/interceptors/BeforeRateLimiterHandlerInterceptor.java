/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package org.training.interceptors;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hazelcast.core.IMap;





/**
 *
 */
public class BeforeRateLimiterHandlerInterceptor extends HandlerInterceptorAdapter
{
	private static final String INTERCEPTOR_ONCE_KEY = BeforeRateLimiterHandlerInterceptor.class.getName();

	static Map<String, Integer> hitCountMap = null;

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		System.out.println("Remote Address of req==============================" + request.getRemoteAddr());
		System.out.println("Remote Id adderess ===================================" + request.getRemoteHost());
		System.out.println("Remote Id adderess ===================================" + request.getRequestedSessionId());
		System.out.println("Remote Id adderess ===================================" + request.getRequestURL());
		System.out.println("Remote Id adderess ===================================" + request.getRequestURI());
		System.out.println("Remote Id adderess ===================================" + request.getRemotePort());
		System.out.println("Remote Id adderess ===================================" + request.getPathInfo());


		System.out.println("local address ==========" + request.getLocalAddr());
		System.out.println("user principal ==========" + request.getUserPrincipal());
		System.out.println("servlet address ==========" + request.getServletPath());

		System.out.println("Remote Id adderess ===================================" + request.getSession().getId());
		System.out.println("Remote Id adderess ==================================="
				+ request.getSession().getSessionContext().getIds());

		System.out.println("Ip address from header" + request.getHeader("Remote_Addr"));
		final String ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
		System.out.println("Ip address from header" + ipAddress);

		try
		{
			System.out.println("Count Before ===========");

			final IMap<String, Integer> dataMap = RILHazelcastClient.getMap("default");
			Integer otpVer = RILHazelcastClient.get("default", request.getRemoteAddr());
			System.out.println("Count ===========" + otpVer);
			if (otpVer != null && otpVer.intValue() > 0)
			{
				System.out.println("Count if ===========" + otpVer);
				dataMap.put(request.getRemoteAddr(), Integer.valueOf(otpVer.intValue() + 1));
			}
			else
			{
				System.out.println("Count else ===========" + otpVer);
				dataMap.put(request.getRemoteAddr(), Integer.valueOf(1), 300, TimeUnit.SECONDS);
			}

			if (otpVer.intValue() > 5)
			{
				ModelAndView modelView = new ModelAndView();
				response.sendRedirect("https://localhost:9002/login.jsp");
				postHandle(request, response, handler, modelView);
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception
	{
		// YTODO Auto-generated method stub
		super.postHandle(request, response, handler, modelAndView);
	}

	public static Map<String, Integer> getCountMap()
	{
		if (hitCountMap == null)
		{
			synchronized (BeforeRateLimiterHandlerInterceptor.class)
			{
				if (hitCountMap == null)
				{
					hitCountMap = new HashMap<String, Integer>();
				}
			}
		}
		return hitCountMap;
	}
}