package com.mikea.treader.server.servlets;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.mikea.treader.model.User;
import com.mikea.treader.util.LongLivedCookie;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class OAuthCallbackServlet extends HttpServlet {
    private final Provider<Twitter> twitter;

    @Inject
    public OAuthCallbackServlet(Provider<Twitter> twitter) {
        this.twitter = twitter;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestToken requestToken = (RequestToken) req.getSession().getAttribute("requestToken");
        String verifier = req.getParameter("oauth_verifier");
        try {
            AccessToken accessToken = twitter.get().getOAuthAccessToken(requestToken, verifier);
            req.getSession().removeAttribute("requestToken");
            req.getSession().setAttribute("accessToken", accessToken);
            
            resp.addCookie(new LongLivedCookie("twitter_user_id", String.valueOf(accessToken.getUserId())));

            User user = new User();
            user.id = accessToken.getUserId();
            user.screenName = accessToken.getScreenName();
            user.token = accessToken.getToken();
            user.tokenSecret = accessToken.getTokenSecret();

            Objectify ofy = ObjectifyService.begin();
            ofy.put(user);

            resp.sendRedirect("/");
        } catch (TwitterException e) {
            throw new ServletException(e);
        }
    }
}
