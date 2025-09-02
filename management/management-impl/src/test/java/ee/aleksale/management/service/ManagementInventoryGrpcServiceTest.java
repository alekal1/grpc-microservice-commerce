package ee.aleksale.management.service;

import ee.aleksale.common.proto.v1.CommerceResponse;
import ee.aleksale.common.proto.v1.InventoryUnit;
import ee.aleksale.inventory.proto.v1.InventoryServiceGrpc;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManagementInventoryGrpcServiceTest {

    private InventoryServiceGrpc.InventoryServiceStub inventoryServiceStub;
    private ManagementInventoryGrpcService managementInventoryGrpcService;

    @BeforeEach
    void init() {
        inventoryServiceStub = mock(InventoryServiceGrpc.InventoryServiceStub.class);

        managementInventoryGrpcService = new ManagementInventoryGrpcService(
                inventoryServiceStub
        );
    }

    private static class RecordingObserver<T> implements StreamObserver<T> {
        volatile boolean completed;
        volatile Throwable error;
        final List<T> values = new ArrayList<>();

        @Override
        public void onNext(T t) { values.add(t); }

        @Override
        public void onError(Throwable throwable) { error = throwable; }

        @Override
        public void onCompleted() { completed = true; }
    }

    @Test
    void addInventoryUnit_forward_and_propagate() {
        var inventoryRequestObserver = new RecordingObserver<InventoryUnit>();
        var inventoryResponseObserver = new AtomicReference<StreamObserver<CommerceResponse>>();

        doAnswer(inv -> {
            inventoryResponseObserver.set(inv.getArgument(0));
            return inventoryRequestObserver;
        }).when(inventoryServiceStub).addInventoryUnit(any());

        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var requestObserver = managementInventoryGrpcService.addInventoryUnit(responseObserver);

        var iu1 = InventoryUnit.getDefaultInstance();
        var iu2 = InventoryUnit.getDefaultInstance();

        requestObserver.onNext(iu1);
        requestObserver.onNext(iu2);
        requestObserver.onCompleted();

        assertEquals(2, inventoryRequestObserver.values.size());
        assertEquals(iu1, inventoryRequestObserver.values.get(0));
        assertEquals(iu2, inventoryRequestObserver.values.get(1));

        assertTrue(inventoryRequestObserver.completed);
        assertNull(inventoryRequestObserver.error);

        var response1 = CommerceResponse.getDefaultInstance();
        var response2 = CommerceResponse.getDefaultInstance();

        var resps = inventoryResponseObserver.get();
        assertNotNull(resps);
        resps.onNext(response1);
        resps.onNext(response2);
        resps.onCompleted();

        assertNull(responseObserver.getError());
        var results = responseObserver.getValues();

        assertEquals(2, results.size());
        assertEquals(response1, results.get(0));
        assertEquals(response2, results.get(1));
    }

    @Test
    void removeInventoryUnit_forward_and_propagate() {
        var inventoryRequestObserver = new RecordingObserver<InventoryUnit>();
        var inventoryResponseObserver = new AtomicReference<StreamObserver<CommerceResponse>>();

        doAnswer(inv -> {
            inventoryResponseObserver.set(inv.getArgument(0));
            return inventoryRequestObserver;
        }).when(inventoryServiceStub).removeInventoryUnit(any());

        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var requestObserver = managementInventoryGrpcService.removeInventoryUnit(responseObserver);

        var iu1 = InventoryUnit.getDefaultInstance();
        var iu2 = InventoryUnit.getDefaultInstance();

        requestObserver.onNext(iu1);
        requestObserver.onNext(iu2);
        requestObserver.onCompleted();

        assertEquals(2, inventoryRequestObserver.values.size());
        assertEquals(iu1, inventoryRequestObserver.values.get(0));
        assertEquals(iu2, inventoryRequestObserver.values.get(1));

        assertTrue(inventoryRequestObserver.completed);
        assertNull(inventoryRequestObserver.error);

        var response1 = CommerceResponse.getDefaultInstance();
        var response2 = CommerceResponse.getDefaultInstance();

        var resps = inventoryResponseObserver.get();
        assertNotNull(resps);
        resps.onNext(response1);
        resps.onNext(response2);
        resps.onCompleted();

        assertNull(responseObserver.getError());
        var results = responseObserver.getValues();

        assertEquals(2, results.size());
        assertEquals(response1, results.get(0));
        assertEquals(response2, results.get(1));
    }

    @Test
    void propagatesErrors() {
        var inventoryRequestObserver = spy(new RecordingObserver<InventoryUnit>());
        var inventoryResponseObserver = new AtomicReference<StreamObserver<CommerceResponse>>();

        doAnswer(inv -> {
            inventoryResponseObserver.set(inv.getArgument(0));
            return inventoryRequestObserver;
        }).when(inventoryServiceStub).addInventoryUnit(any());

        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var requestObserver = managementInventoryGrpcService.addInventoryUnit(responseObserver);

        var callerError = new RuntimeException("error from client");
        requestObserver.onError(callerError);

        verify(inventoryRequestObserver).onError(callerError);

        var inventoryError = new IllegalArgumentException("inventory error");
        var resp = inventoryResponseObserver.get();
        assertNotNull(resp);
        resp.onError(inventoryError);

        assertNotNull(responseObserver.getError());
        assertEquals(inventoryError.getMessage(), responseObserver.getError().getMessage());
    }
}
