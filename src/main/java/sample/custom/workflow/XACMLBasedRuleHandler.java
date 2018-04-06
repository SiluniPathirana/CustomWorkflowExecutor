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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.wso2.balana.utils.exception.PolicyBuilderException;
import org.wso2.balana.utils.policy.PolicyBuilder;
import org.wso2.balana.utils.policy.dto.RequestElementDTO;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.entitlement.EntitlementException;
import org.wso2.carbon.identity.entitlement.EntitlementService;
import org.wso2.carbon.identity.entitlement.common.EntitlementPolicyConstants;
import org.wso2.carbon.identity.entitlement.common.dto.RequestDTO;
import org.wso2.carbon.identity.entitlement.common.dto.RowDTO;
import org.wso2.carbon.identity.entitlement.common.util.PolicyCreatorUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class XACMLBasedRuleHandler {

    private static final Log log = LogFactory.getLog(XACMLBasedRuleHandler.class);


    public boolean isAllowedToSubscribe(String tenantDomainName, String applicationName, String termsCondiotion) {

        if (log.isDebugEnabled()) {
            log.debug("---------In policy provisioning flow...-----------------");
        }

        try {
            RequestDTO requestDTO = createRequestDTO(tenantDomainName, applicationName, termsCondiotion);
            RequestElementDTO requestElementDTO = PolicyCreatorUtil.createRequestElementDTO(requestDTO);

            String requestString = PolicyBuilder.getInstance().buildRequest(requestElementDTO);
            log.debug("---------------------XACML request ------------------------------:\n" + requestString);
            EntitlementService entitlementService = new EntitlementService();
            String responseString = entitlementService.getDecision(requestString);
            if (log.isDebugEnabled()) {
                log.debug("XACML response :\n" + responseString);
            }
            Boolean isAuthorized = evaluateXACMLResponse(responseString);
            if (isAuthorized) {
                return true;
            }
        } catch (PolicyBuilderException e) {
            log.error("Policy Builder Exception occurred", e);
        } catch (EntitlementException e) {
            log.error("Entitlement Exception occurred", e);
        } catch (Exception e) {
            log.error("Evaluate Request Exception occured", e);
        }
        return false;
    }


    private RequestDTO createRequestDTO(String user, String applicationName, String tc1) {
        List<RowDTO> rowDTOs = new ArrayList();


        RowDTO userDTO = createRowDTO(user, RuleConstanats.USER_ATTRIBUTE_DATATYPE,
                RuleConstanats.USER_ATTRIBUTE_ID, RuleConstanats.USER_ATTRIBUTE_CATEGORY);

        RowDTO requestApplicationNameDTO = createRowDTO(applicationName, RuleConstanats.APPLICATION_ATTRIBUTE_DATATYPE,
                RuleConstanats.APPLICATION_ATTRIBUTE_ID, RuleConstanats.APPLICATION_ATTRIBUTE_CATEGORY);

        RowDTO requestTermsCondition1DTO = createRowDTO(tc1, RuleConstanats.TCCODE_ATTRIBUTE_DATATYPE,
                RuleConstanats.TCCODE_ATTRIBUTE_ID, RuleConstanats.TCCODE_ATTRIBUTE_CATEGORY);


        rowDTOs.add(userDTO);
        rowDTOs.add(requestApplicationNameDTO);
        rowDTOs.add(requestTermsCondition1DTO);

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setRowDTOs(rowDTOs);
        return requestDTO;
    }

    private RowDTO createRowDTO(String resourceName, String dataType, String attributeId, String categoryValue) {

        RowDTO rowDTOTenant = new RowDTO();
        rowDTOTenant.setAttributeValue(resourceName);
        rowDTOTenant.setAttributeDataType(dataType);
        rowDTOTenant.setAttributeId(attributeId);
        rowDTOTenant.setCategory(categoryValue);
        return rowDTOTenant;
    }


    private boolean evaluateXACMLResponse(String xacmlResponse) throws Exception {

        try {
            DocumentBuilderFactory documentBuilderFactory = IdentityUtil.getSecuredDocumentBuilderFactory();
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xacmlResponse));
            Document doc = db.parse(is);

            String decision = "";
            NodeList decisionNode = doc.getDocumentElement().getElementsByTagName(
                    RuleConstanats.XACML_RESPONSE_DECISION_NODE);
            if (decisionNode != null && decisionNode.item(0) != null) {
                decision = decisionNode.item(0).getTextContent();
            }
            if (decision.equalsIgnoreCase(EntitlementPolicyConstants.RULE_EFFECT_PERMIT)
                    || decision.equalsIgnoreCase(EntitlementPolicyConstants.RULE_EFFECT_NOT_APPLICABLE)) {
                return true;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return false;
    }
}
