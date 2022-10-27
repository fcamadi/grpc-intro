package org.francd.client;

import io.grpc.stub.StreamObserver;
import org.francd.model.Account;
import org.francd.model.TransferResponse;

import java.util.concurrent.CountDownLatch;

public class TransferStreamingResponse implements StreamObserver<TransferResponse> {

    private final CountDownLatch latch;

    public TransferStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(TransferResponse transferResponse) {
        System.out.println("Status : "+transferResponse.getStatus());
        for (Account account : transferResponse.getAccountsList()) {
            System.out.println("Account Number[amount]: "+account.getAccountNumber()+"["+account.getAmount()+"]");
        }
        System.out.println("----------------------------------------");
    }

    @Override
    public void onError(Throwable throwable) {
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("All transfers done!!!");
        latch.countDown();
    }
}
