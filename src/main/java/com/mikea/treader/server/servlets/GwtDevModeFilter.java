package com.mikea.treader.server.servlets;

import com.google.inject.Singleton;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class GwtDevModeFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        
        String environment = System.getProperty("com.google.appengine.runtime.environment");
        if (request.getServletPath().equals("/") && !environment.equals("Production")) {
            String p = request.getParameter("gwt.codesvr");
            if (p == null) {
                ((HttpServletResponse) servletResponse).sendRedirect("/?gwt.codesvr=127.0.0.1:9997");
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
