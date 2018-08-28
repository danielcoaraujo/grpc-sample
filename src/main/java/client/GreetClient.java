package client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetClient {

    public static void main(String[] args) {
        System.out.println("Greet client!");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();

//        UnaryCall(channel);
//        serverStreaming(channel);
        clientStreaming(channel);

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private static void UnaryCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub client = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Daniel")
                .setLastName("Araujo")
                .build();

        GreetRequest request = GreetRequest
                .newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse response = client.greet(request);
        System.out.println(response.getResult());
    }

    private static void serverStreaming(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub client = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Daniel")
                .setLastName("Araujo")
                .build();

        GreetingRequestManyTimes request = GreetingRequestManyTimes.newBuilder()
                .setGreeting(greeting)
                .build();

        client.greetManyTimes(request)
                .forEachRemaining(response -> System.out.println(response.getResult()));
    }

    private static void clientStreaming(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub client = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver = client.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                System.out.println("Received a response from the server.");
                System.out.println(longGreetResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Completed the request.");
                latch.countDown();
            }
        });


        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Daniel")
                .setLastName("Araujo")
                .build();

        Greeting greeting2 = Greeting.newBuilder()
                .setFirstName("Rafael")
                .setLastName("Araujo")
                .build();

        Greeting greeting3 = Greeting.newBuilder()
                .setFirstName("Michelle")
                .setLastName("Araujo")
                .build();

        requestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(greeting).build());
        requestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(greeting2).build());
        requestStreamObserver.onNext(LongGreetRequest.newBuilder().setGreeting(greeting3).build());

        requestStreamObserver.onCompleted();

        try{
            latch.await(3L, TimeUnit.SECONDS);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
