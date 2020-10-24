package com.fidecent.fbn.hx.configs;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Component
public class ErrorHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, Object o, @NotNull Exception e) {
        log.error(e);
        return null;
    }
}
