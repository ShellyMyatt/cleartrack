package com.cleartrack.service;

import com.cleartrack.dto.CreateProjectRequest;
import com.cleartrack.dto.ProjectResponse;
import com.cleartrack.entity.Project;
import com.cleartrack.entity.ProjectStatus;
import com.cleartrack.entity.User;
import com.cleartrack.exception.ResourceNotFoundException;
import com.cleartrack.repository.ProjectRepository;
import com.cleartrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(CreateProjectRequest request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedBy(creator);

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus().name(),
                project.getCreatedBy() != null ? project.getCreatedBy().getUsername() : null,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
