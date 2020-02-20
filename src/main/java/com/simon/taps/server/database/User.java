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

  @Column(name = "created_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;

  @Id
  private String id;

  @Column(name = "last_won", columnDefinition = "TIMESTAMP")
  private LocalDateTime lastWon;

  @Column(name = "played")
  private Long played;

  @Column(name = "won")
  private Long won;

  @Column(name = "won_today")
  private Long wonToday;

  public Boolean getAdmin() {
    return this.admin;
  }

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public String getId() {
    return this.id;
  }

  public LocalDateTime getLastWon() {
    return this.lastWon;
  }

  public Long getPlayed() {
    return this.played;
  }

  public Long getWon() {
    return this.won;
  }

  public Long getWonToday() {
    return this.wonToday;
  }

  public void setAdmin(final Boolean admin) {
    this.admin = admin;
  }

  public void setCreatedAt(final LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setLastWon(final LocalDateTime lastWon) {
    this.lastWon = lastWon;
  }

  public void setPlayed(final Long played) {
    this.played = played;
  }

  public void setWon(final Long won) {
    this.won = won;
  }

  public void setWonToday(final Long wonToday) {
    this.wonToday = wonToday;
  }

}
