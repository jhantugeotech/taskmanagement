package io.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status=TaskStatus.TODO;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_parent",
            joinColumns = @JoinColumn(name = "child_task_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_task_id")
    )
    private Set<Task> parentTask=new HashSet<>();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;


    @PrePersist
    private void preCreate(){
        this.createdAt=LocalDateTime.now();
        this.updatedAt=this.createdAt;
    }

    @PreUpdate
    private void preUpdate(){
        this.updatedAt=LocalDateTime.now();
    }


}
