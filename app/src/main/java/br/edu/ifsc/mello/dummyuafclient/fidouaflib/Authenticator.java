package br.edu.ifsc.mello.dummyuafclient.fidouaflib;


import org.ebayopensource.fidouaf.marvin.client.msg.DisplayPNGCharacteristicsDescriptor;
import org.ebayopensource.fidouaf.marvin.client.msg.Version;

public class Authenticator {
    public String title;
    public String aaid;
    public String description;
    public Version[] supportedUAFVersions;
    public String assertionScheme;
    public short authenticationAlgorithm;
    public short[] attestationTypes;
    public long userVerification;
    public short keyProtection;
    public short matcherProtection;
    public long attachmentHint;
    public boolean isSecondFactorOnly;
    public short tcDisplay;
    public String tcDisplayContentType;
    public DisplayPNGCharacteristicsDescriptor[] tcDisplayPNGCharacteristics;
    public String icon;
    public String[] supportedExtensionIDs;


    public Authenticator() {
    }

    public Authenticator(String title, String aaid, String description, Version[] supportedUAFVersions,
                         String assertionScheme, short authenticationAlgorithm, short[] attestationTypes,
                         long userVerification, short keyProtection, short matcherProtection,
                         long attachmentHint, boolean isSecondFactorOnly, short tcDisplay,
                         String tcDisplayContentType, DisplayPNGCharacteristicsDescriptor[]
                                 tcDisplayPNGCharacteristics, String icon, String[] supportedExtensionIDs) {
        this.title = title;
        this.aaid = aaid;
        this.description = description;
        this.supportedUAFVersions = supportedUAFVersions;
        this.assertionScheme = assertionScheme;
        this.authenticationAlgorithm = authenticationAlgorithm;
        this.attestationTypes = attestationTypes;
        this.userVerification = userVerification;
        this.keyProtection = keyProtection;
        this.matcherProtection = matcherProtection;
        this.attachmentHint = attachmentHint;
        this.isSecondFactorOnly = isSecondFactorOnly;
        this.tcDisplay = tcDisplay;
        this.tcDisplayContentType = tcDisplayContentType;
        this.tcDisplayPNGCharacteristics = tcDisplayPNGCharacteristics;
        this.icon = icon;
        this.supportedExtensionIDs = supportedExtensionIDs;
    }
}
