package com.simon.taps.server.database;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "rooms")
public class Room {

  @Id
  private String id;

  @Column(name = "pattern")
  private String pattern;

  @Column(name = "pattern_completed")
  private String patternCompleted;

  @Column(name = "round")
  private Long round;

  @Column(name = "state")
  private String state;

  @Column(name = "timer", columnDefinition = "TIMESTAMP")
  private LocalDateTime timer;

  @Version
  private Long version;

  public String getId() {
    return this.id;
  }

  public String getPattern() {
    return this.pattern;
  }

  public String getPatternCompleted() {
    return this.patternCompleted;
  }

  public Long getRound() {
    return this.round;
  }

  public String getState() {
    return this.state;
  }

  public LocalDateTime getTimer() {
    return this.timer;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setPattern(final String pattern) {
    this.pattern = pattern;
  }

  public void setPatternCompleted(final String patternCompleted) {
    this.patternCompleted = patternCompleted;
  }

  public void setRound(final Long round) {
    this.round = round;
  }

  public void setState(final String state) {
    this.state = state;
  }

  public void setTimer(final LocalDateTime timer) {
    this.timer = timer;
  }

}
