package ru.arlen.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.data.mongodb.*;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import ru.arlen.persist.InMemoryPersist;
import ru.arlen.persist.StubPersist;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import java.util.UUID;

@Configuration
public class PersistConfig {
    @Profile({"in-memory", "default"})
    @Bean
    public StateMachinePersist<States, Events, UUID> inMemory() {
        return new InMemoryPersist();
    }

    @Profile("stub")
    @Bean
    public StateMachinePersist<States, Events, UUID> stubPersist() {
        return new StubPersist();
    }

    @Profile("mongo")
    @Bean
    public StateMachineRuntimePersister<States, Events, UUID> mongoPersist(MongoDbStateMachineRepository mongoRepository) {
        return new MongoDbPersistingStateMachineInterceptor<>(mongoRepository);
    }

    @Bean
    public StateMachinePersister<States, Events, UUID> persister(StateMachinePersist<States, Events, UUID> defaultPersist) {
        return new DefaultStateMachinePersister<>(defaultPersist);
    }
}
