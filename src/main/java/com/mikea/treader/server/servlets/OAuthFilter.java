package com.mikea.treader.server.servlets;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.mikea.treader.model.User;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class OAuthFilter implements javax.servlet.Filter {
    private final Provider<Twitter> twitter;

    @Inject
    public OAuthFilter(Provider<Twitter> twitter) {
        this.twitter = twitter;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String servletPath = request.getServletPath();
        if (servletPath.startsWith("/oauth_") ||
                servletPath.startsWith("/s/") ||
                servletPath.startsWith("/_ah/")
                ) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Object accessToken = request.getSession().getAttribute("accessToken");
        if (accessToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    if (name.equals("twitter_user_id")) {
                        Long user_id = Long.parseLong(cookie.getValue());
                        Objectify ofy = ObjectifyService.begin();

                        User user = ofy.find(User.class, user_id);
                        if (user != null) {
                            AccessToken token = new AccessToken(user.token, user.tokenSecret);
                            request.getSession().setAttribute("accessToken", token);
                            twitter.get().setOAuthAccessToken(token);
                            filterChain.doFilter(servletRequest, servletResponse);
                            return;
                        }
                    }
                }
            }

            ((HttpServletResponse) servletResponse).sendRedirect("/oauth_login");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
