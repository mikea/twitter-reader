package com.mikea.treader.server;

import com.google.inject.servlet.ServletModule;
import com.mikea.treader.server.servlets.OAuthFilter;
import com.mikea.treader.server.servlets.OAuthLoginServlet;
import com.mikea.treader.server.servlets.OAuthCallbackServlet;

/**
 */
public class MyServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(OAuthFilter.class);

        serve("/oauth_login").with(OAuthLoginServlet.class);
        serve("/oauth_callback").with(OAuthCallbackServlet.class);
    }
}
