[![Build Status](https://travis-ci.org/emersonmello/dummyuafclient.svg?branch=master)](https://travis-ci.org/emersonmello/dummyuafclient)

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

- Create a Settings activity that allows change details about fake Authenticator, generate new attestation keys, etc.


### Known issues

- Static attestation certificate is not valid (it is expired) and attestation private key is not stored securely, so authentication operation could result in "SIGNATURE IS NOT VALID".  