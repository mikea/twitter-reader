package com.mikea.treader.server;

import com.google.inject.servlet.ServletModule;
import com.mikea.treader.server.servlets.GwtDevModeFilter;
import com.mikea.treader.server.servlets.OAuthFilter;
import com.mikea.treader.server.servlets.OAuthCallbackServlet;

/**
 */
public class Servlets extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(OAuthFilter.class);
        filter("/*").through(GwtDevModeFilter.class);

        serve("/oauth_callback").with(OAuthCallbackServlet.class);
        serve("/main/client").with(ClientServiceImpl.class);
    }
}
