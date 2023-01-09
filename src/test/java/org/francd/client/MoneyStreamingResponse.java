package org.francd.client;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.francd.client.metadata.ClientConstants;
import org.francd.model.Money;
import org.francd.model.WithdrawalError;

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

        //Thanks to io.grpc.Status we can obtain Status and Metadata from a Throwable
        //Status status = Status.fromThrowable(throwable); // we don't need this now
        Metadata metadata = Status.trailersFromThrowable(throwable);
        WithdrawalError withdrawalError = metadata.get(ClientConstants.WITHDRAWAL_ERROR_KEY);

        System.out.println("withdrawalError: "+withdrawalError.getAmount()+ " - "+withdrawalError.getErrorMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("END async");
        latch.countDown();
    }
}
