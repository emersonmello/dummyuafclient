package br.edu.ifsc.mello.dummyuafclient.fidouaflib;

public enum MatcherProtectionEnum {
    MATCHER_PROTECTION_SOFTWARE((short) 0x01),
    MATCHER_PROTECTION_TEE((short) 0x02),
    MATCHER_PROTECTION_ON_CHIP((short) 0x04);

    private final short VALUE;


    MatcherProtectionEnum(short value) {
        this.VALUE = value;
    }

    public static MatcherProtectionEnum getByValue(final short value) {
        for (final MatcherProtectionEnum matcherProtectionEnum : values()) {
            if (matcherProtectionEnum.getValue() == value) {
                return matcherProtectionEnum;
            }
        }
        throw new IllegalArgumentException("Invalid MATCHER_PROTECTION value: " + Short.toString(value));
    }

    public short getValue() {
        return VALUE;
    }

}
