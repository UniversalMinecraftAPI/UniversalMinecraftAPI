package com.koenv.jsonapi.commands;

import com.koenv.jsonapi.JSONAPIInterface;

public abstract class Command {
    public abstract boolean onCommand(JSONAPIInterface jsonapi, CommandSource commandSource, String[] args);
}
