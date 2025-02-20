/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.indices.breaker;

import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.common.io.stream.Writeable;
import org.opensearch.common.unit.ByteSizeValue;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Locale;

/**
 * Class encapsulating stats about the circuit breaker
 *
 * @opensearch.internal
 */
public class CircuitBreakerStats implements Writeable, ToXContentObject {

    private final String name;
    private final long limit;
    private final long estimated;
    private final long trippedCount;
    private final double overhead;

    public CircuitBreakerStats(String name, long limit, long estimated, double overhead, long trippedCount) {
        this.name = name;
        this.limit = limit;
        this.estimated = estimated;
        this.trippedCount = trippedCount;
        this.overhead = overhead;
    }

    public CircuitBreakerStats(StreamInput in) throws IOException {
        this.limit = in.readLong();
        this.estimated = in.readLong();
        this.overhead = in.readDouble();
        this.trippedCount = in.readLong();
        this.name = in.readString();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeLong(limit);
        out.writeLong(estimated);
        out.writeDouble(overhead);
        out.writeLong(trippedCount);
        out.writeString(name);
    }

    public String getName() {
        return this.name;
    }

    public long getLimit() {
        return this.limit;
    }

    public long getEstimated() {
        return this.estimated;
    }

    public long getTrippedCount() {
        return this.trippedCount;
    }

    public double getOverhead() {
        return this.overhead;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(name.toLowerCase(Locale.ROOT));
        builder.field(Fields.LIMIT, limit);
        builder.field(Fields.LIMIT_HUMAN, new ByteSizeValue(limit));
        builder.field(Fields.ESTIMATED, estimated);
        builder.field(Fields.ESTIMATED_HUMAN, new ByteSizeValue(estimated));
        builder.field(Fields.OVERHEAD, overhead);
        builder.field(Fields.TRIPPED_COUNT, trippedCount);
        builder.endObject();
        return builder;
    }

    @Override
    public String toString() {
        return "["
            + this.name
            + ",limit="
            + this.limit
            + "/"
            + new ByteSizeValue(this.limit)
            + ",estimated="
            + this.estimated
            + "/"
            + new ByteSizeValue(this.estimated)
            + ",overhead="
            + this.overhead
            + ",tripped="
            + this.trippedCount
            + "]";
    }

    /**
     * Fields used for statistics
     *
     * @opensearch.internal
     */
    static final class Fields {
        static final String LIMIT = "limit_size_in_bytes";
        static final String LIMIT_HUMAN = "limit_size";
        static final String ESTIMATED = "estimated_size_in_bytes";
        static final String ESTIMATED_HUMAN = "estimated_size";
        static final String OVERHEAD = "overhead";
        static final String TRIPPED_COUNT = "tripped";
    }
}
