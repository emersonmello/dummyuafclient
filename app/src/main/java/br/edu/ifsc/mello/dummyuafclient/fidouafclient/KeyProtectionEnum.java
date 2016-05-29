package br.edu.ifsc.mello.dummyuafclient.fidouafclient;


public enum KeyProtectionEnum {
    KEY_PROTECTION_SOFTWARE((short) 0x01),
    KEY_PROTECTION_HARDWARE((short) 0x02),
    KEY_PROTECTION_TEE((short) 0x04),
    KEY_PROTECTION_SECURE_ELEMENT((short) 0x08),
    KEY_PROTECTION_REMOTE_HANDLE((short) 0x10);

    private final short VALUE;

    KeyProtectionEnum(short value) {
        this.VALUE = value;
    }

    public static KeyProtectionEnum getByValue(final short value) {
        for (final KeyProtectionEnum keyProtectionEnum : values()) {
            if (keyProtectionEnum.getValue() == value) {
                return keyProtectionEnum;
            }
        }
        throw new IllegalArgumentException("Invalid KEY_PROTECTION value: " + Short.toString(value));
    }

    public short getValue() {
        return VALUE;
    }


}
