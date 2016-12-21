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
package com.manning.unlockingandroid.linkedin;

import java.util.ArrayList;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;

public class ContactHelper {

   public static boolean addContact(ContentResolver resolver,
         Account account, String name, String username, String headline) {
      ArrayList<ContentProviderOperation> batch = 
         new ArrayList<ContentProviderOperation>();

      ContentProviderOperation.Builder builder = ContentProviderOperation
         .newInsert(RawContacts.CONTENT_URI);
      builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
      builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
      builder.withValue(RawContacts.SYNC1, username);
      batch.add(builder.build());

      builder = ContentProviderOperation
         .newInsert(ContactsContract.Data.CONTENT_URI);
      builder.withValueBackReference(ContactsContract.CommonDataKinds.
         StructuredName.RAW_CONTACT_ID, 0);
      builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.
         CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
      builder.withValue(ContactsContract.CommonDataKinds.StructuredName.
         DISPLAY_NAME, name);
      batch.add(builder.build());

      // Create a Data record of custom type
      // "vnd.android.cursor.item/vnd.linkedin.profile" to display in Contacts
      builder = ContentProviderOperation.newInsert(
         ContactsContract.Data.CONTENT_URI);
      builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
         0);
      builder.withValue(ContactsContract.Data.MIMETYPE, 
         LinkedIn.MIME_TYPE);
      builder.withValue(ContactsContract.Data.DATA1, 
         headline);
      builder.withValue(ContactsContract.Data.DATA2, "LinkedIn");

      batch.add(builder.build());
      try {
         resolver.applyBatch(ContactsContract.AUTHORITY, batch);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   public static boolean updateContact(ContentResolver resolver,
         Account account, String username, String headline) {
      ArrayList<ContentProviderOperation> batch = 
         new ArrayList<ContentProviderOperation>();
      ContentProviderOperation.Builder builder = ContentProviderOperation
         .newInsert(ContactsContract.Data.CONTENT_URI);
      builder.withValue(ContactsContract.Data.RAW_CONTACT_ID, 0);
      builder.withValue(ContactsContract.Data.MIMETYPE, 
         LinkedIn.MIME_TYPE);
      builder.withValue(ContactsContract.Data.DATA1, 
         headline);
      builder.withValue(ContactsContract.Data.DATA2, "LinkedIn");
      batch.add(builder.build());
      try {
         resolver.applyBatch(ContactsContract.AUTHORITY, batch);
         return true;
      } catch (Exception e) {
         return false;
      }
   }
}
