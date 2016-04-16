package com.koenv.jsonapi.users.model;

import com.koenv.jsonapi.users.voters.VoterResponse;

import java.util.Collections;
import java.util.stream.Stream;

public class AllowingPermission extends Permission {
    public AllowingPermission() {
        super("ALLOW_ALL", Collections.emptyList(), Collections.emptyList(), StreamPermissions.builder().build());
    }

    @Override
    public Stream<VoterResponse> canAccessNamespaceMethod(String namespace, String method) {
        return Collections.singletonList(VoterResponse.ALLOWED).stream();
    }

    @Override
    public Stream<VoterResponse> canAccessClassMethod(String clazz, String method) {
        return Collections.singletonList(VoterResponse.ALLOWED).stream();
    }

    @Override
    public VoterResponse canAccessStream(String stream) {
        return VoterResponse.ALLOWED;
    }
}
