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
package com.manning.unlockingandroid;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthToken;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Connections;
import com.google.code.linkedinapi.schema.Person;
import com.manning.unlockingandroid.linkedin.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class LinkedInActivity extends Activity {

   public LinkedInActivity() {
   }
   
   private static final String API_KEY = "dqPkKd9ZBXpVmLc83dI_OWZg9nojHA3RPRVwVJ9kUxRE-zZqljgOz_N-KVkvhA5d";
   private static final String SECRET_KEY = "oeERP3xZXW9lPCzB6MMdV45-t6gyP6F-9AWUTZliswKolSERXbQC-nRoLzb0xj68";
   
   private static final String USER_TOKEN = "31c57afe-bffd-4770-8548-4090dbba7ab6";
   private static final String USER_TOKEN_SECRET = "2f749a62-7732-4ba4-9291-c15eed377762";
   
   private static final String PIN = "09516";
   
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      doLogin();
   }
   
   private void doLogin() {
      final LinkedInOAuthService oauthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(API_KEY, SECRET_KEY);
      //LinkedInRequestToken requestToken = oauthService.getOAuthRequestToken();
      LinkedInRequestToken requestToken = new LinkedInRequestToken(USER_TOKEN, USER_TOKEN_SECRET);
      String userToken = requestToken.getToken();
      String userTokenSecret = requestToken.getTokenSecret();
      //((LinkedInApplication)getApplication()).setLastRequest(requestToken);
      /*
      String authURL = requestToken.getAuthorizationUrl();
      Intent authIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authURL));
      startActivity(authIntent);
      */
      String returnedToken = "46032";
      try
      {
         LinkedInAccessToken access = oauthService.getOAuthAccessToken(requestToken, returnedToken);
         LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(API_KEY, SECRET_KEY);
         LinkedInApiClient client = factory.createLinkedInApiClient(access);
         doOp(client);
      }
      catch (Exception e)
      {
         //oauthService.
         e.printStackTrace();
      }
      
   }
   
   private void callback() {
   }
   
   private void doOp(LinkedInApiClient client) {
      /*
      final String consumerKeyValue = API_KEY;
      final String consumerSecretValue = SECRET_KEY;
      final String accessTokenValue = line.getOptionValue(ACCESS_TOKEN_OPTION);
      final String tokenSecretValue = line.getOptionValue(ACCESS_TOKEN_SECRET_OPTION);
      
      final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(consumerKeyValue, consumerSecretValue);
      final LinkedInApiClient client = factory.createLinkedInApiClient(accessTokenValue, tokenSecretValue);
      
      if(line.hasOption(ID_OPTION)) {
              String idValue = line.getOptionValue(ID_OPTION);
              System.out.println("Fetching connections for user with id:" + idValue);
              Connections connections = client.getConnectionsById(idValue);
              printResult(connections);
      } else if (line.hasOption(EMAIL_OPTION)) {
              String emailValue = line.getOptionValue(EMAIL_OPTION);
              System.out.println("Fetching connections for user with email:" + emailValue);
//            Connections connections = client.getConnectionsByEmail(emailValue);
//            printResult(connections);
      } else if (line.hasOption(URL_OPTION)) {
              String urlValue = line.getOptionValue(URL_OPTION);
              System.out.println("Fetching connections for user with url:" + urlValue);
              Connections connections = client.getConnectionsByUrl(urlValue);
              printResult(connections);
      } else {
              System.out.println("Fetching connections for current user.");
              */
              Connections connections = client.getConnectionsForCurrentUser();
              //printResult(connections);
              for (Person person : connections.getPersonList()) {
                 String firstName = person.getFirstName();
                 String lastName = person.getLastName();
                 String status = person.getCurrentStatus();
         }
   }
   

}
