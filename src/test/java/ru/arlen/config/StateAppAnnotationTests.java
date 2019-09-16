package ru.arlen.config;

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

import static org.assertj.core.api.Assertions.assertThat;
import static ru.arlen.statemachine.Events.*;
import static ru.arlen.statemachine.States.*;

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
    public void testInit() {
        assertThat(stateMachine).isNotNull();
        assertThat(stateMachine.getState().getId()).isEqualTo(BACKLOG);
    }

    @Test
    public void testGreenWay() {
                stateMachine.sendEvent(START_FEATURE);
                stateMachine.sendEvent(FINISH_FEATURE);
                stateMachine.sendEvent(QA_TEAM_APPROVE);
        assertThat(stateMachine.getState().getId()).isEqualTo(DONE);
    }

    @Test
    public void testWrongWay() {
        stateMachine.sendEvent(START_FEATURE);
        stateMachine.sendEvent(QA_TEAM_APPROVE);
        assertThat(stateMachine.getState().getId()).isEqualTo(IN_PROGRESS);
    }

    @Test
    public void wire() {
        assertThat(stateMachine.getState().getId()).isEqualTo(BACKLOG);
    }

    @Test
    public void testRockStar() {
        stateMachine.sendEvent(ROCK_STAR);
        assertThat(stateMachine.getState().getId()).isEqualTo(TESTING);
    }
}