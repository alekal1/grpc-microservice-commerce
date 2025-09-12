package ee.aleksale.client.interceptors.server;

import ee.aleksale.client.repository.ClientRepository;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
    public class IdentificationHeaderInterceptor implements ServerInterceptor {

    private final ClientRepository clientRepository;

    private static final Metadata.Key<String> ID_HEADER_KEY =
            Metadata.Key.of("IDENTIFICATION_CODE", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<String> ID_CONTEXT_KEY = Context.key("IDENTIFICATION_CODE");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        log.info("Metadata received from client {}", metadata);

        if (!isValidIdentificationCode(metadata)) {
            return handleInvalidIdentificationKey(serverCall);
        }

        var ctx = Context.current().withValue(ID_CONTEXT_KEY, metadata.get(ID_HEADER_KEY));
        return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
    }

    private boolean isValidIdentificationCode(Metadata metadata) {
        var value = metadata.get(ID_HEADER_KEY);
        if (value == null || StringUtils.isBlank(value)) {
            return false;
        }

        UUID uuidValue;
        try {
            uuidValue = UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            log.error("IdentificationHeaderInterceptor {}", ex.getMessage(), ex);
            return false;
        }
        return clientRepository.findByIdentifierCode(uuidValue).isPresent();
    }

    private static <ReqT, RespT> ServerCall.Listener<ReqT> handleInvalidIdentificationKey(ServerCall<ReqT, RespT> serverCall) {
        serverCall.close(Status.PERMISSION_DENIED
                        .withDescription("Please provide valid IDENTIFICATION_CODE value in metadata."),
                new Metadata());

        return new ServerCall.Listener<>() {
        };
    }
}
