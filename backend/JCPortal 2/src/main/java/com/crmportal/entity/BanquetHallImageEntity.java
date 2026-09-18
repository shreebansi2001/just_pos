package com.crmportal.entity;

import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "banquet_hall_image")
public class BanquetHallImageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hall_id")
	private BanquetHallMasterEntity hall;

	@Column(name = "image_path")
	private String imagePath;

	@Column(name = "is_delete")
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
	private LocalDateTime createdAt;
}