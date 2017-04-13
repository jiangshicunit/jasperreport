package com.haomostudio.jrs.servlet;

import com.haomostudio.jrs.common.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Output error/exception as JSON and also can be used a global error logger.
 * Created by Stevie on 2015/12/22.
 */
public class DefaultHandlerExceptionResolver implements HandlerExceptionResolver {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultHandlerExceptionResolver.class.getName());

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handlerMethod, Exception e) {
        if (e.getMessage() != null && !e.getMessage().contains("Authentication Failed. Please Login.")
                && !e.getMessage().contains("Missing request header 'X-Auth-Token'")
                && !(e instanceof  org.springframework.web.bind.MissingServletRequestParameterException)) {
            LOG.error(request.getRequestURI(), e);
            e.printStackTrace();
        }

        if (e instanceof AuthenticationException) {
            response.setStatus(403);
        } else {
            response.setStatus(500);
        }

        ModelAndView mv = new ModelAndView();
        //set data
        Map<String, Object> result = Resp.fail(e.getMessage());
        mv.addAllObjects(result);
        //set view
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        mv.setView(view);

        return mv;
    }
}
