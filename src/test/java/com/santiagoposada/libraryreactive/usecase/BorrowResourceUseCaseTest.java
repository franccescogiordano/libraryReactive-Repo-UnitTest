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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalDate;
import static org.mockito.Mockito.when;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class BorrowResourceUseCaseTest {
    @Mock
    private ResourceMapper resourceMapper;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private UpdateUseCase updateUseCase;
    @InjectMocks
    private BorrowResourceUseCase borrowResourceUseCase;
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
    void testBorrowResourceWithAvailableUnits() {

        String resourceId = "1233435ff";


        when(resourceRepository.findById(resourceId)).thenReturn(Mono.just(resource));
        when(resourceMapper.fromResourceEntityToDTO()).thenReturn(r->resourceDTO);
        when(updateUseCase.apply(resourceDTO)).thenReturn(Mono.just(resourceDTO));


        Mono<String> result = borrowResourceUseCase.apply(resourceId);
        resourceDTO.setUnitsAvailable(1);

        StepVerifier.create(result)
                .expectNext("The resource "
                        + resourceDTO.getName() + " has been borrowed, there are "
                        + resourceDTO.getUnitsAvailable() + " units available")
                .verifyComplete();
    }

    @Test
    void testBorrowResourceWithNoAvailableUnits() {
        // Arrange
        String resourceId = "1233435ff";

        resource.setUnitsAvailable(0);
        when(resourceRepository.findById(resourceId)).thenReturn(Mono.just(resource));

        // Act
        Mono<String> result = borrowResourceUseCase.apply(resourceId);

        // Assert
        StepVerifier.create(result)
                .expectNext("There arent units left to be borrow of that resource")
                .verifyComplete();
    }
    @Test
    void testNullId() {

       // when(resourceRepository.findById(resourceId)).thenReturn(Mono.just(resource));

        StepVerifier.create(borrowResourceUseCase.apply(null))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Id cannot be null"))
                .verify();

    }
}
