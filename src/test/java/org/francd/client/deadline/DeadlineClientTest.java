package org.francd.client.deadline;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.francd.client.MoneyStreamingResponse;
import org.francd.model.Balance;
import org.francd.model.BalanceCheckRequest;
import org.francd.model.BankServiceGrpc;
import org.francd.model.WithdrawRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeadlineClientTest {

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

        try {
            Balance balanceResponse = bankServiceBlockingStub
                    .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                    .getBalance(balanceRequest);
            System.out.println("Received balance amount: " + balanceResponse.getAmount());
        } catch (StatusRuntimeException e) {
            //do something meaningful ..
            System.out.println("Something went wrong: "+e.getStatus().getDescription());
        }
    }

    @Test
    void withdrawTest() {
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(6)
                .setAmount(50)
                .build();

        try {
            bankServiceBlockingStub
                    .withDeadlineAfter(9, TimeUnit.SECONDS)
                    .withDraw(withdrawRequest)
                    .forEachRemaining(money -> System.out.println("Received: " + money.getValue()));
        } catch (StatusRuntimeException e) {
            //do something meaningful ..
            System.out.println("Something went wrong: "+e.getStatus().getDescription());
        }
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

}
