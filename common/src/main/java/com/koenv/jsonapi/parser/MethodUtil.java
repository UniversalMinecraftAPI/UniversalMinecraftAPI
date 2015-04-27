package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.blocks.Block;
import com.koenv.jsonapi.parser.blocks.Method;
import com.koenv.jsonapi.parser.blocks.ThisVariable;
import com.koenv.jsonapi.parser.parameters.IntegerParameter;
import com.koenv.jsonapi.parser.parameters.Parameter;
import com.koenv.jsonapi.parser.parameters.StringParameter;

import java.util.List;

public class MethodUtil {
    public static String printBlocks(List<Block> blocks) {
        StringBuilder builder = new StringBuilder();
        for (Block block : blocks) {
            if (block instanceof Method) {
                builder.append(printMethod((Method) block));
            } else if (block instanceof ThisVariable) {
                builder.append("this");
            }
            builder.append(".");
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static String printMethod(Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append(method.getMethodName());
        builder.append('(');
        for (Parameter parameter : method.getParameters()) {
            builder.append(printParameter(parameter));
        }
        builder.append(')');
        return builder.toString();
    }

    public static String printParameter(Parameter parameter) {
        if (parameter instanceof IntegerParameter) {
            return printIntegerParameter((IntegerParameter) parameter);
        } else if (parameter instanceof StringParameter) {
            return printStringParameter((StringParameter) parameter);
        }
        return "undefined";
    }

    public static String printIntegerParameter(IntegerParameter integerParameter) {
        return Integer.toString(integerParameter.getValue());
    }

    public static String printStringParameter(StringParameter stringParameter) {
        return '"' + stringParameter.getValue() + '"';
    }
}
