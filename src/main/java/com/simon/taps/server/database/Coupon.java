package com.simon.taps.server.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coupons")
public class Coupon {

  @Column(name = "active")
  private Boolean active;

  @Id
  private String id;

  @Column(name = "issued")
  private Boolean issued;

  public Boolean getActive() {
    return this.active;
  }

  public String getId() {
    return this.id;
  }

  public Boolean getIssued() {
    return this.issued;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setIssued(final Boolean issued) {
    this.issued = issued;
  }

}
