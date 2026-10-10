package com.algonix.server.repository.projection;


import java.util.UUID;

public interface TagProjection {
    UUID getProblemId();
    String getTag();
}

