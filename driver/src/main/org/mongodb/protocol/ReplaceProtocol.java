/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.protocol;

import org.mongodb.Document;
import org.mongodb.Encoder;
import org.mongodb.MongoNamespace;
import org.mongodb.WriteConcern;
import org.mongodb.WriteResult;
import org.mongodb.connection.Connection;
import org.mongodb.connection.ServerDescription;
import org.mongodb.diagnostics.Loggers;
import org.mongodb.diagnostics.logging.Logger;
import org.mongodb.operation.ReplaceRequest;
import org.mongodb.protocol.message.MessageSettings;
import org.mongodb.protocol.message.ReplaceMessage;
import org.mongodb.protocol.message.RequestMessage;

import java.util.List;

import static java.lang.String.format;

public class ReplaceProtocol<T> extends WriteProtocol {
    private static final org.mongodb.diagnostics.logging.Logger LOGGER = Loggers.getLogger("protocol.replace");

    private final List<ReplaceRequest<T>> replaceRequests;
    private final Encoder<Document> queryEncoder;
    private final Encoder<T> encoder;

    public ReplaceProtocol(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern,
                           final List<ReplaceRequest<T>> replaceRequests, final Encoder<Document> queryEncoder, final Encoder<T> encoder) {
        super(namespace, ordered, writeConcern);
        this.replaceRequests = replaceRequests;
        this.queryEncoder = queryEncoder;
        this.encoder = encoder;
    }

    @Override
    public WriteResult execute(final Connection connection, final ServerDescription serverDescription) {
        LOGGER.debug(format("Replacing document in namespace %s on connection [%s] to server %s", getNamespace(), connection.getId(),
                            connection.getServerAddress()));
        WriteResult writeResult = super.execute(connection, serverDescription);
        LOGGER.debug("Replace completed");
        return writeResult;
    }

    @Override
    protected RequestMessage createRequestMessage(final MessageSettings settings) {
        return new ReplaceMessage<T>(getNamespace().getFullName(), replaceRequests, queryEncoder, encoder, settings);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}