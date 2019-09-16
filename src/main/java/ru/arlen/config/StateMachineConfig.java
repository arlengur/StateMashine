package ru.arlen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import java.util.Optional;

import static ru.arlen.statemachine.Events.*;
import static ru.arlen.statemachine.States.*;

@Configuration
@EnableStateMachineFactory
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                .source(BACKLOG)
                .target(IN_PROGRESS)
                .event(START_FEATURE)
                .and()
                .withExternal()
                .source(IN_PROGRESS)
                .target(TESTING)
                .event(FINISH_FEATURE)
                .action(deployAction())
                .and()
                .withExternal()
                .source(TESTING)
                .target(DONE)
                .event(QA_TEAM_APPROVE)
                .and()
                .withExternal()
                .source(TESTING)
                .target(IN_PROGRESS)
                .event(QA_TEAM_REJECT)
                .and()
                .withExternal()
                .source(BACKLOG)
                .target(TESTING)
                .event(ROCK_STAR);
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(BACKLOG)
                .state(IN_PROGRESS, timeToWork(), timeToSleep())
                .state(TESTING)
                .state(DONE);
    }

    private Action<States, Events> timeToSleep() {
        return context -> log.warn("Time to sleep...");
    }

    private Action<States, Events> timeToWork() {
        return context -> log.warn("Time to work...");
    }

    private Action<States, Events> deployAction() {
        return context -> log.warn("Deploing...");
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration()
                .listener(listener())
                .autoStartup(true);
    }

    private StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void transition(Transition transition) {
                log.info("MOVE from {} to {}",
                        ofNullableState(transition.getSource()),
                        ofNullableState(transition.getTarget()));
            }

            private Object ofNullableState(State s) {
                return Optional.ofNullable(s).map(State::getId).orElse(null);
            }

            @Override
            public void eventNotAccepted(Message<Events> event) {
                log.error("not accepted: {}", event);
            }
        };
    }
}
