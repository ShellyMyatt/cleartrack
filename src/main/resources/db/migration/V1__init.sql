CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE projects (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          status VARCHAR(50) NOT NULL,
                          created_by BIGINT,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_projects_created_by
                              FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE project_members (
                                 project_id BIGINT NOT NULL,
                                 user_id BIGINT NOT NULL,
                                 member_role VARCHAR(50) NOT NULL,
                                 PRIMARY KEY (project_id, user_id),
                                 CONSTRAINT fk_project_members_project
                                     FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_project_members_user
                                     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       project_id BIGINT NOT NULL,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       status VARCHAR(50) NOT NULL,
                       priority VARCHAR(50) NOT NULL,
                       assigned_to BIGINT,
                       created_by BIGINT,
                       due_date TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_tasks_project
                           FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                       CONSTRAINT fk_tasks_assigned_to
                           FOREIGN KEY (assigned_to) REFERENCES users(id),
                       CONSTRAINT fk_tasks_created_by
                           FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE task_comments (
                               id BIGSERIAL PRIMARY KEY,
                               task_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               comment_text TEXT NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_task_comments_task
                                   FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                               CONSTRAINT fk_task_comments_user
                                   FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE attachments (
                             id BIGSERIAL PRIMARY KEY,
                             task_id BIGINT,
                             uploaded_by BIGINT NOT NULL,
                             file_name VARCHAR(255) NOT NULL,
                             file_path VARCHAR(500),
                             content_type VARCHAR(100),
                             file_size BIGINT,
                             uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_attachments_task
                                 FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL,
                             CONSTRAINT fk_attachments_uploaded_by
                                 FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            actor_user_id BIGINT,
                            action VARCHAR(100) NOT NULL,
                            entity_type VARCHAR(100) NOT NULL,
                            entity_id BIGINT,
                            details JSONB,
                            success BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_audit_logs_actor
                                FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_audit_logs_actor_user_id ON audit_logs(actor_user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);