package com.koenv.jsonapi.parser;

import com.koenv.jsonapi.parser.exceptions.ParameterParseException;
import com.koenv.jsonapi.parser.exceptions.ParseException;
import org.junit.Test;

public class MethodParserTest {
    @Test
    public void oneMethod() throws Exception {
        new MethodParser().parse("getIt()");
    }

    @Test
    public void chainedMethods() throws Exception {
        new MethodParser().parse("getIt().getIt()");
    }

    @Test(expected = ParseException.class)
    public void invalidMethodThrowsException() throws Exception {
        new MethodParser().parse("namespace.getIt(name)as");
    }

    @Test(expected = ParameterParseException.class)
    public void invalidParameterThrowsException() throws Exception {
        new MethodParser().parse("namespace.getIt(name)");
    }

    @Test
    public void complexMethod() throws Exception {
        new MethodParser().parse("this.getPlayers().filterBy()");
    }
}
