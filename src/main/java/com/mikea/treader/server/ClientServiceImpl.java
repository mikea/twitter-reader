package com.mikea.treader.server;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mikea.treader.client.ClientService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@Singleton
public class ClientServiceImpl extends RemoteServiceServlet implements ClientService {
    private Provider<Twitter> twitter;

    @Inject
    public ClientServiceImpl(Provider<Twitter> twitter) {
        this.twitter = twitter;
    }

    @Override
    public String getMessage() {
        try {
            Twitter t = twitter.get();
            ResponseList<Status> timeline = t.getHomeTimeline();
            return "HELLO: " + timeline.toString();
        } catch (TwitterException e) {
            throw new IllegalStateException(e);
        }
    }
}
