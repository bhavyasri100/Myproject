package com.sample.na.helper;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class UndeclaredParamsHandlerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
							 Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			if (handlerMethod.getMethodAnnotation(DisallowUndeclaredRequestParams.class) != null) {
				checkParams(request, getDeclaredRequestParams(handlerMethod));
			}
		}
		return true;
	}


	@Autowired
	private void checkParams(HttpServletRequest request, Set<String> allowedParams) {
		request.getParameterMap().entrySet().forEach(entry -> {
			String param = entry.getKey();
			if (!allowedParams.contains(param)) {
				try {
					throw new UndeclaredRequestParamException(param, allowedParams);
				} catch (UndeclaredRequestParamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new IllegalArgumentException(e.getMessage());
				}
			}
		});
	}


	private Set<String> getDeclaredRequestParams(HandlerMethod handlerMethod) {
		Set<String> declaredRequestParams = new HashSet<>();
		MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
		ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

		for (MethodParameter methodParameter : methodParameters) {
			if (methodParameter.hasParameterAnnotation(RequestParam.class)) {
				RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
				if (StringUtils.hasText(requestParam.value())) {
					declaredRequestParams.add(requestParam.value());
				} else {
					methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
					declaredRequestParams.add(methodParameter.getParameterName());
				}
			}
		}
		return declaredRequestParams;
	}

}