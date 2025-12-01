package org.example.flyora_backend.service;

import java.util.Map;

import org.example.flyora_backend.DTOs.IssueReportDTO;
import org.example.flyora_backend.dynamo.models.IssueReportDynamoDB;
import org.example.flyora_backend.repository.IssueReportRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueReportRepository issueRepository;

    @Override
    public Map<String, Object> submitIssue(IssueReportDTO dto) {
        IssueReportDynamoDB issue = new IssueReportDynamoDB();
        issue.setId(issueRepository.generateNewId()); // Generate ID

        // SỬA LỖI: Set ID thay vì set Object
        issue.setCustomerId(dto.getCustomerId());
        issue.setOrderId(dto.getOrderId());
        
        issue.setContent(dto.getContent());

        issueRepository.save(issue);

        return Map.of("message", "Gửi báo lỗi thành công");
    }
}