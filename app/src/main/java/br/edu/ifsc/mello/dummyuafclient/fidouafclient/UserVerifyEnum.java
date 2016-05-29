package br.edu.ifsc.mello.dummyuafclient.fidouafclient;

public enum UserVerifyEnum {
    USER_VERIFY_PRESENCE((short) 0x01),
    USER_VERIFY_FINGERPRINT((short) 0x02),
    USER_VERIFY_PASSCODE((short) 0x04),
    USER_VERIFY_VOICEPRINT((short) 0x08),
    USER_VERIFY_FACEPRINT((short) 0x10),
    USER_VERIFY_LOCATION((short) 0x20),
    USER_VERIFY_EYEPRINT((short) 0x40),
    USER_VERIFY_PATTERN((short) 0x80),
    USER_VERIFY_HANDPRINT((short) 0x100),
    USER_VERIFY_NONE((short) 0x200),
    USER_VERIFY_ALL((short) 0x400);

    private final short VALUE;

    UserVerifyEnum(short value) {
        this.VALUE = value;
    }

    public static UserVerifyEnum getByValue(final short value) {
        for (final UserVerifyEnum userVerifyEnum : values()) {
            if (userVerifyEnum.getValue() == value) {
                return userVerifyEnum;
            }
        }
        throw new IllegalArgumentException("Invalid USER_VERIFY value: " + Short.toString(value));
    }

    public short getValue() {
        return VALUE;
    }
}
