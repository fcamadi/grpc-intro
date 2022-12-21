package org.francd.server.metadata;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import org.francd.client.metadata.ClientConstants;

import java.util.concurrent.Executor;

public class UserSessionToken extends CallCredentials {

    private final String jwt;

    public UserSessionToken(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        executor.execute( () -> {
            Metadata metadata = new Metadata();
            metadata.put(ClientConstants.USER_TOKEN, this.jwt);
            metadataApplier.apply(metadata);
            //this could be done too: metadataApplier.fail(Status.UNAUTHENTICATED);
        });
    }

    @Override
    public void thisUsesUnstableApi() {
        //this is to show this API may change in the future
    }
}
