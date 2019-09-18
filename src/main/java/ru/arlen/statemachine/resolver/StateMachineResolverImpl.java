package ru.arlen.statemachine.resolver;

import org.jetbrains.annotations.NotNull;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.trigger.Trigger;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class StateMachineResolverImpl<S, E> implements StateMachineResolver<S, E> {
    @Override
    public List<E> getAvailableEvents(StateMachine<S, E> stateMachine) {
        return stateMachine.getTransitions()
                .stream()
                .filter(t -> isTransitionSourceFromCurrentState(t, stateMachine))
                .filter(t -> evaluateGuardCondition(stateMachine, t))
                .map(Transition::getTrigger)
                .map(Trigger::getEvent)
                .collect(toList());
    }

    private boolean isTransitionSourceFromCurrentState(Transition<S, E> transition, StateMachine<S, E> stateMachine) {

        return stateMachine.getState().getId() == transition.getSource().getId();
    }

    private boolean evaluateGuardCondition(StateMachine<S, E> stateMachine, Transition<S, E> transition) {
        if (transition.getGuard() == null) {
            return true;
        }
        StateContext<S, E> context = makeStateContext(stateMachine, transition);
        try {
            return transition.getGuard().evaluate(context);
        } catch (Exception e) {
            return false;
        }
    }

    @NotNull
    private DefaultStateContext<S, E> makeStateContext(StateMachine<S, E> stateMachine, Transition<S, E> transition) {
        return new DefaultStateContext<>(StateContext.Stage.TRANSITION, null, null, stateMachine.getExtendedState(), transition, stateMachine,
                stateMachine.getState(), transition.getTarget(), null);
    }
}
