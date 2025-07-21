public class SubTask extends Task{
    Epic epic;

    public SubTask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }


}
