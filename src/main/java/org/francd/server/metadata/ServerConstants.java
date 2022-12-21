package org.francd.server.metadata;

import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;

public class ServerConstants {

    public static final Metadata.Key<String> TOKEN =
            Metadata.Key.of("client-token", Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> USER_TOKEN =
            Metadata.Key.of("user-token", Metadata.ASCII_STRING_MARSHALLER);


}
