package org.francd.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.francd.model.*;
import org.francd.server.AccountDBMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;

    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    void balanceTest() {
        BalanceCheckRequest balanceRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(5)
                .build();

        Balance balanceResponse = bankServiceBlockingStub.getBalance(balanceRequest);
        System.out.println("Received balance amount: " + balanceResponse.getAmount());
    }

    @Test
    void withdrawTest() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(7)
                .setAmount(80)
                .build();
        // Iterator<Money> moneyIterator = bankServiceBlockingStub.withDraw(withdrawRequest);
        bankServiceBlockingStub.withDraw(withdrawRequest)
                .forEachRemaining(money -> System.out.println("Received: " + money.getValue()));
    }

    @Test
    void whenWithdrawMoneyAndNotEnoughBalance_ThrowException() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(3)
                .setAmount(40)
                .build();

        StatusRuntimeException thrown = assertThrows(
                StatusRuntimeException.class,
                () -> bankServiceBlockingStub.withDraw(withdrawRequest)
                        .forEachRemaining(money -> System.out.println("Received: " + money.getValue())));

        assertTrue(thrown.getMessage().contains("FAILED_PRECONDITION"));
    }

    @Test
    void withdrawAsyncTest() throws InterruptedException {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(9)
                .setAmount(50)
                .build();

        CountDownLatch latch = new CountDownLatch(1);

        // The async version needs a second param as a callback method to be called on upon each responwe received:
        //
        // public void withDraw(org.francd.model.WithdrawRequest request,
        //        io.grpc.stub.StreamObserver<org.francd.model.Money> responseObserver) { ..
        //
        // So we implement it and use it here
        bankServiceStub.withDraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
    }

    @Test
    void cashStreamingRequestTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest> depositRequestStreamObserver = bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));
        for (int i = 0; i < 10; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder()
                    .setAccountNumber(8)
                    .setAmount(10)
                    .build();
            depositRequestStreamObserver.onNext(depositRequest);
            System.out.println("depositRequestStreamObserver - amount in bank account: "+ AccountDBMap.getBalance(8));
        }
        depositRequestStreamObserver.onCompleted();
        latch.await();
        System.out.println("cashStreamingRequestTest END");
    }

}
