package org.example;

import java.util.Set;

public enum TaskState {
    RUNNING {
        @Override
        Set<TaskState> nextStates() {
            return Set.of(SUSPENDED, READY, WAIT);
        }
    } , SUSPENDED {
        @Override
        Set<TaskState> nextStates() {
            return Set.of(READY);
        }
    }, READY {
        @Override
        Set<TaskState> nextStates() {
            return Set.of(RUNNING);
        }
    }, WAIT {
        @Override
        Set<TaskState> nextStates() {
            return Set.of(READY);
        }
    };

    Set<TaskState> nextStates() {
        return Set.of();
    }
}
