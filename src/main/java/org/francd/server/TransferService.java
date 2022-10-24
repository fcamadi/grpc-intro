package org.francd.server;

import io.grpc.stub.StreamObserver;
import org.francd.model.TransferRequest;
import org.francd.model.TransferResponse;
import org.francd.model.TransferServiceGrpc;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {

    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
        return new TransferStreamingRequest(responseObserver);
    }
}
