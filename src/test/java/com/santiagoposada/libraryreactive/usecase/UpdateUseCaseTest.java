package com.santiagoposada.libraryreactive.usecase;

import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.mapper.ResourceMapper;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
@DataMongoTest
@ExtendWith(MockitoExtension.class)
class UpdateUseCaseTest {
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    UpdateUseCase updateUseCase;

    private Resource resource;

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
        Mockito.when(resourceRepository.save(resource)).thenReturn(Mono.just(resource));
        Mockito.when(resourceMapper.fromResourceEntityToDTO()).thenReturn(resourceDTO1 -> resourceDTO);
        Mockito.when(resourceMapper.fromResourceDTOtoEntity()).thenReturn(resourceDTO1 -> resource);

        // Act and Assert
        StepVerifier.create(updateUseCase.apply(resourceDTO))
                .expectNext(resourceDTO)
                .verifyComplete();

        // Verify the repository interactions
        verify(resourceRepository, times(1)).save(resource);
        verify(resourceMapper, times(1)).fromResourceEntityToDTO();
    }
}