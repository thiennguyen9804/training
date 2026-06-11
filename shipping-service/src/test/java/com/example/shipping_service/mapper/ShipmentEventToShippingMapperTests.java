package com.example.shipping_service.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.example.shipping_service.client.CustomerClientService;
import com.example.shipping_service.entity.Shipping;
import com.example.shipping_service.event.ShipmentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShipmentEventToShippingMapperTests {
  @Mock private CustomerClientService customerClientService;

  @InjectMocks private ShipmentEventToShippingMapperImpl mapper;

  private ShipmentEvent event;
  private final String fakeAddress = "Floor 4, KMS Technology, Ho Chi Minh City, Vietnam";

  @BeforeEach
  void setUp() {
    event = new ShipmentEvent(1L, 1L, "CREATED");
    lenient().when(customerClientService.getCustomerAddress(anyLong())).thenReturn(fakeAddress);
  }

  @Test
  void eventToShipping_shouldMapOrderIdAndStatus() {
    Shipping result = mapper.eventToShipping(event);
    assertThat(result.getOrderId()).isEqualTo(1);
    assertThat(result.getShippingStatus()).isEqualTo("CREATED");
  }

  @Test
  void eventToShipping_shouldEnrichAddressFromService() {
    Shipping result = mapper.eventToShipping(event);
    assertThat(result.getAddress()).isEqualTo(fakeAddress);
  }

  @Test
  void eventToShipping_shouldGenerateTrackingNumber() {
    Shipping result = mapper.eventToShipping(event);
    assertThat(result.getTrackingNumber()).isNotNull().isNotBlank();
  }

  @Test
  void eventToShipping_shouldCallCustomerServiceWithCorrectId() {
    mapper.eventToShipping(event);
    verify(customerClientService).getCustomerAddress(1L);
  }

  @Test
  void eventToShipping_shouldReturnNull_whenEventIsNull() {
    Shipping result = mapper.eventToShipping(null);
    assertThat(result).isNull();
  }
}
