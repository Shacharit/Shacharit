package org.shaharit.face2face.backend.services;

import java.util.List;

public class MatchSummary {
    public final MatchResult matchResult;
    public final List<String> sharedInterests;
    public final List<String> notSharedInterests;

    MatchSummary(MatchResult matchResult, List<String> sharedInteresets, List<String> notSharedInterests) {
        this.matchResult = matchResult;
        this.sharedInterests = sharedInteresets;
        this.notSharedInterests = notSharedInterests;
    }
}
