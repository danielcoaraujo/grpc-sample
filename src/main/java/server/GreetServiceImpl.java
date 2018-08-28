package server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();

        GreetResponse response = GreetResponse.newBuilder()
                .setResult("Hello " + firstName + " " + lastName)
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetingRequestManyTimes request, StreamObserver<GreetingResponseManyTimes> responseObserver) {
        try {
            for(int i=0; i<10; i++){
                String result = "Hello " + request.getGreeting().getFirstName() + " - Time: " + i;
                GreetingResponseManyTimes response = GreetingResponseManyTimes.newBuilder().setResult(result).build();
                Thread.sleep(1000L);
                responseObserver.onNext(response);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        return new StreamObserver<LongGreetRequest>() {

            String result = "";

            @Override
            public void onNext(LongGreetRequest longGreetRequest) {
                result += "Hello " + longGreetRequest.getGreeting().getFirstName() + "!";
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                responseObserver.onCompleted();
            }
        };
    }
}
