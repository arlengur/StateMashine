package ru.arlen;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import static ru.arlen.statemachine.Events.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class StateAppAnnotationTests {

    @Autowired
    @Qualifier("stateMachineFactory")
    private StateMachineFactory<States, Events> stateMachineFactory;
    private StateMachine<States, Events> stateMachine;

    @Before
    public void setUp() throws Exception {
        stateMachine = stateMachineFactory.getStateMachine();
    }

    @Test
    public void contextLoads() {
        Assertions.assertThat(stateMachine).isNotNull();
    }

    @Test
    public void initialStateTest() {
        // Asserts
        Assertions.assertThat(stateMachine.getInitialState().getId()).isEqualTo(States.BACKLOG);
    }

    @Test
    public void firstStepTest() {
        // Act
        stateMachine.sendEvent(START_FEATURE);
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.IN_PROGRESS);
    }

    @Test
    public void testGreenWay() {
        // Arrange
        // Act
        stateMachine.sendEvent(START_FEATURE);
        stateMachine.sendEvent(Events.DEPLOY);
        stateMachine.sendEvent(FINISH_FEATURE);
        stateMachine.sendEvent(QA_CHECKED_UC);
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.DONE);
    }

    @Test
    public void testWrongWay() {
        // Arrange
        // Act
        stateMachine.sendEvent(START_FEATURE);
        stateMachine.sendEvent(QA_CHECKED_UC);
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.IN_PROGRESS);
    }

    @Test
    public void rockStarTest() {
        // Act
        stateMachine.sendEvent(Events.ROCK_STAR_DOUBLE_TASK);
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.TESTING);
    }

    @Test
    public void testingUnreachableWithoutDeploy() {
        // Arrange & Act
        stateMachine.sendEvent(START_FEATURE);
        stateMachine.sendEvent(FINISH_FEATURE);
        stateMachine.sendEvent(QA_CHECKED_UC); // not accepted!
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.IN_PROGRESS);
    }

    @Test
    public void testDeployFromBacklog() {
        // Arrange
        // Act
        stateMachine.sendEvent(Events.DEPLOY);
        // Asserts
        Assertions.assertThat(stateMachine.getState().getId()).isEqualTo(States.BACKLOG);
        Assertions.assertThat(stateMachine.getExtendedState().getVariables().get("deployed"))
                .isEqualTo(true);
    }
}
