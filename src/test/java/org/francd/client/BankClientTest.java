package org.francd.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.francd.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;

    @BeforeAll
    void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
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
}
