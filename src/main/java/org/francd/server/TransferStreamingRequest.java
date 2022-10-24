package org.francd.server;

import io.grpc.stub.StreamObserver;
import org.francd.model.Account;
import org.francd.model.TransferRequest;
import org.francd.model.TransferResponse;
import org.francd.model.TransferStatus;

import java.util.List;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {

    private final StreamObserver<TransferResponse> transferResponseStreamObserver;

    public TransferStreamingRequest(StreamObserver<TransferResponse> transferResponseStreamObserver) {
        this.transferResponseStreamObserver = transferResponseStreamObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        int fromAccountId = transferRequest.getFromAccount();
        int toAccountId = transferRequest.getToAccount();
        int amount = transferRequest.getAmount();
        int balance = AccountDBMap.getBalance(fromAccountId);
        TransferStatus status = TransferStatus.FAILED;

        if (balance > amount && (fromAccountId != toAccountId)) {
            AccountDBMap.deduceBalance(fromAccountId, amount);
            AccountDBMap.addBalance(toAccountId, amount);
            status = TransferStatus.SUCCESS;
        }

        Account fromAccount = Account.newBuilder()
                .setAccountNumber(fromAccountId)
                .setAmount(AccountDBMap.getBalance(fromAccountId))
                .build();
        Account toAccount = Account.newBuilder()
                .setAccountNumber(toAccountId)
                .setAmount(AccountDBMap.getBalance(toAccountId))
                .build();

        TransferResponse transferResponse = TransferResponse.newBuilder()
                .setStatus(status)
                .addAllAccounts(List.of(fromAccount, toAccount))
                .build();

        transferResponseStreamObserver.onNext(transferResponse);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Error: "+throwable.getLocalizedMessage());
    }

    @Override
    public void onCompleted() {
        AccountDBMap.printAccountsDetails();
        transferResponseStreamObserver.onCompleted();
    }
}
