package ee.aleksale.client.config;

import ee.aleksale.order.proto.v1.OrderServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public OrderServiceGrpc.OrderServiceStub orderServiceStub(
            GrpcChannelFactory channels
    ) {
        return OrderServiceGrpc.newStub(channels.createChannel("order"));
    }
}
