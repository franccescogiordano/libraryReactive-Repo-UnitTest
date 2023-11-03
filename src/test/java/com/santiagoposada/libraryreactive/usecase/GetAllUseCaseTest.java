package com.santiagoposada.libraryreactive.usecase;

import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.mapper.ResourceMapper;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class GetAllUseCaseTest {
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private GetAllUseCase getAllUseCase;
    private Resource resource;
    private ResourceDTO resourceDTO;

    @BeforeEach
    void antesde() {

        resource = new Resource();

        resource.setId("1233435ff");
        resource.setName("Nombre #1");
        resource.setType("Tipo #1");
        resource.setCategory("Area tematica #1");
        resource.setUnitsAvailable(10);
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

    @DisplayName("get all resource")
    @Test
    void getAllResourse() {

        when(resourceRepository.findAll()).thenReturn(Flux.just(resource));
        when(resourceMapper.fromResourceEntityToDTO()).thenReturn(r->resourceDTO);


        Publisher<ResourceDTO> setup = getAllUseCase.get();
        StepVerifier
                .create(setup)
                .expectNext(resourceDTO)
                .verifyComplete();

        verify(resourceRepository, times(1)).findAll();
    }

}