package com.kbseed.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_disciplines")
public class MembershipDisciplineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_membership_id", nullable = false)
    private Long clientMembershipId;

    @Column(name = "class_type_id", nullable = false)
    private Long classTypeId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClientMembershipId() { return clientMembershipId; }
    public void setClientMembershipId(Long clientMembershipId) { this.clientMembershipId = clientMembershipId; }
    public Long getClassTypeId() { return classTypeId; }
    public void setClassTypeId(Long classTypeId) { this.classTypeId = classTypeId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
