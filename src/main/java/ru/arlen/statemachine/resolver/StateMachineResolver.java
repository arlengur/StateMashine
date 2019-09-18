package ru.arlen.statemachine.resolver;

import org.springframework.statemachine.StateMachine;

import java.util.List;

public interface StateMachineResolver<S, E> {
    /**
     * Evaluate available events from current states of state-machine
     *
     * @param stateMachine state machine
     *
     * @return Events collection
     */
    List<E> getAvailableEvents(StateMachine<S, E> stateMachine);
}
