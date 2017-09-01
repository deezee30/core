/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.mongo.query;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import org.bson.conversions.Bson;

public class DocumentSearchQuery<Document> extends MongoQuery<Document> {

    /**
     * @param collection  Database collection
     * @param searchQuery Search query
     * @param doAfter     Consumer task to do after query is complete.
     */
    public DocumentSearchQuery(final MongoCollection<org.bson.Document> collection,
                               final Bson searchQuery,
                               final SingleResultCallback<Document> doAfter) {
        super(collection, searchQuery, doAfter);
    }
}
