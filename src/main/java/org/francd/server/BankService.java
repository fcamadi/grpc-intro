package org.francd.server;

import io.grpc.stub.StreamObserver;
import org.francd.model.*;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

        int accountNumber = request.getAccountNumber();
        Balance balance = Balance.newBuilder()
                .setAmount(AccountDBMap.getBalance(accountNumber))
                .build();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    @Override
    public void withDraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {

        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount();
        int balance = AccountDBMap.getBalance(accountNumber);

        for (int i = 0; i < (amount / 10); i++) {
            Money.newBuilder().setValue(10).build();
            responseObserver.onNext(Money.newBuilder().build());
            AccountDBMap.deduceBalance(accountNumber, 10);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }
}
