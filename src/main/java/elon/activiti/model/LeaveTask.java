package elon.activiti.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 请假任务
 */
@Data
@ApiModel(value = "请假任务模型")
public class LeaveTask {

    @ApiModelProperty(value = "任务ID")
    private String taskId = "";

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "流程定义ID")
    private String processDefinitionId = "";
}
