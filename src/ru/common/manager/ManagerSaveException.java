package ru.common.manager;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String s, IOException e) {
    }
}
