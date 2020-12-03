package com.android.documentationrecordviafingerprint.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.documentationrecordviafingerprint.helper.SessionManagement;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.IMyConstants;
import com.android.documentationrecordviafingerprint.model.User;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.android.documentationrecordviafingerprint.model.UserNotes;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomProgressDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomProgressbar;
import com.android.documentationrecordviafingerprint.view.DashboardActivity;
import com.android.documentationrecordviafingerprint.view.OnlineFileViewerActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;

public final class MyFirebaseDatabase implements IMyConstants {
    private static final DatabaseReference databaseReference;
    private static final StorageReference storageReference;
    private static String email_identifier;
    private static CustomProgressDialog progDialog;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    static {
        databaseReference = DB.getDBFirstNodeReference();
        storageReference = DB.getStorageReference();
    }

    public static void createNewUserAccount(final Context context, final User user, final Activity activity) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            Query checkDuplicateAcc = databaseReference.orderByChild(KEY_EMAIL).equalTo(user.getEmail());

            email_identifier = StringOperations.removeInvalidCharsFromIdentifier(user.getEmail());

            checkDuplicateAcc.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Account already exists
                        progDialog.dismissDialog();
                        Toast.makeText(context, "Account already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            //Account Created
                            databaseReference.child(email_identifier).setValue(user);
                            Toast.makeText(context, "New Account Created Successfully", Toast.LENGTH_SHORT).show();
                            new SessionManagement(context).setSession(user.getEmail());
                            context.startActivity(new Intent(context, DashboardActivity.class));
                            progDialog.dismissDialog();
                            activity.finish();
                        } catch (Exception e) {
                            progDialog.dismissDialog();
                            Toast.makeText(context, "Can't create account at this time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void changeFirstName() {

    }

    public static void changeLastName() {

    }

    public static void changePassword() {

    }

    public static void deleteAllUserData(Activity activity) {
        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(activity, "WARNING!!!" +
                "\n\nAll your data will be deleted and can not be recovered!" +
                "\n\nAre you sure to delete all data?\n\n");
        customConfirmDialog.setBtnText("Delete Data")
                .dangerBtn()
                .setOkBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customConfirmDialog.dismissDialog();

                    }
                });
    }

    public static void deleteUserAccount(Activity activity) {
        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(activity, "WARNING!!!" +
                "\n\nAll your data will be deleted and can not be recovered!" +
                "\n\nAre you sure to delete your account?\n\n");
        customConfirmDialog.setBtnText("Delete Account")
                .dangerBtn()
                .setOkBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customConfirmDialog.dismissDialog();

                    }
                });
    }

    public static void getFullName(final Context context, final TextView fullname_tv) {
        try {
            email_identifier = new SessionManagement(context).getEmailIdentifier();
            Query checkAccount = databaseReference.child(email_identifier);
            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String first_name = dataSnapshot.child(KEY_FIRST_NAME).getValue(String.class);
                        String last_name = dataSnapshot.child(KEY_LAST_NAME).getValue(String.class);
                        String full_name = first_name + " " + last_name;
                        fullname_tv.setText(StringOperations.capitalizeString(full_name));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void verifyLoginCredentials(final Context context, final String email, final String password, final Activity activity) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            Query checkAccount = databaseReference.orderByChild(KEY_EMAIL).equalTo(email);

            email_identifier = StringOperations.removeInvalidCharsFromIdentifier(email);

            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String passFromDB = dataSnapshot.child(email_identifier).child(KEY_PASSWORD).getValue(String.class);
                        if (password.equals(passFromDB)) {
                            new SessionManagement(context).setSession(email);
                            progDialog.dismissDialog();
                            context.startActivity(new Intent(context, DashboardActivity.class));
                            activity.finish();
                        } else {
                            progDialog.dismissDialog();
                            Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progDialog.dismissDialog();
                        Toast.makeText(context, "Incorrect Email", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void deleteFile(final Activity activity, final String file_key, final String file_identifier, final boolean close_activity) {
        progDialog = new CustomProgressDialog(activity, "Deleting Existing File . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            Task<Void> task = storageReference.child(file_key).delete();
            task.addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReference.child(email_identifier).child(ID_FILES_PATH).child(file_identifier)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(activity, "File Deleted Successfully", Toast.LENGTH_LONG).show();
                            progDialog.dismissDialog();
                            if (close_activity) {
                                activity.finish();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progDialog.dismissDialog();
                    Toast.makeText(activity, "Failed to Delete File", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void requestFileUpload(final Activity activity, final String file_icon_uri, final String file_name, final String file_extension, final String file_type, final Uri file_uri, final String file_identifier, final String file_size) {
        progDialog = new CustomProgressDialog(activity, "Checking Database . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();

            DatabaseReference childReference = databaseReference.child(email_identifier).child(ID_FILES_PATH).child(file_identifier);
            Query checkuser = childReference.orderByChild(KEY_NAME);
            checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {        //checking filename and type if exist
                        progDialog.dismissDialog();
                        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(activity, "File Duplication not Allowed!\n\n" +
                                "File already exists with this name and type, Long press selected file to Rename file " +
                                "or Update existing file on cloud.");
                        customConfirmDialog.setBtnText("Update")
                                .setOkBtn(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customConfirmDialog.dismissDialog();
                                        if (CheckInternetConnectivity.isInternetConnected(activity)) {
                                            String old_file_storage_key = dataSnapshot.child(KEY_FILE_STORAGE_ID).getValue(String.class);
                                            deleteFile(activity, old_file_storage_key, file_identifier, false);
                                            uploadFile(activity, file_icon_uri, file_name, file_extension, file_type, file_uri, file_identifier, file_size);
                                        } else {
                                            Toast.makeText(activity, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        progDialog.dismissDialog();
                        uploadFile(activity, file_icon_uri, file_name, file_extension, file_type, file_uri, file_identifier, file_size);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progDialog.dismissDialog();
                    Toast.makeText(activity, "Upload Cancelled " + databaseError, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private static void uploadFile(final Context context, final String file_icon_uri, final String file_name, final String file_extension, final String file_type, final Uri file_uri, final String file_identifier, final String file_size) {
        final CustomProgressbar pbar = new CustomProgressbar(context);
        final String file_storage_id = System.currentTimeMillis() + "";
        final UploadTask uploadTask = storageReference.child(file_storage_id).putFile(file_uri);
        pbar.setCancelBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTask.cancel();
            }
        }).setPauseBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uploadTask.pause()) {
                    pbar.setPauseText();
                    uploadTask.resume();
                } else {
                    pbar.setResumeText();
                }
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        String upload_date = simpleDateFormat.format(System.currentTimeMillis());
                        UserFile userFile = new UserFile(file_icon_uri, file_name, file_extension, file_type, uri.toString(), file_size, file_identifier, file_storage_id, upload_date);
                        databaseReference.child(email_identifier).child(ID_FILES_PATH).child(file_identifier).setValue(userFile);
                        Toast.makeText(context, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        pbar.dismissDialog();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "File Upload Failed", Toast.LENGTH_LONG).show();
                pbar.dismissDialog();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                pbar.setProgress((int) currentProgress);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(context, "File Upload Canceled", Toast.LENGTH_LONG).show();
                pbar.dismissDialog();
            }
        });
    }

    public static void renameFileOnCloud(final Activity activity, final String new_file_name, final String new_file_id, final UserFile model) {
        final String old_file_id = model.getId();
        progDialog = new CustomProgressDialog(activity, "Renaming File Name . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            DatabaseReference childReference = databaseReference.child(email_identifier).child(ID_FILES_PATH).child(new_file_id);
            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        progDialog.dismissDialog();
                        new CustomMsgDialog(activity, "File Duplication Not Allowed", "File already exists with this name and type, Try different file name.");
                    } else {
                        model.setId(new_file_id);
                        model.setName(new_file_name);
                        Task<Void> task = databaseReference.child(email_identifier).child(ID_FILES_PATH).child(new_file_id).setValue(model);
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(email_identifier).child(ID_FILES_PATH).child(old_file_id).setValue(null);
                                Intent it = new Intent(activity, OnlineFileViewerActivity.class);
                                it.putExtra(EXTRA_USER_FILE, model);
                                progDialog.dismissDialog();
                                activity.finish();
                                activity.startActivity(it);
                                Toast.makeText(activity, "File Renamed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void uploadNotes(final Context context, final UserNotes userNotes) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(context).getEmailIdentifier();
            DatabaseReference childReference = databaseReference.child(email_identifier).child(ID_NOTES_PATH).child(userNotes.getId());
            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progDialog.dismissDialog();
                        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, "Notes with title "
                                + userNotes.getName().toUpperCase() + " already exists!\n\nDo you want to Update them?");
                        customConfirmDialog.setBtnText("Update")
                                .setOkBtn(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customConfirmDialog.dismissDialog();
                                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                                            String date_modify = simpleDateFormat.format(System.currentTimeMillis());
                                            userNotes.setDate_modify(date_modify);
                                            databaseReference.child(email_identifier).child(ID_NOTES_PATH)
                                                    .child(userNotes.getId())
                                                    .setValue(userNotes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Notes Updated on Server Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        try {
                            databaseReference.child(email_identifier).child(ID_NOTES_PATH).child(userNotes.getId()).setValue(userNotes);
                            progDialog.dismissDialog();
                            Toast.makeText(context, "Notes Saved Successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            progDialog.dismissDialog();
                            Toast.makeText(context, "Can't create notes due to some errors", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void editNotes(final Context context) {
        try {

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void renameNotes(final Context context) {
        try {

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void deleteNotes(final Activity activity, final String file_id, final boolean close_activity) {
        progDialog = new CustomProgressDialog(activity, "Deleting Existing Notes . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            final DatabaseReference childReference = databaseReference.child(email_identifier).child(ID_NOTES_PATH).child(file_id);
            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        childReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progDialog.dismissDialog();
                                if (close_activity) {
                                    activity.finish();
                                }
                                Toast.makeText(activity, "Notes Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progDialog.dismissDialog();
                                Toast.makeText(activity, "Failed to Delete Notes", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        progDialog.dismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }
}
