# Dummy FIDO UAF Client for Android

It is a dummy FIDO UAF Client suitable to conduct development tests on Android smartphones that are not FIDO Ready

- This client is heavily based on [eBay UAF open source project](https://github.com/eBay/UAF)
- This client got some inspirations (and codes) from [Android Google Samples](https://github.com/googlesamples/android-AsymmetricFingerprintDialog) and [Daon Inc. Sample FIDO UAF App](https://github.com/daoninc/fido-android-rp-app)
 
## Smartphone requirements

- Android 6 or above
- Fingerprint sensor

## How to use it?

- This client will be invoked by a FIDO  RP application for Android. You can try these:
 - [eBay Android RP Client](https://github.com/eBay/UAF)
 - [FidoAndroidRPApp](https://github.com/apowers313/FidoAndroidRPApp)
 - [Sample FIDO UAF Android App](https://github.com/daoninc/fido-android-rp-app)


### Enhancement requests

- Move from [SharedPreferences](https://developer.android.com/guide/topics/data/data-storage.html#pref) (code inherited from eBay) to [Hardware-backed Keystore](https://source.android.com/security/keystore/index.html) to store a key pair for each Relaying Party
- Create a Settings activity that allows change details about fake Authenticator, generate new attestation keys, etc.
- Create a UI to list Relaying Parties details (i.e. Name, URL, associated key material, username account)

### Known issues

- According to [FIDO Protocol](https://fidoalliance.org/specs/fido-uaf-v1.0-ps-20141208/fido-uaf-protocol-v1.0-ps-20141208.html#dictionary-finalchallengeparams-members), `channelBinding` contains TLS information to be sent by the FIDO client to the FIDO Server, however Dummy UAF Client is not doing that.
- `saveAAIDandKeyID` method is not working properly