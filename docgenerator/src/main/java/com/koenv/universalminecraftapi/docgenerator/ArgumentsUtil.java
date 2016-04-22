package com.koenv.universalminecraftapi.docgenerator;

import com.koenv.universalminecraftapi.docgenerator.model.NamespacedMethod;
import com.koenv.universalminecraftapi.parser.ExpressionPrinter;
import com.koenv.universalminecraftapi.parser.expressions.*;

import java.util.*;

public class ArgumentsUtil {
    public static String getExample(NamespacedMethod method, Map<String, Object> exampleArguments) {
        NamespaceExpression namespaceExpression = new NamespaceExpression(method.getNamespace());

        List<Expression> arguments = new ArrayList<>();

        method.getArguments().forEach(argument -> {
            Object a = exampleArguments.get(argument.getName());
            if (a == null) {
                throw new IllegalArgumentException("Missing value for example argument " + argument.getName() + " for method " + method.getDeclaration());
            }
            arguments.add(convert(a));
        });

        MethodCallExpression methodCallExpression = new MethodCallExpression(method.getName(), arguments);

        ChainedMethodCallExpression root = new ChainedMethodCallExpression(Arrays.asList(namespaceExpression, methodCallExpression));

        return ExpressionPrinter.printExpression(root);
    }

    public static Expression convert(Object object) {
        if (object instanceof String) {
            return new StringExpression((String) object);
        } else if (object instanceof Double) {
            return new DoubleExpression((Double) object);
        } else if (object instanceof Integer) {
            return new IntegerExpression((Integer) object);
        } else if (object instanceof Boolean) {
            return new BooleanExpression((Boolean) object);
        } else if (object instanceof Map) {
            //noinspection unchecked
            return new MapExpression(convertMap((Map<Object, Object>) object));
        } else if (object instanceof List) {
            //noinspection unchecked
            return new ListExpression(convertList((List<Object>) object));
        }
        throw new IllegalArgumentException("Unable to convert object " + object.getClass().getName() + ": " + object.toString());
    }

    public static Map<Expression, Expression> convertMap(Map<Object, Object> map) {
        Map<Expression, Expression> result = new HashMap<>();
        map.forEach((key, value) -> result.put(convert(key), convert(value)));
        return result;
    }

    public static List<Expression> convertList(List<Object> list) {
        List<Expression> result = new ArrayList<>();
        list.forEach(o -> result.add(convert(o)));
        return result;
    }
}
