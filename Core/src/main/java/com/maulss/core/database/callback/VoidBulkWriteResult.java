/*
 * Part of core.
 * Made on 09/09/2017
 */

package com.maulss.core.database.callback;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.bulk.BulkWriteUpsert;

import java.util.List;

public class VoidBulkWriteResult extends BulkWriteResult {

    @Override
    public boolean wasAcknowledged() {
        return false;
    }

    @Override
    public int getInsertedCount() {
        return 0;
    }

    @Override
    public int getMatchedCount() {
        return 0;
    }

    @Override
    public int getDeletedCount() {
        return 0;
    }

    @Override
    public boolean isModifiedCountAvailable() {
        return false;
    }

    @Override
    public int getModifiedCount() {
        return 0;
    }

    @Override
    public List<BulkWriteUpsert> getUpserts() {
        return null;
    }
}