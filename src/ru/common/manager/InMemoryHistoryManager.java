package ru.common.manager;

import ru.common.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        public Task getTask() {
            return task;
        }


    }

    private final Map<Integer, Node> taskHistory;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        this.taskHistory = new HashMap<>();
    }

    public List<Task> getTasks() {
        List<Task> array = new ArrayList<>();
        for (Node node : taskHistory.values()) {
            array.add(node.getTask());
        }
        return array;
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Integer id = node.getTask().getId();
        taskHistory.remove(id);

        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = node.next;
            head.prev = null;
        } else if (node == tail) {
            tail = node.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

    }

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newTail = new Node(oldTail, task, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
    }


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int id = task.getId();
        remove(id);
        linkLast(task);
        taskHistory.put(id, tail);
    }

    @Override
    public void remove(int id) {
        Node node = taskHistory.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void clearHistory() {
        taskHistory.clear();
        head = null;
        tail = null;
    }

}
