package ee.aleksale.management.service;

import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.inventory.proto.v1.InventoryManagementServiceGrpc;
import ee.aleksale.management.proto.v1.InventoryManagementGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ManagementInventoryGrpcService extends InventoryManagementGrpc.InventoryManagementImplBase {

    private final InventoryManagementServiceGrpc.InventoryManagementServiceStub inventoryServiceStub;

    @Override
    public StreamObserver<InventoryUnit> addInventoryUnit(StreamObserver<CommerceResponse> responseObserver) {
        var inventoryResponseObserver = getInventoryResponseObserver(responseObserver);
        var inventoryRequestObserver = inventoryServiceStub.addInventoryUnit(inventoryResponseObserver);

        return new StreamObserver<>() {
            @Override
            public void onNext(InventoryUnit unit) {
                log.info("addInventoryUnit - onNext");
                inventoryRequestObserver.onNext(unit);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("addInventoryUnit - onError", throwable);
                inventoryRequestObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                log.info("addInventoryUnit - onCompleted");
                inventoryRequestObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<InventoryUnit> removeInventoryUnit(StreamObserver<CommerceResponse> responseObserver) {
        var inventoryResponseObserver = getInventoryResponseObserver(responseObserver);
        var inventoryRequestObserver = inventoryServiceStub.removeInventoryUnit(inventoryResponseObserver);

        return new StreamObserver<>() {
            @Override
            public void onNext(InventoryUnit unit) {
                log.info("removeInventoryUnit - onNext");
                inventoryRequestObserver.onNext(unit);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("removeInventoryUnit - onError");
                inventoryRequestObserver.onError(throwable);

            }

            @Override
            public void onCompleted() {
                log.info("removeInventoryUnit - onCompleted");
                inventoryRequestObserver.onCompleted();
            }
        };
    }

    private StreamObserver<CommerceResponse> getInventoryResponseObserver(StreamObserver<CommerceResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(CommerceResponse commerceResponse) {
                log.info("Response observer - onNext");
                responseObserver.onNext(commerceResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Response observer - onError");
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                log.info(" Response observer - onCompleted");
                responseObserver.onCompleted();
            }
        };
    }
}
