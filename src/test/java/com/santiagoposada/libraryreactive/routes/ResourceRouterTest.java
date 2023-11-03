package com.santiagoposada.libraryreactive.routes;

import com.santiagoposada.libraryreactive.config.WebFluxConfig;
import com.santiagoposada.libraryreactive.dto.ResourceDTO;
import com.santiagoposada.libraryreactive.usecase.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
class ResourceRouterTest {


    private ResourceRouter resourceRouter;
    @Autowired
    private ApplicationContext context;
    private WebTestClient webTestClient;
    @Mock
    BorrowResourceUseCase borrowResourceUseCase;
    @Mock
    ReturnUseCase returnUseCase;
    @Mock
    GetByTypeUseCase getByTypeUseCase;
    @Mock
    GetByCategoryUseCase getByCategoryUseCase;
    @Mock
    DeleteResourceUseCase  deleteResourceUseCase;
    @Mock
    private UpdateUseCase updateUseCase;
    @Mock
    private GetResourceByIdUseCase getResourceByIdUseCase;
    @Mock
    private GetAllUseCase getAllUseCase;
    @Mock
    private CreateResourceUseCase createResourceUseCase;
    @Mock
    private CheckAvailabilityUseCase checkAvailabilityUseCase;

    @BeforeEach
    void setUp() {

        resourceRouter = new ResourceRouter();
        webTestClient = WebTestClient
                .bindToApplicationContext(context).configureClient().
                baseUrl("http://localhost:8080")
                .build();

    }

    @Test
    void createResourceRoute() {
    }

    @Test
    public void testGetAllRouter() {
        WebTestClient client = WebTestClient.bindToRouterFunction(resourceRouter.getAllRouter(getAllUseCase)).build();

        // Simulación de la respuesta de GetAllUseCase
        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        resourceDTOList.add(new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2));
        resourceDTOList.add(new ResourceDTO("2", "Resource 2", "Category 2", "Type 2", LocalDate.now(), 3, 1));
        Mockito.when(getAllUseCase.get()).thenReturn(Flux.fromIterable(resourceDTOList));

        client.get()
                .uri("/resources")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResourceDTO.class)
                .hasSize(2)
                .contains(resourceDTOList.get(0), resourceDTOList.get(1));
    }

    // Código posterior y otras pruebas


    @Test
    void getResourceById() {
        WebTestClient client = WebTestClient.bindToRouterFunction(resourceRouter.getResourceById(getResourceByIdUseCase)).build();

        // Simulación de la respuesta de GetAllUseCase

        ResourceDTO resourceDTO = new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2);

        Mockito.when(getResourceByIdUseCase.apply(resourceDTO.getId())).thenReturn(Mono.just(resourceDTO));

        client.get()
                .uri("/resource/{id}", resourceDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResourceDTO.class)
                .contains(resourceDTO);
    }

    @Test
    void updateResourceRoute() {
        WebTestClient client = WebTestClient.bindToRouterFunction(resourceRouter.updateResourceRoute(updateUseCase)).build();

        // Simulación de la respuesta de GetAllUseCase

        ResourceDTO resourceDTO = new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2);

        Mockito.when(updateUseCase.apply(resourceDTO)).thenReturn(Mono.just(resourceDTO));

        client.put()
                .uri("/update", resourceDTO.getId())
                .body(Mono.just(resourceDTO), ResourceDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResourceDTO.class)
                .value(result -> {
                    StepVerifier.create(Mono.just(result))
                            .expectNextMatches(updatedResource ->
                                    updatedResource.getId()
                                            .equals(resourceDTO.getId())
                                            && updatedResource.getName().equals(
                                            resourceDTO.getName())
                                            && updatedResource.getType().equals(
                                            resourceDTO.getType())
                                            && updatedResource.getCategory().equals(
                                            resourceDTO.getCategory())
                                            && updatedResource
                                            .getUnitsAvailable() == resourceDTO
                                            .getUnitsAvailable()
                                            && updatedResource
                                            .getUnitsOwed() == resourceDTO
                                            .getUnitsOwed()
                            )
                            .expectComplete()
                            .verify();
                });

    }

    @Test
    void deleteResourceToute() {

        String resourceId = "12345";
        Mockito.when(deleteResourceUseCase.apply(resourceId)).thenReturn(Mono.empty());
        webTestClient = WebTestClient.bindToRouterFunction(resourceRouter.deleteResourceToute(deleteResourceUseCase)).build();

        webTestClient.delete().uri("/delete/{id}", resourceId).exchange()
                .expectStatus().isAccepted()
                .expectBody(Void.class)
                .value(result -> {
                    // Assert
                    StepVerifier.create(Mono.empty())
                            .expectComplete()
                            .verify();
                });
    }

    @Test
    void checkForAvailabilityRoute() {
        ResourceDTO resourceDTO = new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2);
        Mockito.when(checkAvailabilityUseCase.apply(resourceDTO.getId())).thenReturn(Mono.just(resourceDTO.getName() + " is available"));

        webTestClient = WebTestClient
                .bindToRouterFunction(new ResourceRouter()
                        .checkForAvailabilityRoute(checkAvailabilityUseCase))
                .build();

        // Act
        webTestClient.get()
                .uri("/availability/{id}", resourceDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(result -> {
                    // Assert
                    StepVerifier.create(Mono.just(result))
                            .expectNext(resourceDTO.getName() + " is available")
                            .expectComplete()
                            .verify();
                });
    }

    @Test
    void getByTypeRoute() {
        WebTestClient client = WebTestClient.bindToRouterFunction(resourceRouter.getByTypeRoute(getByTypeUseCase)).build();



        ResourceDTO resourceDTO = new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2);

        Mockito.when(getByTypeUseCase.apply(resourceDTO.getType())).thenReturn(Flux.just(resourceDTO));

        client.get()
                .uri("/getByType/{type}", resourceDTO.getType())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResourceDTO.class)
                .contains(resourceDTO);
    }

    @Test
    void getByCategory() {
        WebTestClient client = WebTestClient.bindToRouterFunction(resourceRouter.getByCategory(getByCategoryUseCase)).build();

        ResourceDTO resourceDTO = new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2);

        Mockito.when(getByCategoryUseCase.apply(resourceDTO.getCategory())).thenReturn(Flux.just(resourceDTO));

        client.get()
                .uri("/getByCategory/{category}", resourceDTO.getCategory())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ResourceDTO.class)
                .contains(resourceDTO);
    }

    @Test
    void borrowResourceRoute() {
        ResourceDTO resourceDTO = new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2);
        String respuesta = "The resource " + resourceDTO.getName() + " has been borrowed, there are "
                + (resourceDTO.getUnitsAvailable() - 1) + " units available";

        Mockito.when(borrowResourceUseCase.apply(resourceDTO.getId())).thenReturn(Mono.just(respuesta));

        webTestClient = WebTestClient
                .bindToRouterFunction(resourceRouter.borrowResourceRoute(borrowResourceUseCase))
                .build();

        webTestClient.put()
                .uri("/borrow/{id}", resourceDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(result -> {
                    StepVerifier.create(Mono.just(result))
                            .expectNext(respuesta)
                            .expectComplete()
                            .verify();
                });
    }

    @Test
    void returnRoute() {
        ResourceDTO resourceDTO = new ResourceDTO("1", "Resource 1", "Category 1", "Type 1", LocalDate.now(), 5, 2);
        String respuesta = "The resource with id: " + resourceDTO.getId() + " was returned successfully";
        Mockito.when(returnUseCase.apply(resourceDTO.getId())).thenReturn(Mono.just(respuesta));

        webTestClient = WebTestClient
                .bindToRouterFunction(new ResourceRouter().returnRoute(returnUseCase))
                .build();
        webTestClient.put()
                .uri("/return/{id}", resourceDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .value(result -> {
                    StepVerifier.create(Mono.just(result))
                            .expectNext(respuesta  )
                            .expectComplete()
                            .verify();
                });
    }
}