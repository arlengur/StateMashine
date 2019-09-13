package ru.arlen.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:appconfig-root.xml")
public class StateAppXmlBasedTests {

    @Autowired
    @Qualifier("stateMachine")
    private StateMachine<States, Events> stateMachine;

    @Test
    public void firstTest() {
        stateMachine.start();
        stateMachine.sendEvent(Events.START_FEATURE);
        assertTrue("Wrong state", stateMachine.getState().getId() == States.IN_PROGRESS);
    }
}