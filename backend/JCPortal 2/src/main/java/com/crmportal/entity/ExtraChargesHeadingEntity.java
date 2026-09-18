package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "extra_charges_heading")
public class ExtraChargesHeadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "heading_name", nullable = false)
    private String headingName;
    
	@Column(name = "heading_name_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String headingNameHindi;

	@Column(name = "heading_name_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String headingNameGujarati;
    
    @Column(name = "sub_heading_name")
    private String subHeadingName;
    
    @Column(name = "sub_heading_name_hindi", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String subHeadingNameHindi;
    
    @Column(name = "sub_heading_name_gujarati", columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String subHeadingNameGujarati;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private EventMasterEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_function_id", referencedColumnName = "event_function_id")
    private EventFunctionMasterEntity eventFunction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserMasterEntity user;

    @OneToMany(mappedBy = "heading", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtraChargesRowEntity> rows;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "created_at", columnDefinition = "datetime")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "datetime")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHeadingName() { return headingName; }
    public void setHeadingName(String headingName) { this.headingName = headingName; }

    public EventMasterEntity getEvent() { return event; }
    public void setEvent(EventMasterEntity event) { this.event = event; }

    public EventFunctionMasterEntity getEventFunction() { return eventFunction; }
    public void setEventFunction(EventFunctionMasterEntity eventFunction) { this.eventFunction = eventFunction; }

    public UserMasterEntity getUser() { return user; }
    public void setUser(UserMasterEntity user) { this.user = user; }

    public List<ExtraChargesRowEntity> getRows() { return rows; }
    public void setRows(List<ExtraChargesRowEntity> rows) { this.rows = rows; }

    public Boolean getIsDelete() { return isDelete; }
    public void setIsDelete(Boolean isDelete) { this.isDelete = isDelete; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	public String getSubHeadingName() {
		return subHeadingName;
	}

	public void setSubHeadingName(String subHeadingName) {
		this.subHeadingName = subHeadingName;
	}

	public String getHeadingNameHindi() {
		return headingNameHindi;
	}

	public void setHeadingNameHindi(String headingNameHindi) {
		this.headingNameHindi = headingNameHindi;
	}

	public String getHeadingNameGujarati() {
		return headingNameGujarati;
	}

	public void setHeadingNameGujarati(String headingNameGujarati) {
		this.headingNameGujarati = headingNameGujarati;
	}

	public String getSubHeadingNameHindi() {
		return subHeadingNameHindi;
	}

	public void setSubHeadingNameHindi(String subHeadingNameHindi) {
		this.subHeadingNameHindi = subHeadingNameHindi;
	}

	public String getSubHeadingNameGujarati() {
		return subHeadingNameGujarati;
	}

	public void setSubHeadingNameGujarati(String subHeadingNameGujarati) {
		this.subHeadingNameGujarati = subHeadingNameGujarati;
	}
    
    
}