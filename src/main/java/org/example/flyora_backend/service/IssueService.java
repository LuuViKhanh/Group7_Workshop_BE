package org.example.flyora_backend.service;

import java.util.Map;
import org.example.flyora_backend.DTOs.IssueReportDTO;

public interface IssueService {
    Map<String, Object> submitIssue(IssueReportDTO dto);
}
