package ru.arlen;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.arlen.config.StateMachineConfig;

public class StateApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(StateMachineConfig.class);
    }
}
