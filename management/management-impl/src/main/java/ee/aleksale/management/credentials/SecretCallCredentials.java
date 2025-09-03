package ee.aleksale.management.credentials;

import ee.aleksale.management.config.ManagementConfig;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class SecretCallCredentials extends CallCredentials {

    private static final Metadata.Key<byte[]> SYSTEM_KEY = Metadata.Key.of("system-bin", Metadata.BINARY_BYTE_MARSHALLER);

    private final ManagementConfig managementConfig;

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo,
                                     Executor appExecutor,
                                     MetadataApplier applier) {
        appExecutor.execute(() -> {
            try {
                var headers = new Metadata();
                var encodedSecret = Base64.getEncoder().encode(
                        managementConfig.getManagementSystemSecret().getBytes(StandardCharsets.UTF_8)
                );
                headers.put(SYSTEM_KEY, encodedSecret);
                applier.apply(headers);
            } catch (Throwable t) {
                applier.fail(Status.INVALID_ARGUMENT.withCause(t));
            }
        });
    }
}
