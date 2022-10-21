package org.francd.client;

import io.grpc.stub.StreamObserver;
import org.francd.model.Money;

public class MoneyStreamingResponse implements StreamObserver<Money> {

    @Override
    public void onNext(Money money) {
        System.out.println("Received async: "+money.getValue());
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getLocalizedMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("END async");
    }
}
