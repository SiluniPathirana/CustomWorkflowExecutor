/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/


package sample.custom.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.WorkflowResponse;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.SubscriptionWorkflowDTO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowDTO;
import org.wso2.carbon.apimgt.impl.workflow.*;
import java.util.List;

public class CustomSubscriptionCreationWSWorkflowExecutor extends WorkflowExecutor {

    private static final Log log = LogFactory.getLog(CustomSubscriptionCreationWSWorkflowExecutor.class);
    private boolean result = false;
    private String applicationName;
    private String subscriber;
    private String claimValue;

    @Override
    public String getWorkflowType() {
        return WorkflowConstants.WF_TYPE_AM_SUBSCRIPTION_CREATION;
    }

    @Override
    public WorkflowResponse execute(WorkflowDTO workflowDTO) throws WorkflowException {

        try {


            String logMessage = "------------Executing CustomSubscriptionCreationWSWorkflowExecutor-----------------";
            log.debug(logMessage);
            XACMLBasedRuleHandler xacmlBasedRuleHandler = new XACMLBasedRuleHandler();
            applicationName = ((SubscriptionWorkflowDTO) workflowDTO).getApplicationName();
            subscriber = ((SubscriptionWorkflowDTO) workflowDTO).getSubscriber();


            log.debug(claimValue);

            result = xacmlBasedRuleHandler.isAllowedToSubscribe("admin@carbon.super",
                    "admindcpp_testXACML_PRODUCTION", "20");
            log.debug(result);
            super.execute(workflowDTO);
            if (result) {
                workflowDTO.setStatus(WorkflowStatus.APPROVED);
                complete(workflowDTO);
            } else {
            }


        } catch (java.lang.Exception e) {
            log.error("Error sending out message", e);
            throw new WorkflowException("Error sending out message", e);
        }

        return new GeneralWorkflowResponse();
    }

    @Override
    public WorkflowResponse complete(WorkflowDTO workflowDTO) throws WorkflowException {
        workflowDTO.setUpdatedTime(System.currentTimeMillis());
        super.complete(workflowDTO);
        ApiMgtDAO apiMgtDAO = ApiMgtDAO.getInstance();
        try {
            apiMgtDAO.updateSubscriptionStatus(
                    Integer.parseInt(workflowDTO.getWorkflowReference()), APIConstants.SubscriptionStatus.UNBLOCKED);
        } catch (APIManagementException e) {
            throw new WorkflowException("Could not complete subscription creation workflow", e);
        }
        return new GeneralWorkflowResponse();
    }


    public List<WorkflowDTO> getWorkflowDetails(String s) throws WorkflowException {
        return null;
    }
}
