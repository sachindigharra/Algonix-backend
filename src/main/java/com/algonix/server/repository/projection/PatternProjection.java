package com.algonix.server.repository.projection;

import java.util.UUID;

public interface PatternProjection {
    UUID getProblemId();
    String getPattern();
}
