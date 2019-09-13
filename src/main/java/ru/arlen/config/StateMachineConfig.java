package ru.arlen.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import ru.arlen.statemachine.Events;
import ru.arlen.statemachine.States;

import static ru.arlen.statemachine.Events.*;
import static ru.arlen.statemachine.States.*;

@Configuration
@EnableStateMachine

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
                .and()
                .withExternal()
                .source(TESTING)
                .target(DONE)
                .event(QA_TEAM_APPROVE)
                .and()
                .withExternal()
                .source(TESTING)
                .target(IN_PROGRESS)
                .event(QA_TEAM_REJECT);
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates().initial(BACKLOG).state(IN_PROGRESS).state(TESTING).state(DONE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration().autoStartup(true);
    }
}
