package elon.activiti.rest;

import elon.activiti.model.LeaveTask;
import elon.activiti.model.ResponseModel;
import elon.activiti.service.ActivitiProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Activiti流程管理类。
 */
@RestController
@RequestMapping("v1/activiti")
@Api(tags = "Activiti流程管理类")
public class ActivitiProcessController {

    @Autowired
    private ActivitiProcessService activitiProcessService;

    /**
     * 部署流程
     * @param bpmnXmlFile xml定义文件路径
     * @param bmpnImageFile bpmn图片文件路径
     * @return 处理结果
     */
    @PostMapping("/deploy")
    @ApiOperation(value = "部署流程")
    public ResponseModel<String> deploy(@ApiParam(value = "xml定义文件路径", defaultValue = "leave.bpmn20.xml")
                                        @RequestHeader("bpmnXmlFile") String bpmnXmlFile,
                                        @ApiParam(value = "bpmn图片文件路径", defaultValue = "leave.bpmn20.png")
                                        @RequestHeader("bmpnImageFile") String bmpnImageFile){
        String id = activitiProcessService.deploy(bpmnXmlFile, bmpnImageFile);
        return ResponseModel.success(id);
    }

    /**
     * 创建流程实例。
     *
     * @param processDefinitionId 部署ID
     * @return 流程定义ID
     */
    @PostMapping("/v1/create-process-instance")
    @ApiOperation(value = "创建流程实例")
    public ResponseModel<String> createProcessInstance(@ApiParam(value = "流程定义ID", defaultValue = "requestleave:9:22505")
                                                       @RequestHeader("processDefinitionId") String processDefinitionId,
                                                       @ApiParam(value = "申请人", defaultValue = "zhangsan")
                                                       @RequestHeader("applyAssignee") String applyAssignee,
                                                       @ApiParam(value = "审批人", defaultValue = "lisi")
                                                       @RequestHeader("approvalAssignee") String approvalAssignee) {
        String processInstanceId =  activitiProcessService.createProcessInstance(processDefinitionId,
                applyAssignee, approvalAssignee);
        return ResponseModel.success(processInstanceId);
    }

    @PostMapping("/v1/query-todo-tasks")
    @ApiOperation(value = "查询待处理任务列表")
    public ResponseModel<List<LeaveTask>> queryToDoTasks(@ApiParam(value = "当前处理人", defaultValue = "zhangsan")
                                              @RequestParam("assignee") String assignee) {
        return ResponseModel.success(activitiProcessService.queryToDoTasks(assignee));
    }

    /**
     * 提交任务。
     *
     * @param taskId 任务ID
     * @return 处理结果
     */
    @PostMapping("/v1/complete-task")
    @ApiOperation(value = "提交任务")
    public ResponseModel<String> completeTask(@ApiParam(value = "任务ID") @RequestHeader("taskId") String taskId,
                                              @ApiParam(value = "执行参数") @RequestBody Map<String, Object> paramMap) {
        activitiProcessService.completeTask(taskId, paramMap);
        return ResponseModel.success("success");
    }

    public void getProcessDefineImge(HttpServletResponse response){

    }
}
