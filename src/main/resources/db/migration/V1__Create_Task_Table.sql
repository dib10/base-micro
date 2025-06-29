-- V1__Create_Task_Table.sql

-- Cria a tabela principal para as tarefas
CREATE TABLE TASKS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(250),
    due_date TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL,
    owner_user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL
);

-- Cria a tabela para guardar a lista de IDs de utilizadores responsáveis
CREATE TABLE TASK_RESPONSIBLE_USERS (
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (task_id) REFERENCES TASKS(id)
);