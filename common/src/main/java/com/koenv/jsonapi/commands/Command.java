package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.JSONAPI;

public abstract class Command {
    public abstract boolean onCommand(JSONAPI jsonapi, CommandSource commandSource, String[] args);
}
