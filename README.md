# Mobtexting Missedcall Phone Number Verification
_Easy to integrate android sdk to verify phone number from Mobtexting_
## Getting Started
### Gradle
**Step 1.** _Add the JitPack repository to your build file_
```java
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
**Step 2.** Add the dependency
```java
dependencies {
  implementation 'com.github.mobtexting:missedcallverification-android:v1.0.0'
}
```
#### Define _API KEY_ in Manifest file inside Application tag
```xml
  <meta-data android:name="mobtexting.api_key" android:value="@string/mobtextingapikey" />
```
#### Usage (How to verify phone number using missed call)
**Step 3.**
implement VerificationInterface in Activity or fragment and implement the methods.
```java
public class MainActivity extends AppCompatActivity implements VerificationInterface{

    private Button btnVerify;
    private ProgressDialog pd;
    private MissedCallReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnVerify = (Button) findViewById(R.id.btnVerify);
        
        
    }
    
    @Override
    public void onResponse(ServerResponse serverResponse) {
        
    }
    
    @Override
    public void onError(ServerResponse serverResponse) {
        
    }
    
    @Override
    public void missedCallReceived(boolean b, String s) {
        
    }
 }
 ```
 **Step 4.**
 recomanded to register the receiver and initiate the call from dialer
 ```java
 btnVerify.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         //recomanded to receiver receiver
         registerReceiver();
         performDial("123"); //pass the missed call number (whom to dial)
     }
 });
 
/**
* perform dial to particular number
* @param numberString
*/
private void performDial(String numberString) {
    if (!numberString.equals("")) {
      Uri number = Uri.parse("tel:" + numberString);
      Intent dial = new Intent(Intent.ACTION_CALL, number);
      //check the permission (recomanded to check the permission)
      if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) !=                      PackageManager.PERMISSION_GRANTED) {
      return;
    }
    startActivity(dial);
  }
}
    
    /**
     * register receiver for outgoing call and phone state
     */
private void registerReceiver() {
  IntentFilter intentFilter = new IntentFilter();
  intentFilter.addAction("android.intent.action.PHONE_STATE");
  intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
  //create object of MisscallReciver and the **context** or **this** as parameter
  receiver = new MissedCallReceiver(this);
  registerReceiver(receiver, intentFilter);
}
```
 **Step 5.**
 recomanded to unregister the receiver
 ```java
 @Override
 protected void onDestroy() {
    //recomanded to unregister the receiver
    unRegisterReceiver();
    super.onDestroy();
 }
    
/**
* recomanded to unregister the receiver
*/
private void unRegisterReceiver() {
    if (receiver != null) {
      Log.d("activity", "broadcast receiver unregistered");
      MainActivity.this.unregisterReceiver(receiver);
      receiver = null;
    } else {
      Log.d("activity", "broadcast receiver not unregistered");
    }
}
 ```
 **Step 6.**
 after performing a dial, it will invoked inside missedCallReceived override method (after missedcall automatially will disconnect)
```java
//dial a number from activity and call back with dialed number
 @Override
public void missedCallReceived(boolean b, String s) {
  if (b) {
    //perform mobile numer verification verification 
    MobtextingServices.sendDataToService(this, "verfifcation_number", "missed_call_number", new MobtextingInfoResult(this));
  } else {
    unRegisterReceiver();
  }
}

// we will get server response from mobtexting
@Override
public void onResponse(ServerResponse serverResponse) {
  //do whatever you want        
}
    
@Override
public void onError(ServerResponse serverResponse) {
  //do whatever you want      
}
```
