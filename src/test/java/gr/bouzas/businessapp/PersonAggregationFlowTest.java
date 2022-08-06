package gr.bouzas.businessapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class PersonAggregationFlowTest extends JbpmJUnitBaseTestCase {

    private final static String PROCESS_UNDER_TEST = "PersonAggregationFlow";
    private final static String DMN_MODEL = "Person Aggregation.dmn";
    
    private RuntimeEngine runtimeEngine;

    @Before
    public void init() {
        Map<String, ResourceType> resources = Map.of(
            PROCESS_UNDER_TEST.concat(".bpmn"), ResourceType.BPMN2,
            DMN_MODEL, ResourceType.DMN
             
        );

        this.createRuntimeManager(resources);
        this.runtimeEngine = getRuntimeEngine();
    }

    @Test
    public void personAggregationFlowTest() {
        final Person p = Person
                .builder()
                .name("Vasilis")
                .surname("Bouzas")
                .dob(LocalDate.of(1994, Month.OCTOBER, 20))
                .build();

        Map<String, Object> processParams = Map.of("Person", p);

        final KieSession kieSession = this.runtimeEngine.getKieSession();
        final ProcessInstance processInstance = kieSession.startProcess(PROCESS_UNDER_TEST, processParams);

        assertNotNull(processInstance);
        // assertProcessInstanceCompleted(processInstance.getId());

        // Check org.jbpm.workflow.instance.node.RuleSetNodeInstance#processOutputs line 328
        // to understand how object casting is performed inside Engine
        Person aggregatedPerson = (Person) ((WorkflowProcessInstanceImpl) processInstance).getVariable("Person");
        assertNotNull(aggregatedPerson);
        assertEquals("Vasilis Bouzas", aggregatedPerson.getFullname());
        assertEquals(27, aggregatedPerson.getAge().intValue());
        
    }
    
}
