package com.simon.taps.server.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "players")
public class Player {

  @Id
  private String id;

  @Column(name = "ready")
  private Boolean ready;

  @Column(name = "room_id")
  private String roomId;

  @Column(name = "tile_id")
  private String tileId;

  public String getId() {
    return this.id;
  }

  public Boolean getReady() {
    return this.ready;
  }

  public String getRoomId() {
    return this.roomId;
  }

  public String getTileId() {
    return this.tileId;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setReady(final Boolean ready) {
    this.ready = ready;
  }

  public void setRoomId(final String roomId) {
    this.roomId = roomId;
  }

  public void setTileId(final String tileId) {
    this.tileId = tileId;
  }

}
