import ru.common.manager.TaskManager;
import ru.common.model.Epic;
import ru.common.model.Status;
import ru.common.model.SubTask;
import ru.common.model.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        Task task1 = new Task("Таск 1", "Описание таска 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание таска 2", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);
        System.out.println("");
        System.out.println("TASKS:");
        System.out.println(manager.getAllTasks());
        Task task1Updated = new Task(task1.getId(), "Таск 1", "Обновленный таск 1", Status.IN_PROGRESS);
        manager.updateTask(task1Updated);
        Task task2Updated = new Task(task2.getId(), "Таск 2", "Обновленный таск 2", Status.DONE);
        manager.updateTask(task2Updated);
        System.out.println("");
        System.out.println("UPDATED TASKS:");
        System.out.println(manager.getAllTasks());
        System.out.println("");
        System.out.println("DELETING TASKS:");
        manager.deleteTaskById(task1.getId());
        System.out.println(manager.getAllTasks());
        manager.deleteTasks();
        System.out.println(manager.getAllTasks());
        System.out.println("");

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        System.out.println("EPICS:");
        System.out.println(manager.getAllEpics());
        System.out.println("");

        SubTask subtask1 = new SubTask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        SubTask subtask2 = new SubTask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epic1.getId());
        SubTask subtask3 = new SubTask("Сабтаск 3", "Сабтаск эпика 2", Status.NEW, epic2.getId());
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);
        manager.createSubTask(subtask3);
        System.out.println("SUBTASKS:");
        System.out.println(manager.getAllSubTaskIDs());
        System.out.println(manager.getAllSubTasks());
        System.out.println("");

        SubTask subtask1Updated = new SubTask(subtask1.getId(), "Обновленный сабтаск 1", "Сабтаск эпика 1", Status.DONE, epic1.getId());
        manager.updateSubTask(subtask1Updated);
        SubTask subtask2Updated = new SubTask(subtask2.getId(), "Обновленный сабтаск 2", "Сабтаск эпика 1", Status.DONE, epic1.getId());
        manager.updateSubTask(subtask2Updated);
        SubTask subtask3Updated = new SubTask(subtask3.getId(), "Обновленный сабтаск 3", "Сабтаск эпика 2", Status.IN_PROGRESS, epic2.getId());
        manager.updateSubTask(subtask3Updated);
        System.out.println("UPDATED SUBTASKS:");
        System.out.println(manager.getAllSubTaskIDs());
        System.out.println(manager.getAllSubTasks());
        System.out.println("");
        System.out.println("UPDATED EPICS:");
        System.out.println(manager.getAllEpics());
        System.out.println("");
        System.out.println("DELETING SUBTASKS:");
        manager.deleteSubTaskById(subtask1.getId());
        System.out.println(manager.getAllSubTaskIDs());
        System.out.println(manager.getAllSubTasks());
        manager.deleteAllSubtasks();
        System.out.println(manager.getAllSubTaskIDs());
        System.out.println(manager.getAllSubTasks());
        System.out.println("");
        System.out.println("DELETING EPICS:");
        manager.deleteEpicById(epic1.getId());
        System.out.println(manager.getAllEpics());
        manager.deleteEpics();
        System.out.println(manager.getAllEpics());
        System.out.println("");

        System.out.println("CHANGING EPIC STATUS");
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        manager.createEpic(epic3);
        System.out.println("Создаём эпик " + epic3);
        SubTask subtask4 = new SubTask("Подзадача 4", "Подзадача эпика 3", Status.IN_PROGRESS, epic3.getId());
        manager.createSubTask(subtask4);
        System.out.println("Создаём подзадачу " + subtask4.getName() + " с ID " + subtask4.getId() + " и статусом " + subtask4.getStatus());
        System.out.println("Видим что подзадача добавилась в эпик " + epic3);
        System.out.println("Удаляем подзадачу " + subtask4.getName() + " с ID " + subtask4.getId() + " и статусом " + subtask4.getStatus());
        System.out.println("Все подзадачи: " + manager.getAllSubTasks());
        manager.deleteSubTaskById(subtask4.getId());
        System.out.println("Все подзадачи после удаления: " + manager.getAllSubTasks());
        manager.createSubTask(subtask4);
        System.out.println("Создаем новую подзадачу " + subtask4.getName() + " с ID " + subtask4.getId() + " и статусом " + subtask4.getStatus());
        manager.deleteEpicSubtasks(epic3);
        System.out.println("Удаляем новую подзадачу другим методом и получаем " + manager.getAllSubTasks());
        System.out.println("Видим что подзадачи нет в эпике " + epic3);

    }
}
