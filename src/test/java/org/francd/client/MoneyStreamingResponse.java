package org.francd.client;

import io.grpc.stub.StreamObserver;
import org.francd.model.Money;

import java.util.concurrent.CountDownLatch;

public class MoneyStreamingResponse implements StreamObserver<Money> {

    private final CountDownLatch latch;

    public MoneyStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Money money) {
        System.out.println("Received async: "+money.getValue());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getLocalizedMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("END async");
        latch.countDown();
    }
}
