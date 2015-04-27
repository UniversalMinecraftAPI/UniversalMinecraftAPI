package com.koenv.jsonapi;

import com.koenv.jsonapi.parser.blocks.Block;
import com.koenv.jsonapi.parser.exceptions.MethodParser;
import com.koenv.jsonapi.parser.MethodUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSONAPITest {
    @Test
    public void testAssert() {
        assertTrue(true);
    }

    @Test
    public void testValidMethodParse() throws Exception {
        new MethodParser().parse("this.getPlayers().filterBy(\"name\")");
    }

    @Test
    public void testMethodPrint() throws Exception {
        List<Block> blocks = new MethodParser().parse("this.getPlayers().filterBy(\"name\")");
        assertEquals("this.getPlayers().filterBy(\"name\")", MethodUtil.printBlocks(blocks));
    }
}
