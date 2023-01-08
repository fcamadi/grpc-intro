package org.francd.server.metadata;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.francd.model.*;
import org.francd.server.AccountDBMap;
import org.francd.server.CashStreamingRequest;

import java.util.concurrent.TimeUnit;

public class MetadataService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

        int accountNumber = request.getAccountNumber();
        int amount = AccountDBMap.getBalance(accountNumber);

        // get role from context
        UserRole userRole = ServerConstants.CTX_USER_ROLE.get();
        // only for demostration purposes, because CTX_USER_ROLE_B was not set
        UserRole userRoleB = ServerConstants.CTX_USER_ROLE_B.get();

        amount = UserRole.PREMIUM.equals(userRole) ? amount : amount - 15;

        System.out.println("userRole: "+userRole);
        System.out.println("userRoleB: "+userRoleB);

        Balance balance = Balance.newBuilder()
                .setAmount(amount)
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
            //simulate a high load
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            if (Context.current().isCancelled()) {
                System.out.println("No one is listening! We stop the machine!");
                break;
            }
            responseObserver.onNext(Money.newBuilder().setValue(10).build());
            System.out.println("Delivered 10#");
            AccountDBMap.deduceBalance(accountNumber, 10);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver);
    }
}
