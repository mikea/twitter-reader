package com.mikea.treader.server.servlets;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.mikea.treader.model.User;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class OAuthFilter implements javax.servlet.Filter {
    private static final Logger LOG = Logger.getLogger(OAuthFilter.class.getName());
    
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
            LOG.fine("Skipping filter");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        AccessToken accessToken = (AccessToken) request.getSession().getAttribute("accessToken");
        if (accessToken == null) {
            LOG.fine("Access token is not found");
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    if (name.equals("twitter_user_id")) {
                        LOG.fine("Twitter user id found in cookies");
                        Long user_id = Long.parseLong(cookie.getValue());
                        Objectify ofy = ObjectifyService.begin();

                        User user = ofy.find(User.class, user_id);
                        if (user != null) {
                            LOG.fine("User object found, setting access token");
                            accessToken = new AccessToken(user.token, user.tokenSecret);
                            request.getSession().setAttribute("accessToken", accessToken);
                            Twitter t = twitter.get();
                            t.setOAuthAccessToken(accessToken);
                            filterChain.doFilter(servletRequest, servletResponse);
                            return;
                        }
                    }
                }
            }

            login(request, (HttpServletResponse) servletResponse);
            return;
        } else {
            Twitter t = twitter.get();
            t.setOAuthAccessToken(accessToken);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        LOG.fine("Logging into twitter.");
        try {
            RequestToken requestToken = twitter.get().getOAuthRequestToken();
            req.getSession().setAttribute("requestToken", requestToken);
            resp.sendRedirect(requestToken.getAuthorizationURL());
        } catch (TwitterException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
    }
}
