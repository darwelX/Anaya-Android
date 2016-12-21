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
package com.manning.unlockingandroid.linkedin.sync;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientException;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.schema.Connections;
import com.google.code.linkedinapi.schema.Person;
import com.manning.unlockingandroid.linkedin.ContactHelper;
import com.manning.unlockingandroid.linkedin.LinkedIn;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
   private final AccountManager manager;
   private final LinkedInApiClientFactory factory;
   private final ContentResolver resolver;

   String[] idSelection = new String[] { 
      ContactsContract.RawContacts.SYNC1 };
   String[] idValue = new String[1];

   public SyncAdapter(Context context, boolean autoInitialize) {
      super(context, autoInitialize);
      resolver = context.getContentResolver();
      manager = AccountManager.get(context);
      factory = LinkedInApiClientFactory.newInstance(LinkedIn.API_KEY,
            LinkedIn.SECRET_KEY);
   }

   @Override
   public void onPerformSync(Account account, Bundle extras,
         String authority, ContentProviderClient provider,
         SyncResult syncResult) {
      String authToken = null;
      try {
         // use the account manager to request the credentials
         authToken = manager.blockingGetAuthToken(
               account, LinkedIn.TYPE, true);
         if (authToken == null) {
            syncResult.stats.numAuthExceptions++;
            return;
         }
         authToken = manager.getUserData(account,
               LinkedIn.AUTH_TOKEN);
         String authTokenSecret = manager.getUserData(
               account, LinkedIn.AUTH_TOKEN_SECRET);
         LinkedInApiClient client = factory.createLinkedInApiClient(
               authToken, authTokenSecret);
         // Fetch all of the contacts.
         Connections people = client.getConnectionsForCurrentUser();
         for (Person person:people.getPersonList()) {
            String id = person.getId();
            String firstName = person.getFirstName();
            String lastName = person.getLastName();
            String headline = person.getHeadline();
            // Check to see whether we've already added this contact.
            idValue[0] = id;
            Cursor matches = resolver.query(
ContactsContract.RawContacts.CONTENT_URI, idSelection,
ContactsContract.RawContacts.SYNC1 + "=?", idValue, 
null);
            if (matches.moveToFirst()) {
               // We have a contact, update it.
               // First insert/update values into the data table, then update
               // the raw_contacts row.
               ContactHelper.updateContact(
                  resolver, account, id, headline);
            } else {
               // No contact yet, so create one.
               // Insert into data, then create raw_contacts row.
               ContactHelper.addContact(resolver, 
                  account, firstName + " "
                  + lastName, id, headline);
            }
         }
      } catch (AuthenticatorException e) {
         manager.invalidateAuthToken(LinkedIn.TYPE, authToken);
         syncResult.stats.numAuthExceptions++;
      } catch (IOException ioe) {
         syncResult.stats.numIoExceptions++;
      } catch (OperationCanceledException ioe) {
         syncResult.stats.numIoExceptions++;
      } catch (LinkedInApiClientException liace) {
         manager.invalidateAuthToken(LinkedIn.TYPE, authToken);
         syncResult.stats.numAuthExceptions++;
      }
   }

}
