package ee.aleksale.credentials;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executor;

public class CommerceSecretCallCredentials extends CallCredentials {

    private static final Metadata.Key<byte[]> SYSTEM_KEY = Metadata.Key.of("system-bin", Metadata.BINARY_BYTE_MARSHALLER);

    private final String systemSecret;

    public CommerceSecretCallCredentials(String systemSecret) {
        this.systemSecret = systemSecret;
    }

    @Override
    public void applyRequestMetadata(CallCredentials.RequestInfo requestInfo,
                                     Executor appExecutor,
                                     CallCredentials.MetadataApplier applier) {
        appExecutor.execute(() -> {
            try {
                var headers = new Metadata();
                var encodedSecret = Base64.getEncoder().encode(
                        this.systemSecret.getBytes(StandardCharsets.UTF_8)
                );
                headers.put(SYSTEM_KEY, encodedSecret);
                applier.apply(headers);
            } catch (Exception ex) {
                applier.fail(Status.INVALID_ARGUMENT.withCause(ex));
            }
        });
    }
}
