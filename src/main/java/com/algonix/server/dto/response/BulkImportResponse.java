package com.algonix.server.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResponse {

    private int total;

    private int created;

    private int updated;

    private int failed;

    private List<String> failedProblems;
}