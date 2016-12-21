package com.manning.unlockingandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.widget.Toast;

public class ContactPicker extends Activity {

   public static final int CONTACT_SELECTED = 1;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      // Intent intent = new Intent(Intent.ACTION_PICK,
      // Contacts.CONTENT_LOOKUP_URI);
      // Intent intent = new Intent(Intent.ACTION_PICK,
      // ContactsContract.Data.DATA1. );//Email.CONTENT_URI);
      // Intent intent = new Intent(Intent.ACTION_PICK,
      // ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI);
      // Intent chooser = new Intent(Intent.ACTION_PICK,
      // ContactsContract.Contacts.CONTENT_URI);
      // startActivityForResult(chooser, CONTACT_SELECTED);
      // createContact();
      // editContact();
      silentEditContact();
   }

private void selectContact() {
   Intent chooser = new Intent(Intent.ACTION_PICK,
         ContactsContract.Contacts.CONTENT_URI);
   startActivityForResult(chooser, CONTACT_SELECTED);
}

   private void createContact() {
      Intent creator = new Intent(Intent.ACTION_INSERT,
            ContactsContract.Contacts.CONTENT_URI);
      creator.putExtra(ContactsContract.Intents.Insert.NAME, "Oedipa Maas");
      creator.putExtra(ContactsContract.Intents.Insert.EMAIL,
            "oedipa@waste.example.com");
      startActivity(creator);
   }

   private void silentEditContact() {
   ContentResolver resolver = getContentResolver();
   ContentValues values = new ContentValues();
   Uri contactUri = ContactsContract.Data.CONTENT_URIet
   values.put(ContactsContract.CommonDataKinds.
      Email.DATA, "concealed@example.com");
   String where = ContactsContract.CommonDataKinds.
      Email.DATA + " like '%waste%'";
   resolver.update(contactUri, values, where, null);
// Uri personUri = ContactsContract.Contacts.CONTENT_URI;
   /*
   .buildUpon()
      .appendPath(Long.toString(this.contactId)).build();
      */
   /*
   values.put(Contacts.People.NAME,
   this.editName.getText().toString());
   */
//   resolver.insert(url, values);
   /*
   values.clear(); 
   Uri phoneUri = Uri.withAppendedPath(personUri,
   Contacts.People.Phones.CONTENT_DIRECTORY + "/1");
   values.put(Contacts.Phones.NUMBER,
   this.editPhoneNumber.getText().toString());
   resolver.update(phoneUri, values, null, null);
   this.startActivity(new Intent(this, ProviderExplorer.class));   
   
   
   Cursor c = null;
   try {
      Uri uri = ContactsContract.Contacts.CONTENT_URI;
      String[] projection = new String[] { ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Note.DATA1 };
      String selection = ContactsContract.CommonDataKinds.Email.DATA + " like ?";
      String[] selectionArgs = new String[] { "waste.co" };
      c = managedQuery(uri, projection, selection, selectionArgs, null);
      while (c.moveToNext()) {
         String message = "Person of interest";
         String note = c.getString(1);
         if (!note.contains(message))
            ;
         int id = c.getInt(0);
         Uri contact = ContentUris.appendId(
            ContactsContract.Contacts.CONTENT_URI.
            buildUpon(), id).build();
         Intent editor = new Intent(Intent.ACTION_EDIT, contact);
      }
   } finally {
      if (c != null)
         c.close();
   }
   */
}

   private void editContact() {
      Cursor c = null;
      try {
         Uri uri = ContactsContract.Contacts.CONTENT_URI;
         String[] projection = new String[] { ContactsContract.Contacts._ID };
         String selection = ContactsContract.Contacts.DISPLAY_NAME + "=?";
         String[] selectionArgs = new String[] { "Oedipa Maas" };
         c = managedQuery(uri, projection, selection, selectionArgs, null);
         if (c.moveToFirst()) {
            int id = c.getInt(0);
            Uri contact = ContentUris.appendId(
                  ContactsContract.Contacts.CONTENT_URI.buildUpon(), id)
                  .build();
            Intent editor = new Intent(Intent.ACTION_EDIT, contact);
         }
      } finally {
         if (c != null)
            c.close();
      }
   }

   /*
    * int nameIndex = c
    * .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME); String
    * name = c.getString(nameIndex); Toast.makeText(this, name, 2000).show();
    * 
    * String lookupKey = c .getString(c
    * .getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)); // c =
    * getContentResolver().query(lookupKey, new // String[]{Contacts.});
    */

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      switch (requestCode) {
      case (CONTACT_SELECTED):
         if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {

               int nameIndex = c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);

               try {
                  int contactID = c
                        .getInt(c
                              .getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                  Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                  String[] projection = new String[] { ContactsContract.CommonDataKinds.Email.DATA };
                  String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID
                        + "=?";
                  String[] selectionArgs = new String[] { "" + contactID };
                  c.close();
                  c = managedQuery(uri, projection, selection,
                        selectionArgs, null);
                  String message;
                  if (c.moveToFirst()) {
                     message = "Selected email address " + c.getString(0);
                  } else {
                     message = "No email address found.";
                  }
                  Toast.makeText(this, message, 2000).show();
               } finally {
                  c.close();
               }
            }
         }
         break;
      }
   }

   /*
    * String[] cols = c.getColumnNames(); //ContactsContract.Data. PhoneLookup.
    * //ContactsContract.Contacts.LOOKUP_KEY int emailIndex = c.
    * getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA ); if
    * (emailIndex != -1) { String email = c.getString(emailIndex);
    * System.out.println("Email is " + email); }
    */
}