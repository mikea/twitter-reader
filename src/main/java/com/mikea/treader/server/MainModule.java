package com.mikea.treader.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.ObjectifyService;
import com.mikea.treader.model.User;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.util.logging.Logger;

/**
 */
public class MainModule extends AbstractModule {
    private static final Logger LOG = Logger.getLogger(MainModule.class.getName());
    @Override
    protected void configure() {
        ObjectifyService.register(User.class);
    }

    @Provides
    @RequestScoped
    Twitter getTwitter() {
        Twitter twitter = new TwitterFactory().getInstance();

        String environment = System.getProperty("com.google.appengine.runtime.environment");
        LOG.fine("Environment: " + environment);
        if (environment.equals("Production")) {
            twitter.setOAuthConsumer("hbJyP47vK13RdJunrso0g", "4fOLcwABIZI6dvKUhsAbANesaC0wnimQUDkVXDMYM");
        } else {
            twitter.setOAuthConsumer("V5e8bruepdEvvqa3uQZQ", "aMY3WgbRquNfjZtZH1ch79STPLuH0Il0MozpNB2stxU"); // localhost
        }

        return twitter;
    }
}
