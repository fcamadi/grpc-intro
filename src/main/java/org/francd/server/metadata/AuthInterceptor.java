package org.francd.server.metadata;

import io.grpc.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/*
    user-secret-2:STANDARD
    user-secret-3:PREMIUM
 */
public class AuthInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        //String clientToken = metadata.get(ServerConstants.TOKEN); user token could be handled in another interceptor
        String clientToken = metadata.get(ServerConstants.USER_TOKEN);

        System.out.println("Client token received: "+clientToken);

        if (validateToken(clientToken)) {
            UserRole userRole = extractUserRole(clientToken);
            //store the role in the context:
            Context context = Context.current().withValue(ServerConstants.CTX_USER_ROLE, userRole);
            //return serverCallHandler.startCall(serverCall, metadata);
            //now we pass the context to the service layer
            return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
        } else {
            Status statusKo = Status.UNAUTHENTICATED.withDescription("Invalid token");
            serverCall.close(statusKo, metadata);
            return  new ServerCall.Listener<>() {
            };
        }
        //return null; //do not return null!
    }

    private boolean validateToken(String token) {
        //return Objects.nonNull(token) && ("bank-client-token".equals(token));
        return Objects.nonNull(token) &&
                ( token.startsWith("user-secret-2") ||  token.startsWith("user-secret-3") ) ;
    }

    private UserRole extractUserRole(String jwt) {
        return jwt.endsWith(UserRole.PREMIUM.name()) ? UserRole.PREMIUM : UserRole.STANDARD;
    }
}
