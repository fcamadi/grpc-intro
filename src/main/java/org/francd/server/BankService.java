package org.francd.server;

import io.grpc.stub.StreamObserver;
import org.francd.model.Balance;
import org.francd.model.BalanceCheckRequest;
import org.francd.model.BankServiceGrpc;

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
}
