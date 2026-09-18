package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eventfunction_decoritem_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionDecorItemImagesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "eventfunction_decoritem_image_id")
	private Long id;

	@Column(name = "image_path")
	private String imagePath;

	@Column(name = "decoritem_id")
	private Long decorItemId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "eventfunction_id")
	private Long eventFunctionId;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
}
