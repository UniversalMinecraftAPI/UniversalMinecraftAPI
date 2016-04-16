package com.koenv.jsonapi.users.voters;

import com.koenv.jsonapi.users.model.PermissionType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VoterUtils {
    public static VoterResponse canAccess(PermissionType type, List<String> types, String check) {
        switch (type) {
            case BLACKLIST:
                if (types.contains(check)) {
                    return VoterResponse.DENIED;
                } else {
                    return VoterResponse.ALLOWED;
                }
            case WHITELIST:
                if (types.contains(check)) {
                    return VoterResponse.ALLOWED;
                } else {
                    return VoterResponse.DENIED;
                }
            default:
                return VoterResponse.NEUTRAL;
        }
    }

    public static boolean isUnanimous(Stream<VoterResponse> responses) {
        List<VoterResponse> matchedResponses = responses
                .filter(voterResponse -> voterResponse != VoterResponse.NEUTRAL)
                .collect(Collectors.toList());

        if (matchedResponses.size() == 0) {
            return false; // allMatch returns true if the list is empty, which is something we don't want
        }

        if (matchedResponses.contains(VoterResponse.OVERRIDE_ALLOWED)) {
            return true;
        }

        if (matchedResponses.contains(VoterResponse.OVERRIDE_DENIED)) {
            return true;
        }

        return matchedResponses.stream().allMatch(voterResponse -> voterResponse == VoterResponse.ALLOWED);
    }
}
