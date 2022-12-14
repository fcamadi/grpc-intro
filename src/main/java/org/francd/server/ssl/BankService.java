package org.francd.server.ssl;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.francd.model.*;
import org.francd.server.AccountDBMap;
import org.francd.server.CashStreamingRequest;

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

        if (balance < amount) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Not enough balance. You have only " + balance);
            responseObserver.onError(status.asRuntimeException());
            return;
        }

        for (int i = 0; i < (amount / 10); i++) {
            responseObserver.onNext(Money.newBuilder().setValue(10).build());
            AccountDBMap.deduceBalance(accountNumber, 10);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver);
    }
}
