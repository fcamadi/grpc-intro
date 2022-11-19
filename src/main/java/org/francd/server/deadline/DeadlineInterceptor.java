package org.francd.server.deadline;

import io.grpc.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DeadlineInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor,
            CallOptions callOptions,
            Channel channel) {

        Deadline deadline = callOptions.getDeadline();
        if (Objects.isNull(deadline)) {
            System.out.println("In the DeadlineInterceptor - default deadline");
            callOptions = callOptions.withDeadline(Deadline.after(4, TimeUnit.SECONDS));
        }
        System.out.println("In the DeadlineInterceptor - methodDescriptor: "+methodDescriptor.getServiceName());
        return channel.newCall(methodDescriptor, callOptions);
    }
}
