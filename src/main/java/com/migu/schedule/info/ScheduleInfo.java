package com.migu.schedule.info;

public class ScheduleInfo extends TaskInfo{
    private int consumption;

    public int getConsumption() {
        return consumption;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    @Override
    public String toString()
    {
        return "TaskInfo [taskId=" + getTaskId() + ", consumption=" + consumption + "]";
    }
}
