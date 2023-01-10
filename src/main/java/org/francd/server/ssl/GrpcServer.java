package org.francd.server.ssl;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import org.francd.server.metadata.MetadataService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        URL crtUrl = GrpcServer.class.getClassLoader().getResource("localhost.crt"); // server localhost signed certificate
        File localhostCrt = new File(crtUrl.toURI());
        URL pemUrl = GrpcServer.class.getClassLoader().getResource("localhost.pem"); // server localhost key in PKCS8 standard
        File localhostPem = new File(pemUrl.toURI());

        SslContext sslContext = GrpcSslContexts
                .configure(
                    SslContextBuilder.forServer(localhostCrt, localhostPem)
                )
                .build();

        Server server = NettyServerBuilder.forPort(6565)
                .sslContext(sslContext)
                .addService(new MetadataService())
                .build();

        server.start();

        server.awaitTermination();
    }
}
