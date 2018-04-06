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

public class RuleConstanats {

    public static final String APPLICATION_ATTRIBUTE_ID="urn:oasis:names:tc:xacml:1.0:application:application-name";
    public static final String APPLICATION_ATTRIBUTE_CATEGORY="urn:oasis:names:tc:xacml:3.0:attribute-category:custom";
    public static final String APPLICATION_ATTRIBUTE_DATATYPE="http://www.w3.org/2001/XMLSchema#string";

    public static final String TCCODE_ATTRIBUTE_ID="http://wso2.org/claims/tcCodes";
    public static final String TCCODE_ATTRIBUTE_CATEGORY="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
    public static final String TCCODE_ATTRIBUTE_DATATYPE="http://www.w3.org/2001/XMLSchema#string";

    public static final String USER_ATTRIBUTE_ID="urn:oasis:names:tc:xacml:1.0:subject:subject-id";
    public static final String USER_ATTRIBUTE_CATEGORY="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
    public static final String USER_ATTRIBUTE_DATATYPE="http://www.w3.org/2001/XMLSchema#string";

    public static final String XACML_RESPONSE_DECISION_NODE = "Decision";


}


