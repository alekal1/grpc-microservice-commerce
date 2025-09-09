package ee.aleksale.inventory.service.grpc;

import com.google.protobuf.Any;
import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.common.response.proto.v1.ErrorResponse;
import ee.aleksale.common.response.proto.v1.SuccessResponse;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.proto.v1.InventoryManagementServiceGrpc;
import ee.aleksale.inventory.service.InventoryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService(interceptorNames = "commerceSecretManagementCallInterceptor")
@RequiredArgsConstructor
public class InventoryManagementGrpcService extends InventoryManagementServiceGrpc.InventoryManagementServiceImplBase {

    private final InventoryService inventoryService;

    @Override
    public StreamObserver<InventoryUnit> addInventoryUnit(StreamObserver<CommerceResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(InventoryUnit unit) {
                log.info("addInventoryUnit - onNext");

                CommerceResponse response;
                try {
                    var savedInventory = inventoryService.saveInventory(unit);

                    response = CommerceResponse.newBuilder()
                            .setSuccess(SuccessResponse.newBuilder()
                                    .addData(Any.pack(savedInventory))
                                    .build())
                            .build();
                } catch (Exception e) {
                    log.info("addInventoryUnit - onNext exception", e);

                    response = CommerceResponse.newBuilder()
                            .setError(ErrorResponse.newBuilder()
                                    .setCode(400)
                                    .setMessage(e.getMessage())
                                    .build())
                            .build();
                }

                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("addInventoryUnit - onError", throwable);
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                log.info("addInventoryUnit - onCompleted");
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<InventoryUnit> removeInventoryUnit(StreamObserver<CommerceResponse> responseObserver) {
        return new StreamObserver<>() {

            @Override
            public void onNext(InventoryUnit unit) {
                log.info("removeInventoryUnit - onNext");

                CommerceResponse response;

                try {
                    inventoryService.removeInventory(unit);

                    response = CommerceResponse.newBuilder()
                            .setSuccess(SuccessResponse.newBuilder()
                                    .setMessage("Successfully deleted desired quantity.")
                                    .build())
                            .build();

                } catch (InventoryException ie) {
                    response = CommerceResponse.newBuilder()
                            .setError(ErrorResponse.newBuilder()
                                    .setCode(400)
                                    .setMessage(ie.getMessage())
                                    .build())
                            .build();
                }

                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("removeInventory - onError", throwable);
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                log.info("removeInventory - onCompleted");
                responseObserver.onCompleted();
            }
        };
    }
}
