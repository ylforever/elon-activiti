package elon.activiti.service;

import elon.activiti.model.LeaveTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程服务类
 */
@Component
public class ActivitiProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    /**
     * 部署流程。
     *
     * @param bpmnXmlFile
     * @param bmpnImageFile
     */
    public String deploy(String bpmnXmlFile, String bmpnImageFile) {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(bpmnXmlFile)
                .addClasspathResource(bmpnImageFile)
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();

        return processDefinition.getId();
    }

    /**
     * 启动流程实例，返回流程实例ID。
     *
     * @param processDefinitionId 流程定义ID
     * @return 流程实例ID
     */
    public String createProcessInstance(String processDefinitionId, String applyAssignee, String approvalAssignee) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("apply_id_assignee", applyAssignee);
        paramMap.put("approval_id_assignee", approvalAssignee);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, paramMap);
        return processInstance.getProcessInstanceId();
    }

    /**
     * 查询用户待办任务列表。
     *
     * @param assignee 用户
     * @return 任务列表
     */
    public List<LeaveTask> queryToDoTasks(String assignee) {
        List<Task> taskList  = taskService.createTaskQuery().taskAssignee(assignee).list();

        List<LeaveTask> leaveTasks = new ArrayList<>();
        for (Task task : taskList) {
            LeaveTask leaveTask = new LeaveTask();
            leaveTask.setTaskId(task.getId());
            leaveTask.setName(task.getName());
            leaveTask.setProcessDefinitionId(task.getProcessDefinitionId());
            leaveTasks.add(leaveTask);
        }
        return leaveTasks;
    }

    /**
     * 转派责任人
     * @param taskId 任务ID
     * @param userId 转派的人ID
     */
    public void transferAssignee(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
    }

    /**
     * 查询已处理任务列表。
     *
     * @param assignee 用户
     * @return 已处理任务列表
     */
    public List<LeaveTask> queryDoneTasks(String assignee) {
        List<HistoricTaskInstance> taskList  = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .finished()
                .list();

        List<LeaveTask> leaveTasks = new ArrayList<>();
        for (HistoricTaskInstance task : taskList) {
            LeaveTask leaveTask = new LeaveTask();
            leaveTask.setTaskId(task.getId());
            leaveTask.setName(task.getName());
            leaveTask.setProcessDefinitionId(task.getProcessDefinitionId());
            leaveTasks.add(leaveTask);
        }
        return leaveTasks;
    }

    /**
     * 提交任务。
     *
     * @param taskId 任务ID
     * @return 处理结果
     */
    public void completeTask(String taskId, Map<String, Object> paramMap) {
        taskService.complete(taskId, paramMap);
    }

    /**
     * 获取流程定义资源。
     *
     * @param processDefineId 流程定义ID
     * @param type 1-获取xml, 2-获取图片
     * @return 文件流
     */
    public InputStream getProcessDefineResource(String processDefineId, int type) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefineId).singleResult();

        String resourceName = "";
        if (type == 1) {
            resourceName = processDefinition.getResourceName();
        } else if (type == 2) {
            resourceName = processDefinition.getDiagramResourceName();
        } else {
            return null;
        }

        return repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
    }
}
