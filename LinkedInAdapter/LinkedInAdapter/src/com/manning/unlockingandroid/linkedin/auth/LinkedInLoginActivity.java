/*
Copyright 2010 Chris King 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

	http://www.apache.org/licenses/LICENSE-2.0 
	
Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/
package com.manning.unlockingandroid.linkedin.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.manning.unlockingandroid.linkedin.LinkedIn;
import com.manning.unlockingandroid.linkedin.R;

public class LinkedInLoginActivity extends AccountAuthenticatorActivity {

   public static final String PARAM_CREDENTIALS = "confirmCredentials";
   public static final String PARAM_PASSWORD = "password";
   public static final String PARAM_USERNAME = "username";
   public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

   private static final String TAG = "LinkedInLoginActivity";

   private AccountManager accountManager;
   private Thread authentication;
   private String userToken;
   private String userTokenSecret;

   private Handler handler;

   private Boolean confirmCredentials = false;
   protected boolean createAccount = false;

   private TextView status;
   private String accountName;
   private EditText accountNameField;
   private String enteredPIN;
   private EditText pinField;

   LinkedInOAuthService oauthService;
   private String authToken;
   private String authTokenSecret;

   @Override
   public void onCreate(Bundle icicle) {
      super.onCreate(icicle);
      oauthService = LinkedInOAuthServiceFactory.getInstance()
         .createLinkedInOAuthService(LinkedIn.API_KEY,
         LinkedIn.SECRET_KEY);
      accountManager = AccountManager.get(this);
      handler = new Handler();
      Log.i(TAG, "loading data from Intent");
      final Intent intent = getIntent();
      accountName = intent.getStringExtra(PARAM_USERNAME);
      createAccount = (accountName == null);
      confirmCredentials = intent.getBooleanExtra(PARAM_CREDENTIALS, 
         false);

      setContentView(R.layout.main);

      status = (TextView) findViewById(R.id.message);
      pinField = (EditText) findViewById(R.id.pin_edit);
      accountNameField = (EditText) findViewById(R.id.email_edit);

      accountNameField.setText(accountName);
      status.setText(R.string.login_activity_instructions);
   }

   @Override
   protected Dialog onCreateDialog(int id) {
      final ProgressDialog dialog = new ProgressDialog(this);
      dialog.setMessage(getText(R.string.working));
      dialog.setIndeterminate(true);
      dialog.setCancelable(true);
      dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
         public void onCancel(DialogInterface dialog) {
            if (authentication != null) {
               authentication.interrupt();
               finish();
            }
         }
      });
      return dialog;
   }

   public void startLogin(View view) {
      // Initiate a request to LinkedIn.
      try {
         LinkedInRequestToken requestToken = oauthService
            .getOAuthRequestToken();
         userToken = requestToken.getToken();
         userTokenSecret = requestToken.getTokenSecret();
         String authURL = requestToken.getAuthorizationUrl();
         Intent authIntent = new Intent(Intent.ACTION_VIEW, Uri
            .parse(authURL));
         startActivity(authIntent);
      } catch (Exception ioe) {
         status.setText(R.string.start_login_error);
      }
   }

   // annotate - called when login button is pressed
   public void finishLogin(View view) {
      if (createAccount) {
         accountName = accountNameField.getText().toString();
      }
      enteredPIN = pinField.getText().toString();
      if (TextUtils.isEmpty(accountName) || 
         TextUtils.isEmpty(enteredPIN)) {
         status.setText(R.string.empty_fields_error);
      } else {
         showProgress();
         authentication = authenticate(userToken, 
               userTokenSecret, enteredPIN);
      }
   }

   private Thread authenticate(final String userToken,
         final String userTokenSecret, final String pin) {
      Thread authenticator = new Thread() {
         public void run() {
            boolean success;
            try {
               LinkedInRequestToken requestToken = new 
                  LinkedInRequestToken(userToken, userTokenSecret);
               LinkedInAccessToken access = oauthService
                  .getOAuthAccessToken(requestToken, pin);
               authToken = access.getToken();
               authTokenSecret = access.getTokenSecret();
               success = true;
            } catch (Exception e) {
               success = false;
            }
            final boolean result = success;
            handler.post(new Runnable() {
               public void run() {
                  onAuthenticationResult(result);
               }
            });
         }
      };
      authenticator.start();
      return authenticator;
   }

   /**
    * Called when the authentication process completes (see attemptLogin()).
    */
   public void onAuthenticationResult(boolean result) {
      // Hide the progress dialog
      hideProgress();
      if (result) {
         if (!confirmCredentials) {
            finishLogin();
         } else {
            finishConfirmCredentials(true);
         }
      } else {
         if (createAccount) {
            status.setText(getText(R.string.login_fail_error));
         }
      }
   }

   /**
    * 
    * Called when response is received from the server for authentication
    * request. See onAuthenticationResult(). Sets the AccountAuthenticatorResult
    * which is sent back to the caller. Also sets the authToken in
    * AccountManager for this account.
    * 
    * @param the
    *           confirmCredentials result.
    */

   protected void finishLogin() {
      final Account account = new Account(accountName, LinkedIn.TYPE);
      if (createAccount) {
         Bundle data = new Bundle();
         data.putString(LinkedIn.AUTH_TOKEN, 
            authToken);
         data.putString(LinkedIn.AUTH_TOKEN_SECRET, 
            authTokenSecret);
         accountManager.addAccountExplicitly(account, 
            enteredPIN, data);
         // Set contacts sync for this account.
         ContentResolver.setSyncAutomatically(account,
            ContactsContract.AUTHORITY, true);
      } else {
         accountManager.setPassword(account, enteredPIN);
         accountManager.setUserData(account, 
               LinkedIn.AUTH_TOKEN, authToken);
         accountManager.setUserData(account, LinkedIn.
               AUTH_TOKEN_SECRET,  authTokenSecret);
      }
      final Intent intent = new Intent();
      intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
      intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, LinkedIn.TYPE);
      setAccountAuthenticatorResult(intent.getExtras());
      setResult(RESULT_OK, intent);
      finish();
   }

   /**
    * Called when response is received from the server for confirm credentials
    * request. See onAuthenticationResult(). Sets the AccountAuthenticatorResult
    * which is sent back to the caller.
    * 
    * @param the
    *           confirmCredentials result.
    */
   protected void finishConfirmCredentials(boolean result) {
      final Account account = new Account(accountName, LinkedIn.TYPE);
      accountManager.setPassword(account, enteredPIN);
      final Intent intent = new Intent();
      intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
      setAccountAuthenticatorResult(intent.getExtras());
      setResult(RESULT_OK, intent);
      finish();
   }

   /**
    * Hides the progress UI for a lengthy operation.
    */
   protected void hideProgress() {
      dismissDialog(0);
   }

   /**
    * Shows the progress UI for a lengthy operation.
    */
   protected void showProgress() {
      showDialog(0);
   }
}
