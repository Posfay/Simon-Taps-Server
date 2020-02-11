package com.simon.taps.server.database;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Column(name = "admin")
  private Boolean admin;

  @Id
  private String id;

  @Column(name = "last_won", columnDefinition = "TIMESTAMP")
  private LocalDateTime lastWon;

  @Column(name = "won_today")
  private Long wonToday;

  public Boolean getAdmin() {
    return this.admin;
  }

  public String getId() {
    return this.id;
  }

  public LocalDateTime getLastWon() {
    return this.lastWon;
  }

  public Long getWonToday() {
    return this.wonToday;
  }

  public void setAdmin(final Boolean admin) {
    this.admin = admin;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setLastWon(final LocalDateTime lastWon) {
    this.lastWon = lastWon;
  }

  public void setWonToday(final Long wonToday) {
    this.wonToday = wonToday;
  }

}
