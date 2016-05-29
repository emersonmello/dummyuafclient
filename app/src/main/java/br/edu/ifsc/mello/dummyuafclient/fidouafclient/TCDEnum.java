package br.edu.ifsc.mello.dummyuafclient.fidouafclient;


public enum TCDEnum {
    TRANSACTION_CONFIRMATION_DISPLAY_ANY((short) 0x01),
    TRANSACTION_CONFIRMATION_DISPLAY_PRIVILEGED_SOFTWARE((short) 0x02),
    TRANSACTION_CONFIRMATION_DISPLAY_TEE((short) 0x04),
    TRANSACTION_CONFIRMATION_DISPLAY_HARDWARE((short) 0x08),
    TRANSACTION_CONFIRMATION_DISPLAY_REMOTE((short) 0x10);

    private final short VALUE;


    TCDEnum(short value) {
        this.VALUE = value;
    }

    public static TCDEnum getByValue(final short value) {
        for (final TCDEnum tcdEnum : values()) {
            if (tcdEnum.getValue() == value) {
                return tcdEnum;
            }
        }
        throw new IllegalArgumentException("Invalid TRANSACTION_CONFIRMATION_DISPLAY value: " + Short.toString(value));
    }

    public short getValue() {
        return VALUE;
    }

}
