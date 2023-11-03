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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.when;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class ReturnUseCaseTest {
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private UpdateUseCase updateUseCase;

    @InjectMocks
    ReturnUseCase returnUseCase;
    private Resource resource;
    private Resource resourceReturned;
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


        resourceReturned = new Resource();

        resourceReturned.setId("1233435ff");
        resourceReturned.setName("ResourceName");
        resourceReturned.setType("Tipo #1");
        resourceReturned.setCategory("Area tematica #1");
        resourceReturned.setUnitsAvailable(3);
        resourceReturned.setUnitsOwed(5);
        resourceReturned.setLastBorrow(LocalDate.parse("2020-01-10"));
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
    void apply() {
        Mockito.when(resourceMapper.fromResourceEntityToDTO()).thenReturn(r->resourceDTO);
        Mockito.when(resourceRepository.findById(resource.getId())).thenReturn(Mono.just(resource));
        Mockito.when(updateUseCase.apply(resourceDTO)).thenReturn(Mono.just(resourceDTO));

        // Act and Assert
        StepVerifier.create(returnUseCase.apply(resource.getId()))
                .expectNext("The resource with id: " + resource.getId() + "was returned successfully")
                .verifyComplete();

        // Verify the repository interactions
        verify(resourceRepository, times(1)).findById(resource.getId());
        verify(updateUseCase, times(1)).apply(resourceDTO);
    }
}