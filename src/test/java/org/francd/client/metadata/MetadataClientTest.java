package org.francd.client.metadata;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.francd.model.Balance;
import org.francd.model.BalanceCheckRequest;
import org.francd.model.BankServiceGrpc;
import org.francd.model.WithdrawRequest;
import org.francd.server.deadline.DeadlineInterceptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataClientTest {

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;

    @BeforeAll
    void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                //.intercept(new DeadlineInterceptor())
                .intercept(MetadataUtils.newAttachHeadersInterceptor(ClientConstants.getMetadataWithClientToken()))
                .usePlaintext()
                .build();
        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    }

    @Test
    void balanceTest() {
        BalanceCheckRequest balanceRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(7)
                .build();

        try {
            Balance balanceResponse = bankServiceBlockingStub.getBalance(balanceRequest);
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
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .withDraw(withdrawRequest)
                    .forEachRemaining(money -> System.out.println("Received: " + money.getValue()));
        } catch (StatusRuntimeException e) {
            //do something meaningful ..
            System.out.println("Something went wrong: "+e.getStatus().getDescription());
        }
    }

}
