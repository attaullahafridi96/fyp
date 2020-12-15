package com.android.documentationrecordviafingerprint.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.SessionManagement;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.User;
import com.android.documentationrecordviafingerprint.model.UserNotes;
import com.android.documentationrecordviafingerprint.model.UserUploads;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomHorizontalProgressDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomProgressDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomToast;
import com.android.documentationrecordviafingerprint.view.DashboardActivity;
import com.android.documentationrecordviafingerprint.view.LoginActivity;
import com.android.documentationrecordviafingerprint.view.NotesEditorActivity;
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
import java.util.ArrayList;

public final class MyFirebaseDatabase implements IMyConstants {
    private static final DatabaseReference realtimeDatabaseReference;
    private static final StorageReference storageReference;
    private static String email_identifier;
    private static CustomProgressDialog progDialog;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    static {
        realtimeDatabaseReference = DB.getRtDBFirstNodeReference();
        storageReference = DB.getStorageReference();
    }

    public static void createNewUserAccount(final Context context, final User user, final Activity activity) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            Query checkDuplicateAcc = realtimeDatabaseReference.orderByChild(USER_KEY_EMAIL).equalTo(user.getEmail());

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
                            realtimeDatabaseReference.child(email_identifier).setValue(user);
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
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    ////////////////////////////////    SETTINGS LOGIC STARTS FROM HERE    ////////////////////////////////////////////////////
    public static void changeFirstName(final Activity activity, final String newFirstName) {
        progDialog = new CustomProgressDialog(activity, "Changing First Name . . .");
        progDialog.showDialog();
        try {
            Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(USER_KEY_FIRST_NAME).setValue(newFirstName);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "First Name Changed Successfully", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Changing first name failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void changeLastName(final Activity activity, final String newLastName) {
        progDialog = new CustomProgressDialog(activity, "Changing Last Name . . .");
        progDialog.showDialog();
        try {
            Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(USER_KEY_LAST_NAME).setValue(newLastName);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Last Name Changed Successfully", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Changing last name failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void changePassword(final Activity activity, final String newPassword) {
        progDialog = new CustomProgressDialog(activity, "Changing Password . . .");
        progDialog.showDialog();
        try {
            Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(USER_KEY_PASSWORD).setValue(newPassword);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Password Changed Successfully", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    CustomToast.makeToast(activity, "Changing password failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void deleteUserAllData(final Activity activity) {
        progDialog = new CustomProgressDialog(activity, "Deleting all data . . .");
        progDialog.showDialog();
        try {
            final SessionManagement session = new SessionManagement(activity);
            email_identifier = session.getEmailIdentifier();
            Query query = realtimeDatabaseReference.child(email_identifier).child(ID_FILES);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                private final ArrayList<String> file_storage_ids = new ArrayList<>();

                @Override
                public void onDataChange(@NonNull DataSnapshot uploaded_files) {
                    if (uploaded_files.exists()) {
                        for (DataSnapshot snapshot1 : uploaded_files.getChildren()) {
                            UserUploads user = snapshot1.getValue(UserUploads.class);
                            file_storage_ids.add(user.getFileStorageId());
                        }
                        for (int i = 0; i < file_storage_ids.size(); i++) {
                            if (i == file_storage_ids.size() - 1) {
                                deleteSingleFileFromUploads(file_storage_ids.get(i))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(ID_FILES).setValue(null);
                                                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progDialog.dismissDialog();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progDialog.dismissDialog();
                                                        CustomToast.makeToast(activity, "Can't delete your files at this time!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                                                    }
                                                });
                                                task = realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).setValue(null);
                                                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progDialog.dismissDialog();
                                                        CustomToast.makeToast(activity, "All your Files and Notes Deleted Successfully", Toast.LENGTH_SHORT);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progDialog.dismissDialog();
                                                        CustomToast.makeToast(activity, "Can't delete your notes at this time!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progDialog.dismissDialog();
                                        CustomToast.makeToast(activity, "Data deletion Failed\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                                    }
                                });
                            } else {
                                deleteSingleFileFromUploads(file_storage_ids.get(i));
                            }
                        }
                    } else {
                        Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).setValue(null);
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progDialog.dismissDialog();
                                CustomToast.makeToast(activity, "All your Notes Deleted Successfully", Toast.LENGTH_LONG);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progDialog.dismissDialog();
                                CustomToast.makeToast(activity, "Can't delete your data at this time!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
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
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void deleteUserAccount(final Activity activity) {
        progDialog = new CustomProgressDialog(activity, "Deleting your account . . .");
        progDialog.showDialog();
        try {
            final SessionManagement session = new SessionManagement(activity);
            email_identifier = session.getEmailIdentifier();
            Query query = realtimeDatabaseReference.child(email_identifier).child(ID_FILES);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                private final ArrayList<String> file_storage_ids = new ArrayList<>();

                @Override
                public void onDataChange(@NonNull DataSnapshot uploaded_files) {
                    if (uploaded_files.exists()) {
                        for (DataSnapshot snapshot1 : uploaded_files.getChildren()) {
                            UserUploads user = snapshot1.getValue(UserUploads.class);
                            file_storage_ids.add(user.getFileStorageId());
                        }
                        for (int i = 0; i < file_storage_ids.size(); i++) {
                            if (i == file_storage_ids.size() - 1) {
                                deleteSingleFileFromUploads(file_storage_ids.get(i))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Task<Void> task = realtimeDatabaseReference.child(email_identifier).setValue(null);
                                                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        session.destroySession();
                                                        progDialog.dismissDialog();
                                                        activity.startActivity(new Intent(activity, LoginActivity.class));
                                                        activity.finish();
                                                        CustomToast.makeToast(activity, "Your Account Deleted Successfully", Toast.LENGTH_LONG);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progDialog.dismissDialog();
                                                        CustomToast.makeToast(activity, "Can't delete your Account at this time!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progDialog.dismissDialog();
                                        CustomToast.makeToast(activity, "Data deletion Failed", Toast.LENGTH_LONG);
                                    }
                                });
                            } else {
                                deleteSingleFileFromUploads(file_storage_ids.get(i));
                            }
                        }
                    } else {
                        Task<Void> task = realtimeDatabaseReference.child(email_identifier).setValue(null);
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                session.destroySession();
                                progDialog.dismissDialog();
                                activity.startActivity(new Intent(activity, LoginActivity.class));
                                activity.finish();
                                CustomToast.makeToast(activity, "Your Account Deleted Successfully", Toast.LENGTH_LONG);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progDialog.dismissDialog();
                                CustomToast.makeToast(activity, "Can't delete your Account at this time!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
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
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private static Task<Void> deleteSingleFileFromUploads(final String file_storage_id) {
        return storageReference.child(file_storage_id).delete();
    }
    ////////////////////////////////    SETTINGS LOGIC ENDS HERE    ////////////////////////////////////////////////////

    public static void getFullName(final Context context, final TextView fullname_tv) {
        try {
            email_identifier = new SessionManagement(context).getEmailIdentifier();
            Query checkAccount = realtimeDatabaseReference.child(email_identifier);
            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String first_name = dataSnapshot.child(USER_KEY_FIRST_NAME).getValue(String.class);
                        String last_name = dataSnapshot.child(USER_KEY_LAST_NAME).getValue(String.class);
                        String full_name = first_name + " " + last_name;
                        fullname_tv.setText(StringOperations.capitalizeString(full_name));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            CustomToast.makeToast(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void verifyLoginCredentials(final Context context, final String email, final String password, final Activity activity) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            Query checkAccount = realtimeDatabaseReference.orderByChild(USER_KEY_EMAIL).equalTo(email);

            email_identifier = StringOperations.removeInvalidCharsFromIdentifier(email);

            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String passFromDB = dataSnapshot.child(email_identifier).child(USER_KEY_PASSWORD).getValue(String.class);
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
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void deleteFile(final Activity activity, final String file_storage_id, final String file_identifier, final boolean close_activity) {
        progDialog = new CustomProgressDialog(activity, "Deleting Existing File . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            Task<Void> task = storageReference.child(file_storage_id).delete();
            task.addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    realtimeDatabaseReference.child(email_identifier).child(ID_FILES).child(file_identifier)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(activity, "File Deleted Successfully", Toast.LENGTH_SHORT).show();
                            progDialog.dismissDialog();
                            if (close_activity) {
                                activity.finish();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progDialog.dismissDialog();
                    Toast.makeText(activity, "File deletion Failed\nError:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void requestFileUpload(final Activity activity, final String file_icon_uri, final String file_name, final String file_extension, final String file_type, final Uri file_uri, final String file_identifier, final String file_size) {
        progDialog = new CustomProgressDialog(activity, "Checking Database . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();

            DatabaseReference childReference = realtimeDatabaseReference.child(email_identifier).child(ID_FILES).child(file_identifier);
            Query checkuser = childReference.orderByChild(KEY_TITLE);
            checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {        //checking filename and type if exist
                        progDialog.dismissDialog();
                        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(activity, "File Duplication not Allowed!\n\n" +
                                "File already exists with this name and type, Rename file on long press selected file " +
                                "or you can Update existing file on cloud.");
                        customConfirmDialog.setBtnText("Update")
                                .setOkBtn(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customConfirmDialog.dismissDialog();
                                        if (CheckInternetConnectivity.isInternetConnected(activity)) {
                                            String old_file_storage_key = dataSnapshot.child(KEY_FILE_STORAGE_ID).getValue(String.class);
                                            String date_upload = dataSnapshot.child(KEY_DATE_UPLOAD).getValue(String.class);
                                            deleteFile(activity, old_file_storage_key, file_identifier, false);
                                            String modity_date = simpleDateFormat.format(System.currentTimeMillis());
                                            uploadFile(activity, file_icon_uri, file_name, file_extension, file_type, file_uri, file_identifier, file_size, date_upload, modity_date);
                                        } else {
                                            Toast.makeText(activity, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        progDialog.dismissDialog();
                        String upload_date = simpleDateFormat.format(System.currentTimeMillis());
                        uploadFile(activity, file_icon_uri, file_name, file_extension, file_type, file_uri, file_identifier, file_size, upload_date, null);
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
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private static void uploadFile(final Context context, final String file_icon_uri, final String file_name,
                                   final String file_extension, final String file_type, final Uri file_uri,
                                   final String file_identifier, final String file_size,
                                   final String date_upload, final String date_modify) {
        try {
            final CustomHorizontalProgressDialog pbar = new CustomHorizontalProgressDialog(context);
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
                            UserUploads userUploads = new UserUploads(file_icon_uri, file_name, file_extension, file_type, uri.toString(), file_size, file_identifier, file_storage_id, date_upload);
                            userUploads.setDateModify(date_modify);
                            realtimeDatabaseReference.child(email_identifier).child(ID_FILES).child(file_identifier).setValue(userUploads);
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
        } catch (Exception e) {
            progDialog.dismissDialog();
            CustomToast.makeToast(context, "Error: " + e, Toast.LENGTH_LONG);
        }
    }

    public static void renameFileOnCloud(final Activity activity, final String new_file_name, final String new_file_id, final UserUploads model) {
        final String old_file_id = model.getId();
        progDialog = new CustomProgressDialog(activity, "Renaming File . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            DatabaseReference childReference = realtimeDatabaseReference.child(email_identifier).child(ID_FILES).child(new_file_id);
            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        progDialog.dismissDialog();
                        new CustomMsgDialog(activity, "File Duplication Not Allowed", "File already exists with this name and type, Try different file name.");
                    } else {
                        model.setId(new_file_id);
                        model.setTitle(new_file_name);
                        Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(ID_FILES).child(new_file_id).setValue(model);
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                realtimeDatabaseReference.child(email_identifier).child(ID_FILES).child(old_file_id).setValue(null);
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
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    ////////////////////////////////    NOTES LOGIC STARTS FROM HERE    ////////////////////////////////////////////////////

    public static void uploadNotes(final Context context, final UserNotes userNotes, final Menu toolbar_menu, final EditText notes_title_ed, final TextView editor_title) {
        progDialog = new CustomProgressDialog(context, "Processing . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(context).getEmailIdentifier();
            DatabaseReference childReference = realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).child(userNotes.getId());
            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progDialog.dismissDialog();
                        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, "Notes with title "
                                + userNotes.getTitle().toUpperCase() + " already exists!\n\nDo you want to Update them?");
                        customConfirmDialog.setBtnText("Update")
                                .setOkBtn(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customConfirmDialog.dismissDialog();
                                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                                            String date_modify = simpleDateFormat.format(System.currentTimeMillis());
                                            userNotes.setDateModify(date_modify);
                                            realtimeDatabaseReference.child(email_identifier).child(ID_NOTES)
                                                    .child(userNotes.getId())
                                                    .setValue(userNotes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    CustomToast.makeToast(context, "Notes Updated on Server Successfully", Toast.LENGTH_SHORT);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    CustomToast.makeToast(context, "Notes Updation Failed!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(context, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        try {
                            Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).child(userNotes.getId()).setValue(userNotes);
                            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    toolbar_menu.findItem(R.id.editor_rename_menu_item).setVisible(true);
                                    toolbar_menu.findItem(R.id.editor_delete_menu_item).setVisible(true);
                                    notes_title_ed.setEnabled(false);
                                    editor_title.setText(StringOperations.capitalizeString(userNotes.getTitle()));
                                    progDialog.dismissDialog();
                                    Toast.makeText(context, "Notes Uploaded to Server Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progDialog.dismissDialog();
                                    Toast.makeText(context, "Failed to save Notes\nError: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
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
            progDialog.showDialog();
            CustomToast.makeToast(context, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void renameNotesOnCloud(final Activity activity, final String new_notes_name, final String new_notes_id, final UserNotes model, final boolean close_activity) {
        final String old_notes_id = model.getId();
        progDialog = new CustomProgressDialog(activity, "Renaming Notes . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            DatabaseReference childReference = realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).child(new_notes_id);
            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        progDialog.dismissDialog();
                        new CustomMsgDialog(activity, "Notes Duplication Not Allowed", "Notes already exists with this name, Try different notes name.");
                    } else {
                        model.setId(new_notes_id);
                        model.setTitle(new_notes_name);
                        Task<Void> task = realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).child(new_notes_id).setValue(model);
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).child(old_notes_id).setValue(null);
                                progDialog.dismissDialog();
                                if (close_activity) {
                                    Intent it = new Intent(activity, NotesEditorActivity.class);
                                    it.putExtra(EXTRA_USER_NOTES, model);
                                    activity.finish();
                                    activity.startActivity(it);
                                }
                                Toast.makeText(activity, "Notes Renamed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progDialog.dismissDialog();
                                CustomToast.makeToast(activity, "Can't rename Notes at this time!\nError:" + e.getMessage(), Toast.LENGTH_LONG);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            progDialog.showDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    public static void deleteNotes(final Activity activity, final String file_id, final boolean close_activity) {
        progDialog = new CustomProgressDialog(activity, "Deleting Existing Notes . . .");
        progDialog.showDialog();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            final DatabaseReference childReference = realtimeDatabaseReference.child(email_identifier).child(ID_NOTES).child(file_id);
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
            progDialog.showDialog();
            CustomToast.makeToast(activity, "Error:" + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
}
