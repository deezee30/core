/*
 * rv_core
 * 
 * Created on 03 July 2017 at 11:34 PM.
 */

package com.maulss.core.database.mongo;

import com.google.common.collect.Lists;
import com.maulss.core.Logger;
import com.maulss.core.database.*;
import com.maulss.core.database.callback.DatabaseCallback;
import com.maulss.core.database.callback.VoidBulkWriteResult;
import com.maulss.core.database.callback.VoidCallback;
import com.maulss.core.database.callback.VoidUpdateResult;
import com.maulss.core.database.mongo.data.MongoDataOperator;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Resembles a Mongo database object or document that occupies the space
 * within a Mongo database.
 *
 * This can be used for anything varying from players to anything else as an entry.
 */
public interface MongoIdentity extends Identity {

    /**
     * Returns the default {@link MongoCollection<Document>}
     * that represents the collection that the profile is stored
     * within inside the database.
     *
     * <p>The collection must either have a primary key set to
     * {@code _id} which should represent the profile's
     * {@code UUID}, <b>OR</b> override the default method
     * {@link #getDatabaseKey()}.</p>
     *
     * <p>This method will be called many times over and over by internal
     * methods. This means that the return of this value should be cached
     * instead of obtaining a new collection instance each time.</p>
     *
     * @return the profile's general database collection
     */
    MongoCollection<Document> getCollection();

    @Override
    default Logger getLogger() {
        return Mongo.get().getLogger();
    }

    /**
     * @return the search query used to look up the identity data
     */
    default Bson getSearchQuery() {
        return Filters.eq(getDatabaseKey().getKey(), getUuid());
    }

    default void retrieveAll(final DatabaseCallback<List<Document>> doAfter) {
        MongoCollection<Document> coll = getCollection();
        if (coll == null) {
            doAfter.onResult(Collections.emptyList());
            return;
        }

        coll.find().into(new LinkedList<>(), doAfter);
    }

    default void retrieve(final DatabaseCallback<Document> doAfter) {
        MongoCollection<Document> coll = getCollection();
        if (coll == null) {
            doAfter.onResult(null);
            return;
        }

        coll.find(getSearchQuery()).first(doAfter);
    }

    default void insert(final Map<DatabaseKey, Object> map) {
        insert(map, error -> {
            if (getLogger().logIf(error != null,
                    "Could not insert new `%s` data with entries `%s`:",
                    MongoIdentity.this, map))
                error.printStackTrace();
        });
    }

    default void insert(final Map<DatabaseKey, Object> map,
                        final VoidCallback doAfter) {
        checkNotNull(map, "insert map");

        MongoCollection<Document> coll = getCollection();
        if (coll == null) {
            doAfter.onResult();
            return;
        }

        Document insert = new Document();
        for (Map.Entry<DatabaseKey, Object> entry : map.entrySet()) {
            insert.append(entry.getKey().getKey(), entry.getValue());
        }

        coll.insertOne(insert, doAfter);
    }

    default void update(final DatabaseKey stat,
                        final Object value) {
        update(stat, new Value<>(value));
    }

    default void update(final DatabaseKey stat,
                        final Value value) {
        update(stat, value, (result, error) -> {
            if (getLogger().logIf(error != null,
                    "Could not update `%s` data with value `%s:%s`:",
                    MongoIdentity.this, value, value.getType()))
                error.printStackTrace();
        });
    }

    default void update(final DatabaseKey stat,
                        final Object value,
                        final DatabaseCallback<UpdateResult> doAfter) {
        update(stat, new Value<>(value), doAfter);
    }

    default void update(final DatabaseKey stat,
                        final Value value,
                        final DatabaseCallback<UpdateResult> doAfter) {
        Object obj = value.getValue();
        MongoDataOperator operator = MongoDataOperator.$SET;
        ValueType type = value.getType();
        if (value.isInteger() && type != ValueType.SET) {
            int i = (Integer) obj;
            if (type.equals(ValueType.TAKE)) i = -i;
            obj = i;
        }

        update(stat, obj, operator, doAfter);
    }

    default void update(final DatabaseKey stat,
                        final Object obj,
                        final MongoDataOperator operator) {
        update(stat, obj, operator, (result, error) -> {
            if (getLogger().logIf(error != null,
                    "Could not update `%s` data with value `%s:%s`:",
                    MongoIdentity.this, obj, operator))
                error.printStackTrace();
        });
    }

    default void update(final DatabaseKey stat,
                        final Object obj,
                        final MongoDataOperator operator,
                        final DatabaseCallback<UpdateResult> doAfter) {
        checkNotNull(stat);

        MongoCollection<Document> coll = getCollection();
        if (coll == null) {
            doAfter.onResult(new VoidUpdateResult());
            return;
        }

        coll.updateOne(getSearchQuery(),
                new Document(operator.getOperator(),
                        new Document(stat.getKey(), obj)), doAfter);
    }

    default void update(final Map<DatabaseKey, Value> operations) {
        update(operations, (result, error) -> {
            if (getLogger().logIf(error != null,
                    "Could not bulk update `%s` data with operations `%s`:",
                    MongoIdentity.this, operations))
                error.printStackTrace();
        });
    }

    default void update(final Map<DatabaseKey, Value> operations,
                        final DatabaseCallback<BulkWriteResult> doAfter) {
        checkNotNull(operations);

        List<WriteModel<Document>> writeModels = Lists.newArrayListWithCapacity(operations.size());

        Bson searchQuery = getSearchQuery();
        writeModels.addAll(operations
                .entrySet()
                .stream()
                .map(entry -> new UpdateOneModel<Document>(
                        searchQuery,
                        new Document(
                                MongoDataOperator.$SET.getOperator(),
                                new Document(
                                        entry.getKey().getKey(),
                                        entry.getValue().getValue()
                                )
                        )
                )).collect(Collectors.toList())
        );

        update(writeModels, doAfter);
    }

    default void update(final List<WriteModel<Document>> operations) {
        update(operations, (result, error) -> {
            if (getLogger().logIf(error != null,
                    "Could not bulk update `%s` data with operations `%s`:",
                    MongoIdentity.this, operations))
                error.printStackTrace();
        });
    }

    default void update(final List<WriteModel<Document>> operations,
                        final DatabaseCallback<BulkWriteResult> doAfter) {
        checkNotNull(operations);

        MongoCollection<Document> coll = getCollection();
        if (coll == null) {
            doAfter.onResult(new VoidBulkWriteResult());
            return;
        }

        coll.bulkWrite(operations, doAfter);
    }
}