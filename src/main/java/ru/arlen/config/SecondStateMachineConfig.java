package ru.arlen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import java.util.Optional;

import static ru.arlen.statemachine.Events.*;
import static ru.arlen.statemachine.States.BACKLOG;
import static ru.arlen.statemachine.States.DONE;

@Slf4j
@Configuration
@EnableStateMachineFactory(name = "secondStateMachineFactory")
public class SecondStateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration()
                .listener(listener())
                .autoStartup(true);
    }

    private StateMachineListener<States, Events> listener() {

        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void eventNotAccepted(Message<Events> event) {
                log.error("SECOND: Not accepted event: {}", event);
            }

            @Override
            public void transition(Transition<States, Events> transition) {
                log.info("TRANS from: {} to: {}",
                        ofNullableState(transition.getSource()),
                        ofNullableState(transition.getTarget()));
            }

            private Object ofNullableState(State s) {
                return Optional.ofNullable(s)
                        .map(State::getId)
                        .orElse(null);
            }
        };
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(BACKLOG)
                .end(DONE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                // BACKLOG --> DONE : START_FEATURE
                .source(BACKLOG)
                .target(DONE)
                .event(START_FEATURE)
                .guard(failGuard())
                .and()
                // BACKLOG --> DONE : ROCK_STAR_DOUBLE_TASK
                .withExternal()
                .source(BACKLOG)
                .target(DONE)
                .action(failAction())
                .event(ROCK_STAR_DOUBLE_TASK)
                .and()
                // BACKLOG --> TESTING : FINISH_FEATURE
                .withExternal()
                .source(BACKLOG)
                .target(DONE)
                .action(actionWithInternalError())
                .event(FINISH_FEATURE);
    }

    private Action<States, Events> failAction() {
        return context -> {
            throw new RuntimeException("fail in action");
        };
    }

    private Guard<States, Events> failGuard() {
        return context -> {
            throw new RuntimeException("fail in Guard");
        };
    }

    private Action<States, Events> actionWithInternalError() {
        return context -> {
            context.getStateMachine()
                    .setStateMachineError(new RuntimeException("fail in Action"));
        };
    }


}
