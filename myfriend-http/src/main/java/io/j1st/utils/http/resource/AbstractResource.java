package io.j1st.utils.http.resource;

import org.apache.commons.lang.StringUtils;
import xiaopeng666.top.utils.mongo.MongoStorage;

import javax.servlet.http.HttpServletRequest;

/**
 * Abstract Base Resource
 */
public class AbstractResource {

    protected final MongoStorage mongo;

    public AbstractResource(MongoStorage mongo) {
        this.mongo = mongo;
    }

}
