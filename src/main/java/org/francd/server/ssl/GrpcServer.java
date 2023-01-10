package org.francd.server.ssl;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.francd.server.metadata.MetadataService;

import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(6565)
                .addService(new MetadataService())
                .build();

        server.start();

        server.awaitTermination();
    }
}
