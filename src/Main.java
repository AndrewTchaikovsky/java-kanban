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

        SubTask subtask1 = new SubTask("Сабтаск 1", "Сабтаск эпика 1", Status.NEW, epic1);
        SubTask subtask2 = new SubTask("Сабтаск 2", "Сабтаск эпика 1", Status.NEW, epic1);
        SubTask subtask3 = new SubTask("Сабтаск 3", "Сабтаск эпика 2", Status.NEW, epic2);
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);
        manager.createSubTask(subtask3);
        System.out.println("SUBTASKS:");
        System.out.println(manager.getAllSubTasks());
        System.out.println("");

        SubTask subtask1Updated = new SubTask(subtask1.getId(), "Обновленный сабтаск 1", "Сабтаск эпика 1", Status.DONE, epic1);
        manager.updateSubTask(subtask1Updated);
        SubTask subtask2Updated = new SubTask(subtask2.getId(), "Обновленный сабтаск 2", "Сабтаск эпика 1", Status.DONE, epic1);
        manager.updateSubTask(subtask2Updated);
        SubTask subtask3Updated = new SubTask(subtask3.getId(), "Обновленный сабтаск 3", "Сабтаск эпика 2", Status.IN_PROGRESS, epic2);
        manager.updateSubTask(subtask3Updated);
        System.out.println("UPDATED SUBTASKS:");
        System.out.println(manager.getAllSubTasks());
        System.out.println("");
        System.out.println("UPDATED EPICS:");
        System.out.println(manager.getAllEpics());
        System.out.println("");
        System.out.println("DELETING SUBTASKS:");
        manager.deleteSubTaskById(subtask1.getId());
        System.out.println(manager.getAllSubTasks());
        manager.deleteSubTasks();
        System.out.println(manager.getAllSubTasks());
        System.out.println("");
        System.out.println("DELETING EPICS:");
        manager.deleteEpicById(epic1.getId());
        System.out.println(manager.getAllEpics());
        manager.deleteEpics();
        System.out.println(manager.getAllEpics());


    }
}
