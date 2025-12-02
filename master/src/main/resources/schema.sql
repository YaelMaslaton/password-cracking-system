-- PostgreSQL DDL for Password Cracking System

-- Create batches table
CREATE TABLE IF NOT EXISTS batches (
    batch_id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_tasks INTEGER NOT NULL,
    completed_tasks INTEGER NOT NULL DEFAULT 0
);

-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    task_id UUID PRIMARY KEY,
    batch_id UUID NOT NULL,
    hash_value VARCHAR(32) NOT NULL,
    status VARCHAR(50) NOT NULL,
    found_password VARCHAR(255),
    total_sub_tasks INTEGER NOT NULL,
    completed_sub_tasks INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id)
);

-- Create sub_tasks table
CREATE TABLE IF NOT EXISTS sub_tasks (
    sub_task_id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    range_start BIGINT NOT NULL,
    range_end BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    result_password VARCHAR(255),
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_tasks_batch_id ON tasks(batch_id);
CREATE INDEX IF NOT EXISTS idx_sub_tasks_task_id ON sub_tasks(task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_sub_tasks_status ON sub_tasks(status);