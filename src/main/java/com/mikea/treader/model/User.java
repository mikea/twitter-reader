package com.mikea.treader.model;

import javax.persistence.Id;

/**
 */
public class User {
    @Id public Long id;
    public String screenName;
    public String token;
    public String tokenSecret;
}
