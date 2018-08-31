package server;

import com.proto.greet.*;
import io.grpc.Status;
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

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        return new StreamObserver<GreetEveryoneRequest>() {

            @Override
            public void onNext(GreetEveryoneRequest value) {
                System.out.println("Sending treated message.");
                GreetEveryoneResponse response = GreetEveryoneResponse.newBuilder()
                        .setResult("Hello " +  value.getGretting().getFirstName())
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                // nothing for now.
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }

        };
    }

    @Override
    public void greetErrorWhenIsEmpty(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        if(request.getGreeting().getFirstName().isEmpty()){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The value mustn't be null.")
                    .asRuntimeException());
        }else{
            responseObserver.onNext(GreetResponse.newBuilder()
                    .setResult("Tudo certo.")
                    .build());
        }
    }
}
