/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.mongo.query;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

public class SingleUpdateQuery<UpdateResult> extends MongoQuery<UpdateResult> {

    private final Bson newDocument;

    /**
     * @param collection  Database collection
     * @param searchQuery     Search query
     * @param newDocument     New Document to replace
     * @param doAfterOptional Consumer task to do after query is complete.
     */
    public SingleUpdateQuery(final MongoCollection<Document> collection,
                             final Bson searchQuery,
                             final Bson newDocument,
                             final SingleResultCallback<UpdateResult> doAfterOptional) {
        super(collection, searchQuery, doAfterOptional);
        this.newDocument = newDocument;
    }

    public Bson getNewDocument() {
        return newDocument;
    }
}
