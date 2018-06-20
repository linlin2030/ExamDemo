package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.ScheduleInfo;
import com.migu.schedule.info.TaskInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
*类名和方法不能修改
 */
public class Schedule {

    List<Integer> nodeQueue;
    List<TaskInfo> taskQueue;
    List<ScheduleInfo> waitScheduleInfos;

    public Schedule(){
        nodeQueue = new ArrayList<Integer>();
        taskQueue = new ArrayList<TaskInfo>();
        waitScheduleInfos = new ArrayList<ScheduleInfo>();
    }

    public int init() {
        if(nodeQueue != null){
            nodeQueue.clear();
        }

        if(taskQueue != null){
            taskQueue.clear();
        }

        if(waitScheduleInfos != null){
            waitScheduleInfos.clear();
        }


        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {
        // 如果服务节点编号小于等于0, 返回E004:服务节点编号非法
        if(nodeId <= 0){
            return ReturnCodeKeys.E004;
        }

        // 如果服务节点编号已注册, 返回E005:服务节点已注册
        if(nodeQueue.contains(nodeId)){
            return ReturnCodeKeys.E005;
        }

        nodeQueue.add(nodeId);
        return ReturnCodeKeys.E003;
    }

    public int unregisterNode(int nodeId) {
        // 如果服务节点编号小于等于0, 返回E004:服务节点编号非法
        if(nodeId <= 0){
            return ReturnCodeKeys.E004;
        }

        // 轮询服务节点
        int index = -1;
        int size = nodeQueue.size();
        for(int i = 0; i < size; i++){
            if(nodeQueue.get(i) == nodeId){
                index = i;
                break;
            }
        }

        // 如果服务节点编号未被注册, 返回E007:服务节点不存在
        if(index < 0){
            return ReturnCodeKeys.E007;
        }

        nodeQueue.remove(index);
        return ReturnCodeKeys.E006;
    }

    public int isExistTask(List<ScheduleInfo> scheduleInfos,int taskId){
        int index = -1;
        int size = scheduleInfos.size();
        if(size == 0){
            return index;
        }

        for(int i = 0; i < size; i++){
            ScheduleInfo item = scheduleInfos.get(i);
            if(item.getTaskId() == taskId){
                index = i;
                break;
            }
        }
        return index;
    }

    public int addTask(int taskId, int consumption) {
        // 如果任务编号小于等于0, 返回E009:任务编号非法
       if(taskId <= 0){
           return ReturnCodeKeys.E009;
       }

       // 如果相同任务编号任务已经被添加, 返回E010:任务已添加
       if(isExistTask(waitScheduleInfos,taskId) >= 0){
           return ReturnCodeKeys.E010;
       }

        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setTaskId(taskId);
        scheduleInfo.setConsumption(consumption);
        waitScheduleInfos.add(scheduleInfo);
        return ReturnCodeKeys.E008;
    }


    public int deleteTask(int taskId) {
        // 如果任务编号小于等于0, 返回E009:任务编号非法
        if(taskId <= 0){
            return ReturnCodeKeys.E009;
        }

        int index = isExistTask(waitScheduleInfos,taskId);
        if(index >= 0){
            waitScheduleInfos.remove(index);
            return ReturnCodeKeys.E011;
        }

        // 如果指定编号的任务未被添加, 返回E012:任务不存在
        return ReturnCodeKeys.E012;
    }


    public int scheduleTask(int threshold) {
        if(threshold <= 0){
            return ReturnCodeKeys.E002;
        }

        int nodeSize = nodeQueue.size();
        for(int i = 0; i < nodeSize; i++){
           int nodeId = nodeQueue.get(i);
            int index = i;
            for (int j = i + 1; j <nodeSize; j++){
                int nodeId2 = nodeQueue.get(j);
                if(nodeId > nodeId2){
                    index = j;
                }
            }

            if(index != i){
                int temp = nodeId;
                nodeId = nodeQueue.get(index);
                nodeQueue.remove(i);
                nodeQueue.add(i,nodeId);

                nodeQueue.remove(index);
                nodeQueue.add(index,temp);
            }
        }

        int waitSize = waitScheduleInfos.size();
        for(int i = 0; i < waitSize; i++){
            ScheduleInfo item = waitScheduleInfos.get(i);
            int taskId = item.getTaskId();
            int index = i;
            for (int j = i + 1; j <waitSize; j++){
                ScheduleInfo item2 = waitScheduleInfos.get(j);
                if(item2.getTaskId() <= taskId){
                    taskId = item2.getTaskId();
                    index = j;
                }
            }

            if(index != i){
                ScheduleInfo temp = item;
                item = waitScheduleInfos.get(index);
                waitScheduleInfos.remove(i);
                waitScheduleInfos.add(i,item);

                waitScheduleInfos.remove(index);
                waitScheduleInfos.add(index,temp);
            }
        }

        int val = (int)Math.ceil((double)waitSize / nodeSize);
        for(int i = 0; i < nodeSize; i ++){
            int nodeID = nodeQueue.get(i);
            for(int j = i*val; (j < (i + 1)* val) && (j < waitSize); j++){
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setNodeId(nodeID);
                taskInfo.setTaskId(waitScheduleInfos.get(j).getTaskId());
                taskQueue.add(taskInfo);
            }
        }

        if(taskQueue.size() == waitScheduleInfos.size()){
            return ReturnCodeKeys.E013;
        }

        return ReturnCodeKeys.E014;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        if(tasks == null){
            return ReturnCodeKeys.E016;
        }

        int waitSize = taskQueue.size();
        for(int i = 0; i < waitSize; i++){
            TaskInfo item = taskQueue.get(i);
            int taskId = item.getTaskId();
            int index = i;
            for (int j = i + 1; j <waitSize; j++){
                TaskInfo item2 = taskQueue.get(j);
                if(item2.getTaskId() <= taskId){
                    taskId = item2.getTaskId();
                    index = j;
                }
            }

            if(index != i){
                TaskInfo temp = item;
                item = taskQueue.get(index);
                taskQueue.remove(i);
                taskQueue.add(i,item);

                taskQueue.remove(index);
                taskQueue.add(index,temp);
            }
        }

        for(int i = 0; i < taskQueue.size(); i++){
            tasks.add(taskQueue.get(i));
        }

        return ReturnCodeKeys.E015;
    }

}
