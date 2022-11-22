package org.francd.server.metadata;

import io.grpc.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String clientToken = metadata.get(ServerConstants.TOKEN);
        System.out.println("Client token received: "+clientToken);

        if (validateToken(clientToken)) {
            return serverCallHandler.startCall(serverCall, metadata);
        } else {
            Status statusKo = Status.UNAUTHENTICATED.withDescription("Invalid token");
            serverCall.close(statusKo, metadata);
            return  new ServerCall.Listener<>() {
            };
        }
        //return null; //do not return null!
    }

    private boolean validateToken(String token) {
        return Objects.nonNull(token) && ("bank-client-token".equals(token));
    }
}
