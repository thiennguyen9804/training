package com.example.shipping_service.service;

import com.example.shipping_service.entity.Shipping;
import com.example.shipping_service.event.ShipmentEvent;
import com.example.shipping_service.mapper.ShipmentEventToShippingMapper;
import com.example.shipping_service.repository.ShippingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShipmentEventToShippingMapper mapper;

    @Mock
    private ShippingRepository shippingRepository;

    @InjectMocks
    private ShippingService shippingService;

    @Test
    void shouldMapAndSaveShippingSuccessfully() {
        ShipmentEvent sampleEvent = new ShipmentEvent(1L, 1L, "");
        Shipping mockEntity = new Shipping();
        when(mapper.eventToShipping(sampleEvent)).thenReturn(mockEntity);
        shippingService.saveShipping(sampleEvent);
        verify(mapper, times(1)).eventToShipping(sampleEvent);
        verify(shippingRepository, times(1)).save(mockEntity);
    }
}
