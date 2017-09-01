/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.mongo.query;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class BulkWriteQuery<BulkWriteResult> extends MongoQuery<BulkWriteResult> {

    private final List<UpdateOneModel<Document>> models;

    /**
     * @param collection  Database collection
     * @param doAfterOptional Consumer task to do after query is complete.
     * @param models          Write models
     */
    public BulkWriteQuery(final MongoCollection<Document> collection,
                          final List<UpdateOneModel<Document>> models,
                          final SingleResultCallback<BulkWriteResult> doAfterOptional) {
        super(collection, null, doAfterOptional);
        this.models = Validate.notNull(models);
    }

    public BulkWriteQuery(final MongoCollection<Document> collection,
                          final Bson searchQuery,
                          final SingleResultCallback<BulkWriteResult> doAfter,
                          final List<UpdateOneModel<Document>> models) {
        super(collection, searchQuery, doAfter);
        this.models = Validate.notNull(models);
    }

    public List<UpdateOneModel<Document>> getModels() {
        return models;
    }
}
