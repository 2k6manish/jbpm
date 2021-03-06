/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.jbpm.services.task.wih;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExternalTaskEventListener implements TaskLifeCycleEventListener {

    private RuntimeManagerRegistry registry = RuntimeManagerRegistry.get();
    private static final Logger logger = LoggerFactory.getLogger(ExternalTaskEventListener.class);
 
    public ExternalTaskEventListener() {
    }

    public void processTaskState(Task task) {

        long workItemId = task.getTaskData().getWorkItemId();
        long processInstanceId = task.getTaskData().getProcessInstanceId();
        RuntimeManager manager = getManager(task);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        KieSession session = runtime.getKieSession();
        
        if (task.getTaskData().getStatus() == Status.Completed) {
            String userId = task.getTaskData().getActualOwner().getId();
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("ActorId", userId);
            long contentId = task.getTaskData().getOutputContentId();
            if (contentId != -1) {
                Content content = runtime.getTaskService().getContentById(contentId);
                ClassLoader cl = null;
                if (manager instanceof InternalRuntimeManager) {
                    cl = ((InternalRuntimeManager)manager).getEnvironment().getClassLoader();
                }
                Object result = ContentMarshallerHelper.unmarshall(content.getContent(), session.getEnvironment(), cl);
                results.put("Result", result);
                if (result instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) result;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            results.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                }

                session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
            } else {
                session.getWorkItemManager().completeWorkItem(workItemId, results);
            }
        } else {
            session.getWorkItemManager().abortWorkItem(workItemId);
        }
    }

    public void afterTaskActivatedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskClaimedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskSkippedEvent(TaskEvent event) {
    	Task task = event.getTask();
        long processInstanceId = task.getTaskData().getProcessInstanceId();
        if (processInstanceId <= 0) {
            return;
        }
        processTaskState(task);
    }

    public void afterTaskStartedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskStoppedEvent(Task ti) {
        // DO NOTHING
    }

    public void afterTaskCompletedEvent(TaskEvent event) {
    	Task task = event.getTask();
        long processInstanceId = task.getTaskData().getProcessInstanceId();
        if (processInstanceId <= 0) {
            return;
        }
        RuntimeEngine runtime = getManager(task).getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
        KieSession session = runtime.getKieSession();
        if (session != null) {
            logger.debug(">> I've recieved an event for a known session (" + task.getTaskData().getProcessSessionId()+")");
            processTaskState(task);
        } else {
            logger.error("EE: I've recieved an event but the session is not known by this handler ( "+task.getTaskData().getProcessSessionId()+")");
        }
    }

    public void afterTaskFailedEvent(TaskEvent event) {
    	Task task = event.getTask();
        long processInstanceId = task.getTaskData().getProcessInstanceId();
        if (processInstanceId <= 0) {
            return;
        }
        processTaskState(task);
    }

    public void afterTaskAddedEvent(TaskEvent event) {
        
        // DO NOTHING
    }

    public void afterTaskExitedEvent(TaskEvent event) {
        // DO NOTHING
    }
    
    public RuntimeManager getManager(Task task) {           
        return registry.getManager(task.getTaskData().getDeploymentId());
        
    }


    @Override
    public void afterTaskReleasedEvent(TaskEvent event) {
        
    }

    @Override
    public void afterTaskResumedEvent(TaskEvent event) {
        
    }

    @Override
    public void afterTaskSuspendedEvent(TaskEvent event) {
        
    }

    @Override
    public void afterTaskForwardedEvent(TaskEvent event) {
        
    }

    @Override
    public void afterTaskDelegatedEvent(TaskEvent event) {
        
    }

	@Override
	public void beforeTaskActivatedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskClaimedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskSkippedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskStartedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskStoppedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskCompletedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskFailedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskAddedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskExitedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskReleasedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskResumedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskSuspendedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskForwardedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void beforeTaskDelegatedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void afterTaskActivatedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void afterTaskClaimedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void afterTaskStartedEvent(TaskEvent event) {
		
		
	}

	@Override
	public void afterTaskStoppedEvent(TaskEvent event) {
		
		
	}
}
