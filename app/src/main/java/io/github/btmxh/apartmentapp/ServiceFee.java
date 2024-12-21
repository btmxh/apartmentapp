package io.github.btmxh.apartmentapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import io.github.btmxh.apartmentapp.DatabaseConnection.FeeType;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceFee {
    public static final int NULL_ID = -1;
    private int id = NULL_ID;
    private final SimpleObjectProperty<FeeType> type = new SimpleObjectProperty<>();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleLongProperty value1 = new SimpleLongProperty();
    private final SimpleLongProperty value2 = new SimpleLongProperty();
    private final SimpleObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<LocalDate> deadline = new SimpleObjectProperty<>();

    public ServiceFee(int id, FeeType type, String name, long value1, long value2) {
        this.id = id;
        this.type.set(type);
        this.name.set(name);
        this.value1.set(value1);
        this.value2.set(value2);
    }

    public ServiceFee(int id, FeeType type, String name, long value1, long value2, LocalDate startDate, LocalDate deadline) {
        this.id = id;
        this.type.set(type);
        this.name.set(name);
        this.value1.set(value1);
        this.value2.set(value2);
        this.startDate.set(startDate);
        this.deadline.set(deadline);
    }


    public FeeType getType() {
        return type.get();
    }

    public void setType(FeeType type) {
        this.type.set(type);
    }

    public long getValue1() {
        return value1.get();
    }

    public void setValue1(long value) {
        this.value1.set(value);
    }

    public long getValue2() {
        return value2.get();
    }

    public void setValue2(long value) {
        this.value2.set(value);
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

    public SimpleStringProperty name() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public SimpleObjectProperty<LocalDate> startDate() {
        return startDate;
    }

    public void setStartDate(LocalDate date) {
        this.startDate.set(date);
    }

    public void setDeadline(LocalDate date) {
        this.deadline.set(date);
    }

    public LocalDate getDeadline() {
        return deadline.get();
    }

    public SimpleObjectProperty<LocalDate> deadline() {
        return deadline;
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

            throw new IllegalArgumentException("Công thức đầu cuối không hợp lệ");
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
