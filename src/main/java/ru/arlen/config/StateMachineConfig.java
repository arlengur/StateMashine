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
import static ru.arlen.statemachine.States.*;

@Configuration
@EnableStateMachineFactory
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {
    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration().listener(listener()).autoStartup(true);
    }

    private StateMachineListener<States, Events> listener() {

        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void eventNotAccepted(Message<Events> event) {
                log.error("Not accepted event: {}", event);
            }

            @Override
            public void transition(Transition<States, Events> transition) {
                log.warn("MOVE from: {}, to: {}", ofNullableState(transition.getSource()), ofNullableState(transition.getTarget()));
            }

            private Object ofNullableState(State s) {
                return Optional.ofNullable(s).map(State::getId).orElse(null);
            }
        };
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(States.BACKLOG, developersWakeUpAction())
                .state(States.IN_PROGRESS, weNeedCoffeeAction())
                .state(States.TESTING, qaWakeUpAction())
                .state(States.DONE, goToSleepAction())
                .end(States.DONE);
    }

    private Action<States, Events> developersWakeUpAction() {
        return stateContext -> log.warn("Просыпайтесь лентяи!");
    }

    private Action<States, Events> weNeedCoffeeAction() {
        return stateContext -> log.warn("Без кофе никак!");
    }

    private Action<States, Events> qaWakeUpAction() {
        return stateContext -> log.warn("Будим команду тестирования, солнце высоко!");
    }

    private Action<States, Events> goToSleepAction() {
        return stateContext -> log.warn("Всем спать! клиент доволен.");
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                .source(BACKLOG)
                .target(IN_PROGRESS)
                .event(START_FEATURE)
                .and()
                // DEVELOPERS:
                .withExternal()
                .source(IN_PROGRESS)
                .target(TESTING)
                .event(FINISH_FEATURE)
                .guard(alreadyDeployedGuard())
                .and()
                // QA-TEAM:
                .withExternal()
                .source(TESTING)
                .target(DONE)
                .event(QA_CHECKED_UC)
                .and()
                .withExternal()
                .source(TESTING)
                .target(IN_PROGRESS)
                .event(QA_REJECTED_UC)
                .and()
                // ROCK-STAR:
                .withExternal()
                .source(BACKLOG)
                .target(TESTING)
                .event(ROCK_STAR_DOUBLE_TASK)
                .and()
                // DEVOPS:
                .withInternal()
                .source(IN_PROGRESS)
                .event(DEPLOY)
                .action(deployPreProd())
                .and()
                .withInternal()
                .source(BACKLOG)
                .event(DEPLOY)
                .action(deployPreProd());
    }

    private Guard<States, Events> alreadyDeployedGuard() {
        return context -> Optional.ofNullable(context.getExtendedState().getVariables().get("deployed")).map(v -> (boolean) v).orElse(false);
    }

    private Action<States, Events> deployPreProd() {
        return stateContext -> {
            log.warn("DEPLOY: Выкатываемся на препродакшен.");
            stateContext.getExtendedState().getVariables().put("deployed", true);
        };
    }
}
