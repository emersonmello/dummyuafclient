package br.edu.ifsc.mello.dummyuafclient.fidouafclient;


public enum AttachmentHintEnum {
    ATTACHMENT_HINT_INTERNAL((short) 0x01),
    ATTACHMENT_HINT_EXTERNAL((short) 0x02),
    ATTACHMENT_HINT_WIRED((short) 0x04),
    ATTACHMENT_HINT_WIRELESS((short) 0x08),
    ATTACHMENT_HINT_NFC((short) 0x10),
    ATTACHMENT_HINT_BLUETOOTH((short) 0x20),
    ATTACHMENT_HINT_NETWORK((short) 0x40),
    ATTACHMENT_HINT_READY((short) 0x80),
    ATTACHMENT_HINT_WIFI_DIRECT((short) 0x100);

    private final short VALUE;


    AttachmentHintEnum(short value) {
        this.VALUE = value;
    }

    public static AttachmentHintEnum getByValue(final short value) {
        for (final AttachmentHintEnum attachmentHintEnum : values()) {
            if (attachmentHintEnum.getValue() == value) {
                return attachmentHintEnum;
            }
        }
        throw new IllegalArgumentException("Invalid ATTACHMENT_HINT value: " + Short.toString(value));
    }

    public short getValue() {
        return VALUE;
    }


}
