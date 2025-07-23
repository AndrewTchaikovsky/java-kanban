package ru.common.model;

public class SubTask extends Task {
    private Epic epic;
    int epicID;

    public SubTask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
        epicID = epic.getId();
    }

    public SubTask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "ru.common.model.SubTask{" +
                "epic=" + epic.getName() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public Epic getEpic() {
        return epic;
    }

    public int getEpicID() {
        return epicID;
    }
}
