package com.example.order_service.mapper_learn;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// THIS CLASS IS USED FOR MAP STRUCT LEARNING ONLY, PLEASE DISREGARD THIS CLASS
@RequiredArgsConstructor
@Component
public class MapperMain implements CommandLineRunner {
  private final Logger logger = LogManager.getLogger(getClass());

  @Override
  public void run(String... args) throws Exception {}

  @Data
  public static class Car {
    private int id;
    private String name;
  }

  public static class BioDieselCar extends Car {}

  public static class ElectricCar extends Car {}

  public enum FuelType {
    ELECTRIC,
    BIO_DIESEL
  }

  @Data
  public static class CarDTO {
    private int id;
    private String name;
    private FuelType fuelType;
  }

  @Mapper
  public abstract static class CarsMapper {
    @BeforeMapping
    protected void enrichDTOWithFuelType(Car car, @MappingTarget CarDTO carDto) {
      if (car instanceof ElectricCar) {
        carDto.setFuelType(FuelType.ELECTRIC);
      }
      if (car instanceof BioDieselCar) {
        carDto.setFuelType(FuelType.BIO_DIESEL);
      }
    }

    @AfterMapping
    protected void convertNameToUpperCase(@MappingTarget CarDTO carDto) {
      carDto.setName(carDto.getName().toUpperCase());
    }

    public abstract CarDTO toCarDTO(Car car);
  }
}
