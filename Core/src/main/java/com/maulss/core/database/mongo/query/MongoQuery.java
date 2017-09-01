/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.mongo.query;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

public abstract class MongoQuery<T> {

    private final Bson searchQuery;
    private final MongoCollection<Document> collection;
    private final SingleResultCallback<T> doAfter;

    /**
     * @param collection  Database collection
     * @param searchQuery Search query
     * @param doAfter     Consumer task to do after query is complete.
     */
    public MongoQuery(final MongoCollection<Document> collection,
                      final Bson searchQuery,
                      final SingleResultCallback<T> doAfter) {
        this.collection = collection;
        this.searchQuery = searchQuery;
        this.doAfter = doAfter == null ? (x, y) -> {} : doAfter;
    }

    public Bson getSearchQuery() {
        return searchQuery;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public SingleResultCallback<T> getDoAfter() {
        return doAfter;
    }
}
