package br.edu.ifsc.mello.dummyuafclient.fidouaflib;


public enum ErrorCode {
    NO_ERROR((short) 0x0, "NO ERROR"),
    WAIT_USER_ACTION((short) 0x1, "WAIT USER ACTION"),
    INSECURE_TRANSPORT((short) 0x2, "INSECURE TRANSPORT"),
    USER_CANCELLED((short) 0x3, "USER CANCELLED"),
    UNSUPPORTED_VERSION((short) 0x4, "UNSUPPORTED VERSION_1_0"),
    NO_SUITABLE_AUTHENTICATOR((short) 0x5, "NO SUITABLE AUTHENTICATOR"),
    PROTOCOL_ERROR((short) 0x6, "PROTOCOL ERROR"),
    UNTRUSTED_FACET_ID((short) 0x7, "UNTRUSTED FACET ID"),
    UNKNOWN((short) 0xFF, "UNKNOWN");

    private final short ID;
    private final String DESCRIPTION;

    ErrorCode(final short id, final String description) {
        this.ID = id;
        this.DESCRIPTION = description;
    }

    public short getID() {
        return this.ID;
    }

    public String getDESCRIPTION() {
        return this.DESCRIPTION;
    }

}
