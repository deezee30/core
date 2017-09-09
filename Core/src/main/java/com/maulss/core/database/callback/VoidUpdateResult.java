/*
 * Part of core.
 * Made on 09/09/2017
 */

package com.maulss.core.database.callback;

import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;

public class VoidUpdateResult extends UpdateResult {

    @Override
    public boolean wasAcknowledged() {
        return false;
    }

    @Override
    public long getMatchedCount() {
        return 0;
    }

    @Override
    public boolean isModifiedCountAvailable() {
        return false;
    }

    @Override
    public long getModifiedCount() {
        return 0;
    }

    @Override
    public BsonValue getUpsertedId() {
        return null;
    }
}