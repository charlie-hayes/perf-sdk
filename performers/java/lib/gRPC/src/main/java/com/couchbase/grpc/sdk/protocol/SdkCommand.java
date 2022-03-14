// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: sdk_commands.proto

package com.couchbase.grpc.sdk.protocol;

/**
 * Protobuf type {@code protocol.SdkCommand}
 */
public final class SdkCommand extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:protocol.SdkCommand)
    SdkCommandOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SdkCommand.newBuilder() to construct.
  private SdkCommand(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SdkCommand() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new SdkCommand();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private SdkCommand(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            CommandInsert.Builder subBuilder = null;
            if (commandCase_ == 1) {
              subBuilder = ((CommandInsert) command_).toBuilder();
            }
            command_ =
                input.readMessage(CommandInsert.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((CommandInsert) command_);
              command_ = subBuilder.buildPartial();
            }
            commandCase_ = 1;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return SdkCommands.internal_static_protocol_SdkCommand_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return SdkCommands.internal_static_protocol_SdkCommand_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            SdkCommand.class, SdkCommand.Builder.class);
  }

  private int commandCase_ = 0;
  private java.lang.Object command_;
  public enum CommandCase
      implements com.google.protobuf.Internal.EnumLite,
          com.google.protobuf.AbstractMessage.InternalOneOfEnum {
    INSERT(1),
    COMMAND_NOT_SET(0);
    private final int value;
    private CommandCase(int value) {
      this.value = value;
    }
    /**
     * @param value The number of the enum to look for.
     * @return The enum associated with the given number.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static CommandCase valueOf(int value) {
      return forNumber(value);
    }

    public static CommandCase forNumber(int value) {
      switch (value) {
        case 1: return INSERT;
        case 0: return COMMAND_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public CommandCase
  getCommandCase() {
    return CommandCase.forNumber(
        commandCase_);
  }

  public static final int INSERT_FIELD_NUMBER = 1;
  /**
   * <code>.protocol.CommandInsert insert = 1;</code>
   * @return Whether the insert field is set.
   */
  @java.lang.Override
  public boolean hasInsert() {
    return commandCase_ == 1;
  }
  /**
   * <code>.protocol.CommandInsert insert = 1;</code>
   * @return The insert.
   */
  @java.lang.Override
  public CommandInsert getInsert() {
    if (commandCase_ == 1) {
       return (CommandInsert) command_;
    }
    return CommandInsert.getDefaultInstance();
  }
  /**
   * <code>.protocol.CommandInsert insert = 1;</code>
   */
  @java.lang.Override
  public CommandInsertOrBuilder getInsertOrBuilder() {
    if (commandCase_ == 1) {
       return (CommandInsert) command_;
    }
    return CommandInsert.getDefaultInstance();
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (commandCase_ == 1) {
      output.writeMessage(1, (CommandInsert) command_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (commandCase_ == 1) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, (CommandInsert) command_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof SdkCommand)) {
      return super.equals(obj);
    }
    SdkCommand other = (SdkCommand) obj;

    if (!getCommandCase().equals(other.getCommandCase())) return false;
    switch (commandCase_) {
      case 1:
        if (!getInsert()
            .equals(other.getInsert())) return false;
        break;
      case 0:
      default:
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    switch (commandCase_) {
      case 1:
        hash = (37 * hash) + INSERT_FIELD_NUMBER;
        hash = (53 * hash) + getInsert().hashCode();
        break;
      case 0:
      default:
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static SdkCommand parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static SdkCommand parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static SdkCommand parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static SdkCommand parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static SdkCommand parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static SdkCommand parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static SdkCommand parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static SdkCommand parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static SdkCommand parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static SdkCommand parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static SdkCommand parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static SdkCommand parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(SdkCommand prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code protocol.SdkCommand}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:protocol.SdkCommand)
          SdkCommandOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return SdkCommands.internal_static_protocol_SdkCommand_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return SdkCommands.internal_static_protocol_SdkCommand_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              SdkCommand.class, SdkCommand.Builder.class);
    }

    // Construct using com.couchbase.grpc.sdk.protocol.SdkCommand.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      commandCase_ = 0;
      command_ = null;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return SdkCommands.internal_static_protocol_SdkCommand_descriptor;
    }

    @java.lang.Override
    public SdkCommand getDefaultInstanceForType() {
      return SdkCommand.getDefaultInstance();
    }

    @java.lang.Override
    public SdkCommand build() {
      SdkCommand result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public SdkCommand buildPartial() {
      SdkCommand result = new SdkCommand(this);
      if (commandCase_ == 1) {
        if (insertBuilder_ == null) {
          result.command_ = command_;
        } else {
          result.command_ = insertBuilder_.build();
        }
      }
      result.commandCase_ = commandCase_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof SdkCommand) {
        return mergeFrom((SdkCommand)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(SdkCommand other) {
      if (other == SdkCommand.getDefaultInstance()) return this;
      switch (other.getCommandCase()) {
        case INSERT: {
          mergeInsert(other.getInsert());
          break;
        }
        case COMMAND_NOT_SET: {
          break;
        }
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      SdkCommand parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (SdkCommand) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int commandCase_ = 0;
    private java.lang.Object command_;
    public CommandCase
        getCommandCase() {
      return CommandCase.forNumber(
          commandCase_);
    }

    public Builder clearCommand() {
      commandCase_ = 0;
      command_ = null;
      onChanged();
      return this;
    }


    private com.google.protobuf.SingleFieldBuilderV3<
            CommandInsert, CommandInsert.Builder, CommandInsertOrBuilder> insertBuilder_;
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     * @return Whether the insert field is set.
     */
    @java.lang.Override
    public boolean hasInsert() {
      return commandCase_ == 1;
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     * @return The insert.
     */
    @java.lang.Override
    public CommandInsert getInsert() {
      if (insertBuilder_ == null) {
        if (commandCase_ == 1) {
          return (CommandInsert) command_;
        }
        return CommandInsert.getDefaultInstance();
      } else {
        if (commandCase_ == 1) {
          return insertBuilder_.getMessage();
        }
        return CommandInsert.getDefaultInstance();
      }
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     */
    public Builder setInsert(CommandInsert value) {
      if (insertBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        command_ = value;
        onChanged();
      } else {
        insertBuilder_.setMessage(value);
      }
      commandCase_ = 1;
      return this;
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     */
    public Builder setInsert(
        CommandInsert.Builder builderForValue) {
      if (insertBuilder_ == null) {
        command_ = builderForValue.build();
        onChanged();
      } else {
        insertBuilder_.setMessage(builderForValue.build());
      }
      commandCase_ = 1;
      return this;
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     */
    public Builder mergeInsert(CommandInsert value) {
      if (insertBuilder_ == null) {
        if (commandCase_ == 1 &&
            command_ != CommandInsert.getDefaultInstance()) {
          command_ = CommandInsert.newBuilder((CommandInsert) command_)
              .mergeFrom(value).buildPartial();
        } else {
          command_ = value;
        }
        onChanged();
      } else {
        if (commandCase_ == 1) {
          insertBuilder_.mergeFrom(value);
        }
        insertBuilder_.setMessage(value);
      }
      commandCase_ = 1;
      return this;
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     */
    public Builder clearInsert() {
      if (insertBuilder_ == null) {
        if (commandCase_ == 1) {
          commandCase_ = 0;
          command_ = null;
          onChanged();
        }
      } else {
        if (commandCase_ == 1) {
          commandCase_ = 0;
          command_ = null;
        }
        insertBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     */
    public CommandInsert.Builder getInsertBuilder() {
      return getInsertFieldBuilder().getBuilder();
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     */
    @java.lang.Override
    public CommandInsertOrBuilder getInsertOrBuilder() {
      if ((commandCase_ == 1) && (insertBuilder_ != null)) {
        return insertBuilder_.getMessageOrBuilder();
      } else {
        if (commandCase_ == 1) {
          return (CommandInsert) command_;
        }
        return CommandInsert.getDefaultInstance();
      }
    }
    /**
     * <code>.protocol.CommandInsert insert = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            CommandInsert, CommandInsert.Builder, CommandInsertOrBuilder>
        getInsertFieldBuilder() {
      if (insertBuilder_ == null) {
        if (!(commandCase_ == 1)) {
          command_ = CommandInsert.getDefaultInstance();
        }
        insertBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                CommandInsert, CommandInsert.Builder, CommandInsertOrBuilder>(
                (CommandInsert) command_,
                getParentForChildren(),
                isClean());
        command_ = null;
      }
      commandCase_ = 1;
      onChanged();;
      return insertBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:protocol.SdkCommand)
  }

  // @@protoc_insertion_point(class_scope:protocol.SdkCommand)
  private static final SdkCommand DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new SdkCommand();
  }

  public static SdkCommand getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SdkCommand>
      PARSER = new com.google.protobuf.AbstractParser<SdkCommand>() {
    @java.lang.Override
    public SdkCommand parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new SdkCommand(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SdkCommand> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SdkCommand> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public SdkCommand getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

