package com.simon.taps.server.database;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coupons")
public class Coupon {

  @Column(name = "active")
  private Boolean active;

  @Column(name = "expire_at")
  private LocalDateTime expireAt;

  @Id
  private String id;

  @Column(name = "issued")
  private Boolean issued;

  @Column(name = "user_id")
  private String userId;

  public Boolean getActive() {
    return this.active;
  }

  public LocalDateTime getExpireAt() {
    return this.expireAt;
  }

  public String getId() {
    return this.id;
  }

  public Boolean getIssued() {
    return this.issued;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public void setExpireAt(final LocalDateTime expireAt) {
    this.expireAt = expireAt;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setIssued(final Boolean issued) {
    this.issued = issued;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }

}
