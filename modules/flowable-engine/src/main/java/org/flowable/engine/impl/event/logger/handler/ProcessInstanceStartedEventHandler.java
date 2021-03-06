/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.impl.event.logger.handler;

import java.util.HashMap;
import java.util.Map;

import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.delegate.event.FlowableEntityWithVariablesEvent;
import org.flowable.engine.impl.persistence.entity.EventLogEntryEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;

/**
 * @author Joram Barrez
 */
public class ProcessInstanceStartedEventHandler extends AbstractDatabaseEventLoggerEventHandler {

    private static final String TYPE = "PROCESSINSTANCE_START";

    @Override
    public EventLogEntryEntity generateEventLogEntry(CommandContext commandContext) {

        FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
        ExecutionEntity processInstanceEntity = (ExecutionEntity) entityEvent.getEntity();

        Map<String, Object> data = new HashMap<>();
        putInMapIfNotNull(data, Fields.ID, processInstanceEntity.getId());
        putInMapIfNotNull(data, Fields.BUSINESS_KEY, processInstanceEntity.getBusinessKey());
        putInMapIfNotNull(data, Fields.PROCESS_DEFINITION_ID, processInstanceEntity.getProcessDefinitionId());
        putInMapIfNotNull(data, Fields.NAME, processInstanceEntity.getName());
        putInMapIfNotNull(data, Fields.CREATE_TIME, timeStamp);

        if (event instanceof FlowableEntityWithVariablesEvent) {
            FlowableEntityWithVariablesEvent eventWithVariables = (FlowableEntityWithVariablesEvent) event;
            if (eventWithVariables.getVariables() != null && !eventWithVariables.getVariables().isEmpty()) {
                Map<String, Object> variableMap = new HashMap<>();
                for (Object variableName : eventWithVariables.getVariables().keySet()) {
                    putInMapIfNotNull(variableMap, (String) variableName, eventWithVariables.getVariables().get(variableName));
                }
                putInMapIfNotNull(data, Fields.VARIABLES, variableMap);
            }
        }

        return createEventLogEntry(TYPE, processInstanceEntity.getProcessDefinitionId(), processInstanceEntity.getId(), null, null, data);
    }

}
