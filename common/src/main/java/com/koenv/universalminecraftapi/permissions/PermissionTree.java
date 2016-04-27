package com.koenv.universalminecraftapi.permissions;

import com.koenv.universalminecraftapi.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PermissionTree {
    public static final Pattern PATH_REGEX = Pattern.compile("\\.");

    private final PermissionNode root;

    private PermissionTree(int value) {
        this.root = new PermissionNode();
        this.root.value = value;
    }

    public int get(String path) {
        String[] parts = PATH_REGEX.split(path);
        return get(parts);
    }

    public int get(String[] parts) {
        PermissionNode node = this.root;
        int value = node.value;
        for (String part : parts) {
            if (!node.children.containsKey(part)) {
                break;
            }
            node = node.children.get(part);
            value += node.value;
        }
        return value;
    }

    public static PermissionTree of(List<Pair<String, Integer>> sections, int defaultValue) {
        PermissionTree tree = new PermissionTree(defaultValue);
        sections.forEach(section -> {
            String[] parts = PATH_REGEX.split(section.getLeft());
            PermissionNode current = tree.root;
            for (String part : parts) {
                if (current.children.containsKey(part)) {
                    current = current.children.get(part);
                } else {
                    PermissionNode newNode = new PermissionNode();
                    current.children.put(part, newNode);
                    current = newNode;
                }
            }
            current.value += section.getRight();
        });
        return tree;
    }

    private static class PermissionNode {
        private final Map<String, PermissionNode> children = new HashMap<>();
        private int value = 0;
    }
}
