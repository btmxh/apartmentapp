package io.github.btmxh.apartmentapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceFee {
    public static final int NULL_ID = -1;
    private int id = NULL_ID;
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleObjectProperty<Formula> formula = new SimpleObjectProperty<>();

    public ServiceFee() {
    }

    public ServiceFee(int id, String name, Formula formula) {
        this.id = id;
        this.name.set(name);
        this.formula.set(formula);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Formula getFormula() {
        return formula.get();
    }

    public void setFormula(Formula formula) {
        this.formula.set(formula);
    }

    public static final class Formula implements FormulaTerminal {
        private final String formula;
        private final ExpressionEvaluator evaluator;
        private final List<Pair<String, FormulaTerminal>> variables;

        public Formula(String formula, Map<String, FormulaTerminal> variables) throws CompileException {
            this.formula = formula;
            this.variables = variables.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).toList();
            final var varNames = this.variables.stream().map(Pair::getKey).toArray(String[]::new);
            final var varClasses = new Class[variables.size()];
            Arrays.fill(varClasses, double.class);
            this.evaluator = new ExpressionEvaluator(formula, double.class, varNames, varClasses);
        }

        public double calculate() throws Exception {
            final var args = new Object[variables.size()];
            for (int i = 0; i < args.length; i++) {
                args[i] = variables.get(i).getValue().calculate();
            }
            return (double) this.evaluator.evaluate(args);
        }

        private static final ObjectMapper mapper = new ObjectMapper();

        public String getExpression() {
            return formula;
        }

        public List<Pair<String, FormulaTerminal>> getVariables() {
            return variables;
        }

        private static class RawFormula {
            public String formula;
            public Map<String, JsonNode> terminals;

            public RawFormula() {
            }

            public RawFormula(String formula, Map<String, JsonNode> terminals) {
                this.formula = formula;
                this.terminals = terminals;
            }
        }

        public JsonNode toJSON() {
            final var data = new RawFormula(formula, variables.stream().map(p -> new Pair<>(p.getKey(), p.getValue().toJSON())).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
            return mapper.valueToTree(data);
        }

        private static FormulaTerminal parseFormulaTerminal(JsonNode json) {
            if(json.isNumber()) {
                return new ConstFormulaTerminal(json.doubleValue());
            }

            throw new IllegalArgumentException("Invalid formula terminal");
        }

        public static Formula fromJSON(JsonNode json) throws JsonProcessingException, CompileException {
            final var data = mapper.treeToValue(json, RawFormula.class);
            final var args = new HashMap<String, FormulaTerminal>();
            for(final var arg : data.terminals.entrySet()) {
                args.put(arg.getKey(), parseFormulaTerminal(arg.getValue()));
            }
            return new Formula(data.formula, args);
        }
    }

    public sealed interface FormulaTerminal {
        double calculate() throws Exception;
        JsonNode toJSON();
    }

    public static final class ConstFormulaTerminal implements FormulaTerminal {
        private final double value;

        public ConstFormulaTerminal(double value) {
            this.value = value;
        }

        @Override
        public double calculate() {
            return value;
        }

        @Override
        public JsonNode toJSON() {
            return new DoubleNode(value);
        }
    }
}
