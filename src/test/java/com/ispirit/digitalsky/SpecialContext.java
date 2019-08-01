package com.ispirit.digitalsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.repository.DroneDeviceRepository;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.DroneDeviceServiceImpl;
import com.ispirit.digitalsky.service.DroneTypeServiceImpl;
import com.ispirit.digitalsky.service.api.DigitalSignatureVerifierService;
import com.ispirit.digitalsky.service.api.DroneDeviceService;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.util.CustomValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Validator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class SpecialContext {
  public static final String OBJECT_MAPPER = "object.mapper";

  @Bean
  CustomValidator customValidator(Validator validator) {
    return new CustomValidator(validator);
  }

  @Bean(OBJECT_MAPPER)
  public ObjectMapper objectMapper() {
    return new ObjectMapperBuilder().build();
  }

  @Bean
  DroneDeviceService droneDeviceService(DroneDeviceRepository droneRepository, DigitalSignatureVerifierService signatureVerifierService,
                                        OrganizationOperatorRepository organizationOperatorRepository,
                                        OperatorDroneService operatorDroneService,
                                        ManufacturerService manufacturerService,
                                        DroneTypeServiceImpl droneTypeService) {
    IndividualOperatorRepository individualOperatorRepository = mock(IndividualOperatorRepository.class);
    when(individualOperatorRepository.loadByBusinessIdentifier(any(String.class))).thenReturn(any(IndividualOperator.class));
    return new DroneDeviceServiceImpl(droneRepository, signatureVerifierService,individualOperatorRepository, organizationOperatorRepository, operatorDroneService, manufacturerService, droneTypeService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

