package com.koenv.universalminecraftapi.docgenerator.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodDiffResult<T extends AbstractMethod> {
    private Platform platform1;
    private Set<T> onlyInPlatform1;

    private Platform platform2;
    private Set<T> onlyInPlatform2;

    public MethodDiffResult(Platform platform1, Set<T> onlyInPlatform1, Platform platform2, Set<T> onlyInPlatform2) {
        this.platform1 = platform1;
        this.onlyInPlatform1 = onlyInPlatform1;
        this.platform2 = platform2;
        this.onlyInPlatform2 = onlyInPlatform2;
    }

    public Platform getPlatform1() {
        return platform1;
    }

    public Set<T> getOnlyInPlatform1() {
        return onlyInPlatform1;
    }

    public Platform getPlatform2() {
        return platform2;
    }

    public Set<T> getOnlyInPlatform2() {
        return onlyInPlatform2;
    }

    public static <T extends AbstractMethod> MethodDiffResult<T> diff(Platform platform1, List<T> platform1Methods, Platform platform2, List<T> platform2Methods) {
        HashSet<T> onlyInPlatform1 = new HashSet<>(platform1Methods);
        onlyInPlatform1.removeAll(platform2Methods);

        HashSet<T> onlyInPlatform2 = new HashSet<>(platform2Methods);
        onlyInPlatform2.removeAll(platform1Methods);

        return new MethodDiffResult<>(platform1, onlyInPlatform1, platform2, onlyInPlatform2);
    }
}
