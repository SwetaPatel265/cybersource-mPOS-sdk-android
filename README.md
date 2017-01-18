# CyberSource Mobile Point of Sale SDK for Android

The CyberSource Mobile Point of Sale SDK is a semi-integrated solution that enables you to add mobile point-of-sale functionality to your payment application, including card- present and EMV capabilities. The merchant’s application invokes this SDK to complete an EMV transaction. The SDK handles the EMV workflow as well as securely submitting the EMV transaction for processing. The merchant’s application never touches any EMV data at any point.

## Supported Types

Accepted card types:
1. Visa
2. MasterCard
3. American Express
4. Discover

Accepted currency:
1. USD

Supported payment types:
1. Authorization
2. Capture
3. Authorization reversal
4. Void
5. Refund

Supported transaction types:
1. Contact chip & signature
2. Magstripe read of a chip card (fallback)
3. Magstripe read of regular cards

## Point of Sale Flow

1. Insert approved card reader into the device.
2. Insert card with EMV chip into the reader.
3. Select the application, if prompted. If there is only a compatible application on the card, the application is selected automatically.
4. Confirm the amount.
5. Do not remove the card until the transaction is complete. If at any time the user cancels the transaction, the EMV transaction is cancelled.
6. If at any time the user cancels the transaction, the EMV transaction is cancelled.

## Prerequisites
Before integrating the SDK into your app, you must first obtain test and live card readers, generate client credentials, register and activate the device, and generate a session token as explained below.

1. Card Readers
2. Generating Client Credentials
3. Registering the Device
4. Activating the Device
5. Generating a Token

Please refer documentation guide "CyberSource mPOS Android SDK.pdf" available under Documentation folder for more details on each step.

The merchant’s Android application is assumed to have the following credentials from CyberSource before the SDK can be integrated.

1. Merchant ID
2. Device ID
3. Session Token

## To integrate the SDK:

1. In your Reference application, create lib folder and add mPos SDK .aar file "cybersource-mpos-android-sdk-release.aar"
2. In your application's build.gradle file, add this aar file as a dependency.

  ````
  compile (name: 'cybersource-mpos-android-sdk-release', ext:'aar')
  ````

3. Using the Manager class, initialize the SDK with either the PROD or TEST environment, device ID, terminal ID, terminal ID alternate and merchant ID (MID) value.

  ````
  Settings settings = new Settings(Settings.Environment.ENV_TEST,”deviceID”,”terminalID”,”terminalIDAlternate”,”mid”);
  Manger manager = new Manager(settings);
  ````
Method Definition:
  ````
  public Settings(Environment environment, String deviceID, String terminalID, String terminalIDAlternate, String mid)
  public Manager(Settings settings)
  ````

  Environment – enum with values ENV_TEST for CAS and ENV_LIVE for production environment.

4. Prepare the PaymentRequest object: paymentReqObject = new PaymentRequest();
  ````
  paymentReqObject.setMerchantId(merchantID);
  paymentReqObject.setAccessToken(oAuthToken);
  PurchaseTotal purchaseTotal = new PurchaseTotal();
  purchaseTotal.setCurrency("USD");
  purchaseTotal.setGrandTotalAmount(amount);
  paymentReqObject.setPurchaseTotal(purchaseTotal);
  paymentReqObject.setMerchantReferenceCode(Long.toString(System.currentTimeMillis()));
  ````

5. Create a manager delegate to receive callbacks after the transaction is complete.
  ````
  private final ManagerDelegate managerDelegate = new ManagerDelegate() {
    @Override
    public void performPaymentDidFinish(PaymentResponse paymentResponse,PaymentError paymentError) {
      // Perform actions
    }
  };
  ````
6. Start the payment transaction.
  ````
  manager.performPayment(paymentReqObject, this, managerDelegate);
  ````
  Method definition:
  ````
  public void performPayment(PaymentRequest paymentRequest, Context context, final ManagerDelegate managerDelegate)
  ````

## Permissions

Android 6.0 (API level 23) and later versions enable users to grant permissions to apps while the app is running. The SDK requires the permissions shown below from the merchant’s app:
1. RECORD_AUDIO
2. MODIFY_AUDIO_SETTINGS
3. WRITE_EXTERNAL_STORAGE
4. READ_EXTERNAL_STORAGE

Example Setting Permissions
````
if (ContextCompat.checkSelfPermission(BaseActivity.this,
        Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(BaseActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO,
Manifest.permission.MODIFY_AUDIO_SETTINGS},
                MY_PERMISSIONS_REQUEST_AUDIO);
}
````
