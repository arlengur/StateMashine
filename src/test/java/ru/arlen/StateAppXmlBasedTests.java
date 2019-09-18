package ru.arlen;

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

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:appconfig-root.xml")
public class StateAppXmlBasedTests {

    @Autowired
    @Qualifier("stateMachineFactory")
    private StateMachineFactory<States, Events> stateMachineFactory;
    private StateMachine<States, Events> stateMachine;

    @Before
    public void setUp() throws Exception {
        stateMachine = stateMachineFactory.getStateMachine();
    }

    @Test
    public void firstTest() {
        stateMachine.start();
        stateMachine.sendEvent(Events.START_FEATURE);
        assertTrue("Wrong state", stateMachine.getState().getId() == States.IN_PROGRESS);
    }
}