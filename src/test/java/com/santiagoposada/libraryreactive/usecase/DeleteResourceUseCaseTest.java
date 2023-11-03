package com.santiagoposada.libraryreactive.usecase;

import com.santiagoposada.libraryreactive.entity.Resource;
import com.santiagoposada.libraryreactive.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.when;
@DataMongoTest
@ExtendWith(SpringExtension.class)
class DeleteResourceUseCaseTest {
    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private DeleteResourceUseCase deleteResourceUseCase;
    private String string;
    @BeforeEach
    void setUp() {
        string = "pedro";
    }

    @Test
    void apply() {
        Mockito.when(resourceRepository.deleteById(string)).thenReturn(Mono.empty());


        StepVerifier.create(deleteResourceUseCase.apply(string))
                .verifyComplete();

        verify(resourceRepository, times(1)).deleteById(string);
    }
}