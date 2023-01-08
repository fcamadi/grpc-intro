package org.francd.server.metadata;

import io.grpc.Context;
import io.grpc.Metadata;

public class ServerConstants {

    public static final Metadata.Key<String> TOKEN =
            Metadata.Key.of("client-token", Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> USER_TOKEN =
            Metadata.Key.of("user-token", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<UserRole> CTX_USER_ROLE =  Context.key("user-role");
    // only for demostration purposes:
    public static final Context.Key<UserRole> CTX_USER_ROLE_B =  Context.key("user-role");
}
