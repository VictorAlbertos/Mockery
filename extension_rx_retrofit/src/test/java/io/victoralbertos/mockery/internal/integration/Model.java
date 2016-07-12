package io.victoralbertos.mockery.internal.integration;

final class Model {
  private String s1;

  Model(String s1) {
    this.s1 = s1;
  }

  /**
   * Fucking jackson...
   */
  Model() {
    this.s1 = null;
  }

  String getS1() {
    return s1;
  }

  public void setS1(String s1) {
    this.s1 = s1;
  }
}
