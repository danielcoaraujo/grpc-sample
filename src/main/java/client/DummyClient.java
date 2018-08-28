package client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DummyClient {

    public static void main(String[] args) {
        System.out.println("I'm a gRPC client ...");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        System.out.println("Creating stub");

    }
}
