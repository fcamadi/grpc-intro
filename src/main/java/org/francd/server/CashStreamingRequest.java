package org.francd.server;

import io.grpc.stub.StreamObserver;
import org.francd.model.Balance;
import org.francd.model.DepositRequest;
import org.francd.server.AccountDBMap;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

    private final StreamObserver<Balance> balanceStreamObserver;

    private int accountBalance;

    public CashStreamingRequest(StreamObserver<Balance> balanceStreamObserver) {
        this.balanceStreamObserver = balanceStreamObserver;
    }

    @Override
    public void onNext(DepositRequest depositRequest) {
        int accountNumber = depositRequest.getAccountNumber();
        int amount = depositRequest.getAmount();
        accountBalance = AccountDBMap.addBalance(accountNumber, amount);
        System.out.println("AccountNumber[AccountBalance]: "+accountNumber+"["+accountBalance+"]");
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("CashStreamingRequest Error: "+throwable.getLocalizedMessage());
    }

    @Override
    public void onCompleted() {
        Balance balance = Balance.newBuilder()
                .setAmount(this.accountBalance)
                .build();
        balanceStreamObserver.onNext(balance);
        balanceStreamObserver.onCompleted();
        System.out.println("Final AccountBalance: "+this.accountBalance);
    }
}
