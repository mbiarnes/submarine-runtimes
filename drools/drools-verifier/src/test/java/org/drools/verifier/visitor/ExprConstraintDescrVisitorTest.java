package org.drools.verifier.visitor;

import org.drools.base.evaluators.Operator;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.*;
import org.drools.verifier.data.VerifierComponent;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class ExprConstraintDescrVisitorTest extends TestBase {

    @Test
    public void testVisitPerson() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr1.drl"));

        assertNotNull(packageDescr);

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<StringRestriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);
        Collection<Field> allFields = verifierData.getAll(VerifierComponentType.FIELD);

        assertEquals(3, allRestrictions.size());
        assertEquals(3, allFields.size());

        assertContainsField("name");
        assertContainsField("lastName");
        assertContainsField("age");

        assertContainsStringRestriction(Operator.EQUAL, "toni");
        assertContainsStringRestriction(Operator.NOT_EQUAL, "Lake");
        assertContainsNumberRestriction(Operator.GREATER, 20);
        assertContainsEval("eval( true )");
    }

    @Test
    public void testVisitAnd() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr2.drl"));

        assertNotNull(packageDescr);

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<StringRestriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        assertEquals(2, allRestrictions.size());
        assertContainsFields(1);


        assertContainsField("age");

        assertContainsNumberRestriction(Operator.GREATER, 0);
        assertContainsNumberRestriction(Operator.LESS, 100);
    }

    @Test
    public void testVisitVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr3.drl"));

        assertNotNull(packageDescr);

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<StringRestriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        assertEquals(1, allRestrictions.size());
        assertContainsFields(1);

        assertContainsField("age");
        assertContainsVariable("Test 1", "var");

        assertContainsVariableRestriction(Operator.EQUAL, "var");
    }

    private void assertContainsEval(String eval) {
        Collection<VerifierComponent> allEvals = verifierData.getAll(VerifierComponentType.PREDICATE);

        for (VerifierComponent component : allEvals) {
            Eval evalObject = (Eval) component;
            if (eval.equals(evalObject.getContent())) {
                return;
            }
        }

        fail(String.format("Could not find Eval : %s ", eval));
    }

    private void assertContainsVariableRestriction(Operator operator, String variableName) {
        Collection<Restriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        for (Restriction restriction : allRestrictions) {
            if (restriction instanceof VariableRestriction) {
                VariableRestriction variableRestriction = (VariableRestriction) restriction;
                if (variableName.equals(variableRestriction.getVariable().getName()) && operator.equals(variableRestriction.getOperator())) {
                    return;
                }
            }
        }

        fail(String.format("Could not find VariableRestriction: Operator : %s Variable name: %s", operator, variableName));
    }

    private void assertContainsStringRestriction(Operator operator, String value) {
        Collection<Restriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        for (Restriction restriction : allRestrictions) {
            if (restriction instanceof StringRestriction) {
                StringRestriction stringLiteral = (StringRestriction) restriction;
                if (value.equals(stringLiteral.getValueAsString()) && operator.equals(stringLiteral.getOperator())) {
                    return;
                }
            }
        }

        fail(String.format("Could not find StringRestriction: Operator : %s Value: %s", operator, value));
    }

    private void assertContainsNumberRestriction(Operator operator, Number value) {
        Collection<Restriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        for (Restriction restriction : allRestrictions) {
            if (restriction instanceof NumberRestriction) {
                NumberRestriction numberRestriction = (NumberRestriction) restriction;
                if (value.equals(numberRestriction.getValue()) && operator.equals(numberRestriction.getOperator())) {
                    return;
                }
            }
        }

        fail(String.format("Could not find NumberRestriction: Operator : %s Value: %s", operator, value));
    }

}
