// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: counter.proto

package org.gw.raft.rpc;

public interface IncrementAndGetRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:raft.IncrementAndGetRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required int64 delta = 1;</code>
   * @return Whether the delta field is set.
   */
  boolean hasDelta();
  /**
   * <code>required int64 delta = 1;</code>
   * @return The delta.
   */
  long getDelta();
}
