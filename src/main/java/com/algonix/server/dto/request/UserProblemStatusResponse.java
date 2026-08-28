package com.algonix.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserProblemStatusResponse {
    private UUID problemId;
    private String status;

}
