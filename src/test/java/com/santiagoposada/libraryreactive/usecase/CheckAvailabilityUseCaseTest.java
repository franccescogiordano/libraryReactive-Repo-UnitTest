package com.santiagoposada.libraryreactive.usecase;

import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.mapper.ResourceMapper;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class CheckAvailabilityUseCaseTest {

    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private CheckAvailabilityUseCase checkAvailabilityUseCase;

    private Resource resource;
    private Resource resourceDont;
    private ResourceDTO resourceDTO;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resource = new Resource();

        resource.setId("1233435ff");
        resource.setName("ResourceName");
        resource.setType("Tipo #1");
        resource.setCategory("Area tematica #1");
        resource.setUnitsAvailable(2);
        resource.setUnitsOwed(5);
        resource.setLastBorrow(LocalDate.parse("2020-01-10"));

        resourceDont = new Resource();

        resourceDont.setId("1233435ff");
        resourceDont.setName("ResourceName");
        resourceDont.setType("Tipo #1");
        resourceDont.setCategory("Area tematica #1");
        resourceDont.setUnitsAvailable(0);
        resourceDont.setUnitsOwed(5);
        resourceDont.setLastBorrow(LocalDate.parse("2020-01-10"));

        resourceDTO = new ResourceDTO();
        resourceDTO.setId(resource.getId());
        resourceDTO.setName(resource.getName());
        resourceDTO.setType(resource.getType());
        resourceDTO.setCategory(resource.getCategory());
        resourceDTO.setUnitsAvailable(resource.getUnitsAvailable());
        resourceDTO.setUnitsOwed(resource.getUnitsOwed());
        resourceDTO.setLastBorrow(resource.getLastBorrow());
    }

    @Test
    void siHay() {
        Mockito.when(resourceRepository.findById(resource.getId())).thenReturn(Mono.just(resource));
        StepVerifier.create(checkAvailabilityUseCase.apply(resource.getId())).expectNext(resource.getName() + "is available").verifyComplete();
    }
    @Test
    void noHay() {
        Mockito.when(resourceRepository.findById(resourceDont.getId())).thenReturn(Mono.just(resourceDont));
        StepVerifier.create(checkAvailabilityUseCase.apply(resourceDTO.getId())).expectNext(resource.getName() + "is not available, last borrow "+ resource.getLastBorrow()).verifyComplete();
    }
}