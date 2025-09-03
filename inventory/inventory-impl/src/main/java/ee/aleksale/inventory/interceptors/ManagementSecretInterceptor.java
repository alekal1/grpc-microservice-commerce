package ee.aleksale.inventory.interceptors;

import ee.aleksale.inventory.config.InventoryConfig;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagementSecretInterceptor implements ServerInterceptor {

    private final InventoryConfig inventoryConfig;

    private static final Metadata.Key<byte[]> SYSTEM_KEY =
            Metadata.Key.of("system-bin", Metadata.BINARY_BYTE_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        if (!isValidSystem(metadata)) {
            return handleInvalidSystemKey(call);
        }
        var delegate = next.startCall(call, metadata);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {};
    }

    private boolean isValidSystem(Metadata metadata) {
        var value = metadata.get(SYSTEM_KEY);
        if (value == null) {
            return false;
        }

        var decoded = Base64.getDecoder().decode(value);
        var systemValue = new String(decoded, StandardCharsets.UTF_8);

        return systemValue.equals(inventoryConfig.getManagementSystemSecret());
    }

    private static <ReqT, RespT> ServerCall.Listener<ReqT> handleInvalidSystemKey(ServerCall<ReqT, RespT> serverCall) {
        serverCall.close(Status.PERMISSION_DENIED
                        .withDescription("Only dedicated services allowed to use this method."),
                new Metadata());

        return new ServerCall.Listener<>() {
        };
    }
}
