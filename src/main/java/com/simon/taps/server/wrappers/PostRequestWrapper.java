package com.simon.taps.server.wrappers;

public class PostRequestWrapper {

  private String playerId;

  private String roomId;

  public String getPlayerId() {
    return this.playerId;
  }

  public String getRoomId() {
    return this.roomId;
  }

  public void setPlayerId(final String playerId) {
    this.playerId = playerId;
  }

  public void setRoomId(final String roomId) {
    this.roomId = roomId;
  }

}
