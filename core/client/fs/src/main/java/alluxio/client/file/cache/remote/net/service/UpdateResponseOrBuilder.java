// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: metedataService.proto

package alluxio.client.file.cache.remote.net.service;

public interface UpdateResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:proto.UpdateResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bool isSucceed = 1;</code>
   */
  boolean getIsSucceed();

  /**
   * <code>repeated .proto.fileInfo failed_data = 2;</code>
   */
  java.util.List<fileInfo>
      getFailedDataList();
  /**
   * <code>repeated .proto.fileInfo failed_data = 2;</code>
   */
  fileInfo getFailedData(int index);
  /**
   * <code>repeated .proto.fileInfo failed_data = 2;</code>
   */
  int getFailedDataCount();
  /**
   * <code>repeated .proto.fileInfo failed_data = 2;</code>
   */
  java.util.List<? extends fileInfoOrBuilder>
      getFailedDataOrBuilderList();
  /**
   * <code>repeated .proto.fileInfo failed_data = 2;</code>
   */
  fileInfoOrBuilder getFailedDataOrBuilder(int index);
}
