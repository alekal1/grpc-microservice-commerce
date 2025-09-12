package ee.aleksale.paymentimpl.interceptors.service;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetrieveIdentificationInterceptor implements ServerInterceptor {

    public static final Context.Key<String> ID_CONTEXT_KEY = Context.key("IDENTIFICATION_CODE");
    private static final Metadata.Key<String> ID_HEADER_KEY =
            Metadata.Key.of("IDENTIFICATION_CODE", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        if (!identificationCodeIsPresent(metadata)) {
            return handleInvalidIdentificationKey(serverCall);
        }

        var ctx = Context.current().withValue(ID_CONTEXT_KEY, metadata.get(ID_HEADER_KEY));
        return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
    }

    private boolean identificationCodeIsPresent(Metadata metadata) {
        if (metadata == null) {
            return false;
        }

        var id = metadata.get(ID_HEADER_KEY);

        return id != null && !id.isEmpty();
    }

    private static <ReqT, RespT> ServerCall.Listener<ReqT> handleInvalidIdentificationKey(ServerCall<ReqT, RespT> serverCall) {
        serverCall.close(Status.PERMISSION_DENIED
                        .withDescription("Error forwarding IDENTIFICATION_CODE"),
                new Metadata());

        return new ServerCall.Listener<>() {
        };
    }

}
