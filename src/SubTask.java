public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
        epic.addSubTask(this);
    }

    public SubTask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
        epic.addSubTask(this);
    }

    @Override
    public String toString() {
        return "SubTask{" +
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
}
