package com.haomostudio.jrs.servlet;


import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by Stevie on 2015/12/22.
 */

public class EmptyRequestParamResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterType().isAssignableFrom(String.class)) && (null != parameter.getParameterAnnotation(RequestParam.class));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        RequestParam ann = parameter.getParameterAnnotation(RequestParam.class);
        Object arg = webRequest.getParameter(ann.value());
        if (StringUtils.isEmpty(arg)) {
            throw new MissingServletRequestParameterException(ann.value(), parameter.getParameterType().getSimpleName());
        }
        return arg;
    }
}
