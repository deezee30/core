/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database.mongo;

import com.google.common.base.Preconditions;
import com.maulss.core.database.mongo.query.BulkWriteQuery;
import com.maulss.core.database.mongo.query.DocumentSearchQuery;
import com.maulss.core.database.mongo.query.MongoQuery;
import com.maulss.core.database.mongo.query.SingleUpdateQuery;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MongoAccessThread extends Thread {

    public static final Queue<MongoQuery<?>> CONCURRENT_QUERIES = new ConcurrentLinkedQueue<>();

    public final static UpdateOptions UPDATE_OPTIONS = new UpdateOptions().upsert(true);

    public static void submitQuery(final MongoQuery<?> query) {
        CONCURRENT_QUERIES.add(Preconditions.checkNotNull(query));
    }

    MongoAccessThread() {}

    @Override
    public void run() {
        while (true) {
            try {
                // ALLOW THREAD TO SLEEP FOR 15ms BEFORE CONTINUING QUEUE //
                Thread.sleep(15L);
            } catch (InterruptedException ignored) {}

            while (!CONCURRENT_QUERIES.isEmpty()) {
                MongoQuery<?> query = CONCURRENT_QUERIES.poll();
                if (query == null) continue;

                if (query instanceof SingleUpdateQuery) {
                    SingleUpdateQuery<UpdateResult> updateQuery = (SingleUpdateQuery<UpdateResult>) query;
                    updateQuery.getCollection().updateOne(
                            updateQuery.getSearchQuery(),
                            updateQuery.getNewDocument(),
                            UPDATE_OPTIONS,
                            updateQuery.getDoAfter()
                    );

                } else if (query instanceof DocumentSearchQuery) {
                    DocumentSearchQuery documentSearchQuery = (DocumentSearchQuery) query;
                    documentSearchQuery.getCollection()
                            .find(documentSearchQuery.getSearchQuery())
                            .first(documentSearchQuery.getDoAfter());

                } else if (query instanceof BulkWriteQuery) {
                    BulkWriteQuery<BulkWriteResult> bulkWriteQuery = (BulkWriteQuery<BulkWriteResult>) query;
                    bulkWriteQuery.getCollection().bulkWrite(
                            bulkWriteQuery.getModels(),
                            bulkWriteQuery.getDoAfter()
                    );
                }
            }
        }
    }
}
