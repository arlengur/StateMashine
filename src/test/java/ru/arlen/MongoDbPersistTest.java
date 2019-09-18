package ru.arlen;

import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.test.context.ActiveProfiles;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ru.arlen.statemachine.Events.DEPLOY;
import static ru.arlen.statemachine.Events.START_FEATURE;
import static ru.arlen.statemachine.States.BACKLOG;
import static ru.arlen.statemachine.States.IN_PROGRESS;

@ActiveProfiles("mongo")
public class MongoDbPersistTest extends BaseMongoIT {
    @Autowired
    private StateMachineFactory<States, Events> stateMachineFactory;

    @Autowired
    StateMachinePersister<States, Events, UUID> persister;

    @Before
    public void setUp() throws Exception {
        mongoTemplate.getCollectionNames()
                .forEach(mongoTemplate::dropCollection);
    }

    @Test
    public void firstTest() {
        // Arrange
        // Act
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        // Asserts
        Assertions.assertThat(collectionNames).isEmpty();
    }

    @Test
    public void testPersist() throws Exception {
        // Arrange
        StateMachine<States, Events> firstStateMachine = stateMachineFactory.getStateMachine();
        firstStateMachine.sendEvent(START_FEATURE);
        firstStateMachine.sendEvent(DEPLOY);

        StateMachine<States, Events> secondStateMachine = stateMachineFactory.getStateMachine();

        // Check Precondition
        Assertions.assertThat(firstStateMachine.getState().getId()).isEqualTo(IN_PROGRESS);
        Assertions.assertThat((boolean) firstStateMachine.getExtendedState().getVariables().get("deployed"))
                .isEqualTo(true);
        Assertions.assertThat(secondStateMachine.getState().getId()).isEqualTo(BACKLOG);
        Assertions.assertThat(secondStateMachine.getExtendedState().getVariables().get("deployed")).isNull();

        // Act
        persister.persist(firstStateMachine, firstStateMachine.getUuid());
        persister.persist(secondStateMachine, secondStateMachine.getUuid());
        persister.restore(secondStateMachine, firstStateMachine.getUuid());

        // Asserts
        Assertions.assertThat(secondStateMachine.getState().getId())
                .isEqualTo(IN_PROGRESS);

        Assertions.assertThat((boolean) secondStateMachine.getExtendedState().getVariables().get("deployed"))
                .isEqualTo(true);

        // Mongo specific asserts:
        Assertions.assertThat(mongoTemplate.getCollectionNames())
                .isNotEmpty();
        List<Document> documents = mongoTemplate.findAll(Document.class,
                "MongoDbRepositoryStateMachine");

        Assertions.assertThat(documents).hasSize(2);
        Assertions.assertThat(documents)
                .flatExtracting(Document::values)
                .contains(firstStateMachine.getUuid().toString(),
                        secondStateMachine.getUuid().toString())
                .contains(firstStateMachine.getState().getId().toString(),
                        secondStateMachine.getState().getId().toString());
    }
}
