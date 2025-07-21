public class Main {

    public static void main(String[] args) {

        Task task1 = new Task("Переезд", "Переехать в другой дом", Status.NEW);
        Task task2 = new Task("Перелет", "Перелететь в другую страну", Status.NEW);
        System.out.println(task1);
        System.out.println(task2);
        // update task status

        SubTask subtask1 = new SubTask("Переезд", "Переехать в другой дом", Status.NEW);
        SubTask subtask2 = new SubTask("Перелет", "Перелететь в другую страну", Status.NEW);
        System.out.println(subtask1);
        System.out.println(subtask2);
        // update subtask status

        Epic epic1 = new Epic(subtask1);
        Epic epic2 = new Epic(subtask1, subtask2);
        System.out.println(epic1);
        System.out.println(epic2);
        // update epic status

    }
}
