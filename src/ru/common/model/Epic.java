package ru.common.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Integer> subtaskIDs;
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, Duration.ZERO);
        subtaskIDs = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW, null, Duration.ZERO);
        subtaskIDs = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void recalculateTimeData(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
            return;
        }

        LocalDateTime earliestStart = subtasks.get(0).getStartTime();
        LocalDateTime latestEnd = subtasks.get(0).getEndTime();
        long totalMinutes = 0;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
                if (subtask.getStartTime().isBefore(earliestStart)) {
                earliestStart = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(latestEnd)) {
                latestEnd = subtask.getEndTime();
            }
            totalMinutes += subtask.getDuration().toMinutes();
        }
    }
        this.startTime = earliestStart;
        this.endTime = latestEnd;
        this.duration = Duration.ofMinutes(totalMinutes);
    }

    @Override
    public String toString() {
        String start = "null";
        if (getStartTime() != null) {
            start = getStartTime().toString();
        }

        String durationString = "0";
        if (getDuration() != null) {
            durationString = String.valueOf(getDuration().toMinutes());
        }
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription() + "," + start + "," + durationString + ",";
    }

    public void setSubtaskIDs(List<Integer> subtaskIDs) {
        this.subtaskIDs = new ArrayList<>(subtaskIDs);
    }

    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

}
