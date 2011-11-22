package com.mikea.treader.server.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class OAuthLoginServlet extends HttpServlet {
    private final Twitter twitter;

    @Inject
    public OAuthLoginServlet(Twitter twitter) {
        this.twitter = twitter;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            RequestToken requestToken = twitter.getOAuthRequestToken();

            req.getSession().setAttribute("requestToken", requestToken);
            String authorizationURL = requestToken.getAuthorizationURL();
            resp.sendRedirect(authorizationURL);
        } catch (TwitterException e) {
            throw new ServletException(e);
        }
    }
}
