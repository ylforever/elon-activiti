package elon.activiti.rest;

import elon.activiti.model.LeaveTask;
import elon.activiti.model.ResponseModel;
import elon.activiti.service.ActivitiProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bpmnXmlFile", value = "xml定义文件路径", defaultValue = "leave.bpmn20.xml"),
            @ApiImplicitParam(name = "bmpnImageFile", value = "bpmn图片文件路径", defaultValue = "leave.png")
    })
    public ResponseModel<String> deploy(@RequestHeader("bpmnXmlFile") String bpmnXmlFile,
                                        @RequestHeader("bmpnImageFile") String bmpnImageFile) {
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "流程定义ID", defaultValue = "requestleave:9:22505"),
            @ApiImplicitParam(name = "applyAssignee", value = "申请人", defaultValue = "zhangsan"),
            @ApiImplicitParam(name = "approvalAssignee", value = "审批人", defaultValue = "lisi")
    })
    public ResponseModel<String> createProcessInstance(@RequestHeader("processDefinitionId") String processDefinitionId,
                                                       @RequestHeader("applyAssignee") String applyAssignee,
                                                       @RequestHeader("approvalAssignee") String approvalAssignee) {
        String processInstanceId =  activitiProcessService.createProcessInstance(processDefinitionId,
                applyAssignee, approvalAssignee);
        return ResponseModel.success(processInstanceId);
    }

    @GetMapping("/v1/query-todo-tasks")
    @ApiOperation(value = "查询待处理任务列表")
    public ResponseModel<List<LeaveTask>> queryToDoTasks(@ApiParam(value = "当前处理人", defaultValue = "zhangsan")
                                              @RequestParam("assignee") String assignee) {
        return ResponseModel.success(activitiProcessService.queryToDoTasks(assignee));
    }


    @GetMapping("/v1/query-done-tasks")
    @ApiOperation(value = "查询已处理任务列表")
    public ResponseModel<List<LeaveTask>> queryDoneTasks(@ApiParam(value = "当前处理人", defaultValue = "zhangsan")
                                                         @RequestParam("assignee") String assignee) {
        return ResponseModel.success(activitiProcessService.queryDoneTasks(assignee));
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

    /**
     * 获取流程定义图片。
     *
     * @param response http请求响应
     * @param processDefineId 流程定义ID
     */
    @GetMapping("/v1/process-image")
    @ApiOperation(value = "获取流程定义图片")
    public void getProcessDefineImge(HttpServletResponse response,
                                     @ApiParam(value = "流程定义ID")
                                     @RequestParam("processDefineId") String processDefineId){
        InputStream inputStream = activitiProcessService.getProcessDefineResource(processDefineId, 2);

        byte[] bytes = new byte[1024];
        response.setContentType("image/png");

        try {
            OutputStream outputStream = response.getOutputStream();
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取流程定义XML。
     *
     * @param response http请求响应
     * @param processDefineId 流程定义ID
     */
    @GetMapping("/v1/process-xml")
    @ApiOperation(value = "获取流程定义XML")
    public void getProcessDefineXML(HttpServletResponse response,
                                     @ApiParam(value = "流程定义ID")
                                     @RequestParam("processDefineId") String processDefineId){
        InputStream inputStream = activitiProcessService.getProcessDefineResource(processDefineId, 1);

        byte[] bytes = new byte[1024];
        response.setContentType("text/xml");

        try {
            OutputStream outputStream = response.getOutputStream();
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转派任务责任人
     * @param taskId 任务ID
     * @param userId 转派人的账号ID
     */
    @PostMapping("/transfer-task/{taskId}")
    @ApiOperation(value = "转派任务责任人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID"),
            @ApiImplicitParam(name = "userId", value = "转派人的账号ID", defaultValue = "wang wu")
    })
    public void transferAssignee(@PathVariable("taskId") String taskId, @RequestBody String userId) {
        activitiProcessService.transferAssignee(taskId, userId);
    }
}
