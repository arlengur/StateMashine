package ru.arlen.persist;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import java.util.HashMap;
import java.util.UUID;

public class InMemoryPersist implements StateMachinePersist<States, Events, UUID> {
    private HashMap<UUID, StateMachineContext<States, Events>> storage = new HashMap<>();

    @Override
    public void write(StateMachineContext<States, Events> stateMachineContext, UUID uuid) throws Exception {
        storage.put(uuid, stateMachineContext);
    }

    @Override
    public StateMachineContext<States, Events> read(UUID uuid) throws Exception {
        return storage.get(uuid);
    }
}
