package ru.arlen;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.arlen.statemachine.Events.*;
import static ru.arlen.statemachine.States.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles("in-memory")
public class PersistTest {
    @Autowired
    @Qualifier("stateMachineFactory")
    private StateMachineFactory<States, Events> stateMachineFactory;

    @Autowired
    StateMachinePersister<States, Events, UUID> persister;

    @Test
    public void testPersist() throws Exception {
        //Arrange
        StateMachine<States, Events> firstMachine = stateMachineFactory.getStateMachine();
        StateMachine<States, Events> secondMachine = stateMachineFactory.getStateMachine();

        firstMachine.sendEvent(START_FEATURE);
        firstMachine.sendEvent(DEPLOY);

        //Act
        persister.persist(firstMachine, firstMachine.getUuid());
        persister.persist(secondMachine, secondMachine.getUuid());
        persister.restore(secondMachine, firstMachine.getUuid());

        //Asserts
        assertThat(secondMachine.getState().getId()).isEqualTo(IN_PROGRESS);
        Boolean deployed = (Boolean) secondMachine.getExtendedState().getVariables().get("deployed");
        assertThat(deployed).isTrue();
    }
}
