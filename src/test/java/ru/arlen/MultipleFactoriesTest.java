package ru.arlen;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.junit4.SpringRunner;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;
import ru.arlen.statemachine.resolver.StateMachineResolver;

import java.util.List;

import static ru.arlen.statemachine.Events.*;
import static ru.arlen.statemachine.States.BACKLOG;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MultipleFactoriesTest {

    @Autowired
    @Qualifier("secondStateMachineFactory")
    private StateMachineFactory<States, Events> secondStateMachineFactory;

    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StateMachineResolver<States, Events> stateMachineResolver;

    @Before
    public void setUp() throws Exception {
        stateMachine = secondStateMachineFactory.getStateMachine();
    }

    /**
     * Try to make a transition with the guard throws an exception
     */
    @Test
    public void failInGuardTest() {
        // Arrange
        // Act
        stateMachine.sendEvent(START_FEATURE);
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(BACKLOG);
    }

    /**
     * Try to make a transition when the action throws an exception
     */
    @Test
    public void failInActionTest() {
        // Arrange
        // Act
        stateMachine.sendEvent(ROCK_STAR_DOUBLE_TASK);
        // Asserts
        Assertions.assertThat(2 + 2).isEqualTo(4);
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(BACKLOG);
    }

    @Test
    public void internalFailTest() {
        // Check precondition
        Assertions.assertThat(stateMachine.hasStateMachineError()).isFalse();
        // Act
        stateMachine.sendEvent(FINISH_FEATURE);
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.DONE);

        Assertions.assertThat(stateMachine.hasStateMachineError()).isTrue();
    }

    /**
     * try to evaluate available events from state with exception in guard
     */
    @Test
    public void testResolver() {
        // Arrange
        // Act
        List<Events> availableEvents = stateMachineResolver.getAvailableEvents(stateMachine);

        // Asserts
        Assertions.assertThat(availableEvents).containsOnly(ROCK_STAR_DOUBLE_TASK, FINISH_FEATURE);
    }

}
