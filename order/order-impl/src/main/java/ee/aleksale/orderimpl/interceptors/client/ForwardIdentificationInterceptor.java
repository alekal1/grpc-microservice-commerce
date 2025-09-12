package ee.aleksale.orderimpl.interceptors.client;

import ee.aleksale.orderimpl.interceptors.service.RetrieveIdentificationInterceptor;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ForwardIdentificationInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> ID_HEADER_KEY =
            Metadata.Key.of("IDENTIFICATION_CODE", Metadata.ASCII_STRING_MARSHALLER);


    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String idFromContext = RetrieveIdentificationInterceptor.ID_CONTEXT_KEY.get();
                if (idFromContext != null && !idFromContext.isBlank()) {
                    headers.put(ID_HEADER_KEY, idFromContext);
                } else {
                    log.debug("No IDENTIFICATION_CODE found in Context for method {}", method.getFullMethodName());
                }
                super.start(responseListener, headers);
            }
        };
    }

}
