package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.ConsumptionInfo;
import com.migu.schedule.info.TaskInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/*
*类名和方法不能修改
 */
public class Schedule {

  //任务存储
    private TreeMap<Integer, ConsumptionInfo> taskIds = new TreeMap<Integer, ConsumptionInfo>();

    //挂起队列，根据消耗率分组
    private TreeMap<Integer, TreeSet<ConsumptionInfo>> hangUpQueue = new TreeMap<Integer, TreeSet<ConsumptionInfo>>();

    //服务器节点，根据nodeId分组
    private TreeMap<Integer, TreeSet<ConsumptionInfo>> nodeServers = new TreeMap<Integer, TreeSet<ConsumptionInfo>>();

    public int init() {
        taskIds = new TreeMap<Integer, ConsumptionInfo>();
        hangUpQueue = new TreeMap<Integer, TreeSet<ConsumptionInfo>>();
        nodeServers = new TreeMap<Integer, TreeSet<ConsumptionInfo>>();
        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {

        //不符合要求
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }

        //节点已存在
        if (nodeServers.containsKey(nodeId)) {
            return ReturnCodeKeys.E005;
        }

        //准备节点基础信息
        TreeSet<ConsumptionInfo> consumptionInfos = new TreeSet<ConsumptionInfo>();
        nodeServers.put(nodeId, consumptionInfos);
        return ReturnCodeKeys.E003;
    }

    public int unregisterNode(int nodeId) {

        //不符合要求
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }

        //节点不存在
        if (!nodeServers.containsKey(nodeId)) {
            return ReturnCodeKeys.E007;
        }

        //移除节点
        TreeSet<ConsumptionInfo> consumptionInfos = nodeServers.remove(nodeId);

        //将节点内容放入挂起队列
        Iterator<ConsumptionInfo> itr = consumptionInfos.iterator();
        while (itr.hasNext()) {
            ConsumptionInfo consumptionInfo = itr.next();
            consumptionInfo.setNodeId(-1);//离开节点变为-1
            hangUp(consumptionInfo);
        }
        return ReturnCodeKeys.E006;
    }
    

    private void hangUp(ConsumptionInfo consumptionInfo) {

        TreeSet<ConsumptionInfo> consumptionInfos = hangUpQueue.get(consumptionInfo.getConsumption());
        if (null == consumptionInfos) {
            consumptionInfos = new TreeSet<ConsumptionInfo>();
            hangUpQueue.put(consumptionInfo.getConsumption(), consumptionInfos);
        }
        consumptionInfos.add(consumptionInfo);
    }


    public int addTask(int taskId, int consumption) {
        //不符合要求
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }

        //任务已存在
        if (taskIds.containsKey(taskId)) {
            return ReturnCodeKeys.E010;
        }
        ConsumptionInfo consumptionInfo = new ConsumptionInfo(taskId, consumption);
        taskIds.put(taskId, consumptionInfo);

        //放入挂起队列
        hangUp(consumptionInfo);
        return ReturnCodeKeys.E008;
    }


    public int deleteTask(int taskId) {
        //不符合要求
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }

        //任务已存在
        if (!taskIds.containsKey(taskId)) {
            return ReturnCodeKeys.E012;
        }

        //删除存储
        ConsumptionInfo consumptionInfo = taskIds.remove(taskId);

        //删除挂起队列中的
        deleteTask(consumptionInfo, this.hangUpQueue);

        //删除节点中的
        deleteTask(consumptionInfo, this.nodeServers);
        return ReturnCodeKeys.E011;
    }

    private void deleteTask(ConsumptionInfo consumptionInfo, TreeMap<Integer, TreeSet<ConsumptionInfo>> consumptionInfosMap) {
        Iterator<Map.Entry<Integer, TreeSet<ConsumptionInfo>>> itr = consumptionInfosMap.entrySet().iterator();
        while (itr.hasNext()) {
            itr.next().getValue().remove(consumptionInfo);
        }
    }


    public int scheduleTask(int threshold) {
        // TODO 方法未实现
        return ReturnCodeKeys.E000;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        // TODO 方法未实现
        return ReturnCodeKeys.E000;
    }

}
