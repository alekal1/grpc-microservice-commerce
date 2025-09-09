package ee.aleksale.inventory.service.grpc;

import com.google.protobuf.Any;
import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.common.response.proto.v1.ErrorResponse;
import ee.aleksale.common.response.proto.v1.SuccessResponse;
import ee.aleksale.inventory.proto.v1.InventoryAvailabilityServiceGrpc;
import ee.aleksale.inventory.service.InventoryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService(interceptorNames = "commerceSecretOrderCallInterceptor")
@RequiredArgsConstructor
public class InventoryAvailabilityGrpcService
        extends InventoryAvailabilityServiceGrpc.InventoryAvailabilityServiceImplBase {

    private final InventoryService inventoryService;

    @Override
    public void checkAvailability(InventoryOrder request, StreamObserver<CommerceResponse> responseObserver) {
        var responseBuilder = CommerceResponse.newBuilder();

        try {
            var unit = inventoryService.getInventory(request);

            if (unit == null) {
                responseBuilder.setError(ErrorResponse.newBuilder()
                        .setCode(404)
                        .setMessage("Unit not found.")
                        .build());
            } else {
                responseBuilder.setSuccess(SuccessResponse.newBuilder()
                        .addData(Any.pack(unit))
                        .build());
            }
        } catch (Exception e) {
            responseBuilder.setError(ErrorResponse.newBuilder()
                    .setCode(400)
                    .setMessage(e.getMessage())
                    .build());
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
