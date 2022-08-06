package gr.bouzas.businessapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;

public class PersonAggregationDmnTest {

    private final static String DMN_MODEL_NAME = "Person Aggregation";
    private final static String DMN_MODEL_NAMESPACE = "https://kiegroup.org/dmn/PersonAggregation";

    private final static String DECISION_UNDER_TEST = "Aggregated Person";

    private DMNRuntime runtime;
    private DMNModel dmnModel;

    @Before
    public void init() {
        KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
        KieRuntimeFactory kieRuntimeFactory = KieRuntimeFactory.of(kieClasspathContainer.getKieBase());

        runtime = kieRuntimeFactory.get(DMNRuntime.class);
        dmnModel = runtime.getModel(DMN_MODEL_NAMESPACE, DMN_MODEL_NAME);
    }

    @Test
    public void PersonAggregationDecisionTest() {
        DMNContext context = runtime.newContext();

        final Person p = Person
                .builder()
                .name("Vasilis")
                .surname("Bouzas")
                .dob(LocalDate.of(1994, Month.OCTOBER, 20))
                .build();

        context.set("Person", p);


        DMNResult aggregatedPersonDmnResult = runtime.evaluateByName(dmnModel, context, DECISION_UNDER_TEST);
        assertNotNull(aggregatedPersonDmnResult);
        
        DMNDecisionResult aggregatedPersonResult = aggregatedPersonDmnResult.getDecisionResultByName(DECISION_UNDER_TEST);
        assertNotNull(aggregatedPersonResult);
        assertEquals(DecisionEvaluationStatus.SUCCEEDED, aggregatedPersonResult.getEvaluationStatus());

        // DMN Cannot Convert Context to Object (but process can)
        @SuppressWarnings("unchecked")
        Map<String, Object> aggregatedPerson = (Map<String, Object>) aggregatedPersonResult.getResult();
        assertNotNull(aggregatedPerson);
        assertEquals("Vasilis Bouzas", aggregatedPerson.get("fullname"));
        assertEquals(BigDecimal.valueOf(27), aggregatedPerson.get("age"));

    }

}
