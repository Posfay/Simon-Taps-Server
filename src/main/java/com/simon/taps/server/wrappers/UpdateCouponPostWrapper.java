package com.simon.taps.server.wrappers;

public class UpdateCouponPostWrapper {

  private Boolean active;

  private String id;

  public Boolean getActive() {
    return this.active;
  }

  public String getId() {
    return this.id;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public void setId(final String id) {
    this.id = id;
  }

}
