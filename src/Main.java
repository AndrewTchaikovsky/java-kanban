import ru.common.manager.HistoryManager;
import ru.common.manager.Managers;
import ru.common.manager.TaskManager;
import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.Subtask;
import ru.common.model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = manager.getHistoryManager();

        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        int task1ID = manager.createTask(task1);
        int task2ID = manager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        Epic epic2 = new Epic("Эпик 2", "Эпика без подзадач");
        int epic1ID = manager.createEpic(epic1);
        int epic2ID = manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Сабтаск 3", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        int subtask1ID = manager.createSubtask(subtask1);
        int subtask2ID = manager.createSubtask(subtask2);
        int subtask3ID = manager.createSubtask(subtask3);

        manager.getTask(task1ID);
        System.out.println("");
        System.out.println("История после запроса первой задачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getTask(task2ID);
        System.out.println("История после запроса второй задачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getSubtask(subtask1ID);
        System.out.println("История после запроса первой подзадачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getSubtask(subtask2ID);
        System.out.println("История после запроса второй подзадачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getSubtask(subtask3ID);
        System.out.println("История после запроса третьей подзадачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getEpic(epic1ID);
        System.out.println("История после запроса первого эпика: " + historyManager.getHistory() + System.lineSeparator());
        manager.getEpic(epic2ID);
        System.out.println("История после запроса второго эпика: " + historyManager.getHistory() + System.lineSeparator());

        manager.getTask(task1ID);
        System.out.println("История после повторного запроса первой задачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getTask(task2ID);
        System.out.println("История после повторного запроса второй задачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getSubtask(subtask1ID);
        System.out.println("История после повторного запроса первой подзадачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getSubtask(subtask2ID);
        System.out.println("История после повторного запроса второй подзадачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getSubtask(subtask3ID);
        System.out.println("История после повторного запроса третьей подзадачи: " + historyManager.getHistory() + System.lineSeparator());
        manager.getEpic(epic1ID);
        System.out.println("История после повторного запроса первого эпика: " + historyManager.getHistory() + System.lineSeparator());
        manager.getEpic(epic2ID);
        System.out.println("История после повторного запроса второго эпика: " + historyManager.getHistory() + System.lineSeparator());

        manager.deleteTask(task1ID);
        System.out.println("История после удаления первой задачи: " + historyManager.getHistory() + System.lineSeparator());

        manager.deleteEpic(epic1ID);
        System.out.println("История после удаления первого эпика: " + historyManager.getHistory() + System.lineSeparator());


        printAllTasks(manager, historyManager);

    }

    static void printAllTasks(TaskManager manager, HistoryManager historyManager) {
        System.out.println("");
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task Subtask : manager.getSubtasks()) {
            System.out.println(Subtask);
        }

        System.out.println("История:");
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }


}
