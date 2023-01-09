package org.francd.server.metadata;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import org.francd.model.*;
import org.francd.server.AccountDBMap;
import org.francd.server.CashStreamingRequest;

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

        if (amount < 10 || (amount % 10) != 0) {
            Metadata metadata = new Metadata();
            Metadata.Key<WithdrawalError> errorKey = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());
            WithdrawalError withdrawalError = WithdrawalError.newBuilder()
                    .setAmount(balance)
                    .setErrorMessage(ErrorMessage.ONLY_TEN_MULTIPLES)
                    .build();
            metadata.put(errorKey, withdrawalError);
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
            return;
        }

        if (balance < amount) {
            Metadata metadata = new Metadata();
            Metadata.Key<WithdrawalError> errorKey = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());
            WithdrawalError withdrawalError = WithdrawalError.newBuilder()
                    .setAmount(balance)
                    .setErrorMessage(ErrorMessage.INSUFFICIENT_BALANCE)
                    .build();
            metadata.put(errorKey, withdrawalError);
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
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
