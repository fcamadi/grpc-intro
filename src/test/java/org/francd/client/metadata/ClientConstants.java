package org.francd.client.metadata;

import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;
import org.francd.model.WithdrawalError;

public class ClientConstants {

    public static final Metadata.Key<String> USER_TOKEN = Metadata.Key.of("user-token", Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<WithdrawalError> WITHDRAWAL_ERROR_KEY = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());

    private  static  final Metadata METADATA = new Metadata();

    static {
        METADATA.put(Metadata.Key.of("client-token", Metadata.ASCII_STRING_MARSHALLER), "bank-client-token");
    }

    public static Metadata getMetadataWithClientToken() {
        return METADATA;
    }
}
