// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: counter.proto

package org.gw.raft.rpc;

public final class CounterOutter {
  private CounterOutter() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_raft_GetValueRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_raft_GetValueRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_raft_IncrementAndGetRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_raft_IncrementAndGetRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_raft_ValueResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_raft_ValueResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rcounter.proto\022\004raft\"\'\n\017GetValueRequest" +
      "\022\024\n\014readOnlySafe\030\001 \002(\010\"\'\n\026IncrementAndGe" +
      "tRequest\022\r\n\005delta\030\001 \002(\003\"S\n\rValueResponse" +
      "\022\r\n\005value\030\001 \002(\003\022\017\n\007success\030\002 \002(\010\022\020\n\010redi" +
      "rect\030\003 \001(\t\022\020\n\010errorMsg\030\004 \001(\tB\"\n\017org.gw.r" +
      "aft.rpcB\rCounterOutterP\001"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_raft_GetValueRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_raft_GetValueRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_raft_GetValueRequest_descriptor,
        new java.lang.String[] { "ReadOnlySafe", });
    internal_static_raft_IncrementAndGetRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_raft_IncrementAndGetRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_raft_IncrementAndGetRequest_descriptor,
        new java.lang.String[] { "Delta", });
    internal_static_raft_ValueResponse_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_raft_ValueResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_raft_ValueResponse_descriptor,
        new java.lang.String[] { "Value", "Success", "Redirect", "ErrorMsg", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
