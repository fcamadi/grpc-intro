package org.francd.client;

import io.grpc.stub.StreamObserver;
import org.francd.model.Balance;

import java.util.concurrent.CountDownLatch;

public class BalanceStreamObserver implements StreamObserver<Balance> {

    private final CountDownLatch latch;

    public BalanceStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Balance balance) {
        System.out.println("Balance: "+balance.getAmount());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error: "+throwable.getLocalizedMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server END");
        latch.countDown();
    }
}
