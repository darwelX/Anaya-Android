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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.manning.unlockingandroid.linkedin.LinkedIn;
import com.manning.unlockingandroid.linkedin.R;
import com.manning.unlockingandroid.linkedin.auth.LinkedInLoginActivity;

class LinkedInAccountAuthenticator extends AbstractAccountAuthenticator {
   private final Context context;

   private LinkedInApiClientFactory factory;

   public LinkedInAccountAuthenticator(Context context) {
      super(context);
      this.context = context;
      factory = LinkedInApiClientFactory.newInstance(LinkedIn.API_KEY,
         LinkedIn.SECRET_KEY);
      System.setProperty("http.keepAlive", "false");
   }

   @Override
   public Bundle addAccount(
         AccountAuthenticatorResponse response,
         String accountType, String authTokenType,
         String[] requiredFeatures, Bundle options) {
      // Annotate: Explicitly list the class to handle login
      Intent intent = new Intent(
         context, LinkedInLoginActivity.class);
      intent.putExtra(LinkedInLoginActivity.PARAM_AUTHTOKEN_TYPE,
         authTokenType);
      intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
         response);
      Bundle bundle = new Bundle();
      bundle.putParcelable(AccountManager.KEY_INTENT, intent);
      return bundle;
   }

   @Override
   public Bundle confirmCredentials(AccountAuthenticatorResponse response,
         Account account, Bundle options) {
      if (options != null
            && options.containsKey(AccountManager.KEY_PASSWORD)) {
         String authToken = options.getString(LinkedIn.AUTH_TOKEN);
         String authSecret = options
            .getString(LinkedIn.AUTH_TOKEN_SECRET);
         boolean verified = validateAuthToken(authToken, authSecret);
         Bundle result = new Bundle();
         result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, verified);
         return result;
      }
      Intent intent = new Intent(
         context, LinkedInLoginActivity.class);
      intent.putExtra(LinkedInLoginActivity.PARAM_USERNAME, account.name);
      intent.putExtra(LinkedInLoginActivity.PARAM_CREDENTIALS, true);
      intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
         response);
      Bundle bundle = new Bundle();
      // Verify credentials through the UI (annotate this)
      bundle.putParcelable(
         AccountManager.KEY_INTENT, intent);
      return bundle;
   }

   @Override
   public Bundle editProperties(AccountAuthenticatorResponse response,
         String accountType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Bundle getAuthToken(AccountAuthenticatorResponse response,
         Account account, String authTokenType, Bundle loginOptions) {
      if (!authTokenType.equals(LinkedIn.TYPE)) {
         Bundle result = new Bundle();
         result.putString(AccountManager.KEY_ERROR_MESSAGE,
            "Unrecognized token type");
         return result;
      }
      AccountManager am = AccountManager.get(context);
      String authToken = am.getUserData(account, LinkedIn.AUTH_TOKEN);
      String authTokenSecret = am.getUserData(account,
            LinkedIn.AUTH_TOKEN_SECRET);
      if (authToken != null && authTokenSecret != null) {
         boolean verified = validateAuthToken(authToken,
            authTokenSecret);
         if (verified) {
            Bundle result = new Bundle();
            result.putString(
               AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(
               AccountManager.KEY_ACCOUNT_TYPE, LinkedIn.TYPE);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
         }
      }
      // the password was missing or incorrect, return an Intent to an
      // Activity that will prompt the user for the password.
      Intent intent = new Intent(
         context, LinkedInLoginActivity.class);
      intent.putExtra(AccountManager.
         KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
         response);
      intent.putExtra(LinkedInLoginActivity.PARAM_USERNAME, account.name);
      intent.putExtra(LinkedInLoginActivity.PARAM_AUTHTOKEN_TYPE,
         authTokenType);
      Bundle bundle = new Bundle();
      bundle.putParcelable(AccountManager.KEY_INTENT, intent);
      return bundle;
   }

   @Override
   public String getAuthTokenLabel(String authTokenType) {
      if (authTokenType.equals(LinkedIn.TYPE)) {
         return context.getString(R.string.auth_token_label);
      }
      return null;

   }

   @Override
   public Bundle hasFeatures(AccountAuthenticatorResponse response,
         Account account, String[] features) {
      // Annotate: No special features supported
      Bundle result = new Bundle();
      result.putBoolean(AccountManager.
         KEY_BOOLEAN_RESULT,  false);
      return result;
   }

   /**
    * Validates user's password on the server
    */
   private boolean validateAuthToken(String authToken,
         String authTokenSecret) {
      try {
         LinkedInApiClient client = factory.createLinkedInApiClient(
            authToken, authTokenSecret);
         // #2 Issue a basic network call to ensure that everything is set up
         // properly.
         client.getConnectionsForCurrentUser(0, 1);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Bundle updateCredentials(AccountAuthenticatorResponse response,
         Account account, String authTokenType, Bundle loginOptions) {
      Intent intent = new Intent(context, LinkedInLoginActivity.class);
      intent.putExtra(LinkedInLoginActivity.PARAM_USERNAME, account.name);
      intent.putExtra(LinkedInLoginActivity.PARAM_AUTHTOKEN_TYPE,
         authTokenType);
      intent.putExtra(LinkedInLoginActivity.PARAM_CREDENTIALS, false);
      Bundle bundle = new Bundle();
      bundle.putParcelable(AccountManager.KEY_INTENT, intent);
      return bundle;
   }

}
