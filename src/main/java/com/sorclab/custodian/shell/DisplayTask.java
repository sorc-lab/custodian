package com.sorclab.custodian.shell;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisplayTask {
    private String format;
    private String ansiColor;
    private long taskId;
    private String description;
    private String durationDescription;
}
