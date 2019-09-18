package ru.arlen.persist;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import java.util.UUID;

public class StubPersist implements StateMachinePersist<States, Events, UUID> {
    private StateMachineContext<States, Events> context;

    @Override
    public void write(StateMachineContext<States, Events> stateMachineContext, UUID uuid) throws Exception {
        context = context;
    }

    @Override
    public StateMachineContext<States, Events> read(UUID uuid) throws Exception {
        return context;
    }
}