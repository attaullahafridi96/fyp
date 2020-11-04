package com.android.documentationrecordviafingerprint.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.documentationrecordviafingerprint.controller.DashboardActivity;
import com.android.documentationrecordviafingerprint.controller.OnlineFileViewer;
import com.android.documentationrecordviafingerprint.controller.SessionManagement;
import com.android.documentationrecordviafingerprint.controller.StringOperations;
import com.android.documentationrecordviafingerprint.uihelper.CustomProgressbar;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
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

public final class MyFirebaseDatabase {
    private static final DatabaseReference databaseReference;
    private static final StorageReference storageReference;
    private static String email_identifier;
    private static ProgressDialog progressDialog;
    private static final String FILES_KEY = "files";

    static {
        databaseReference = DB.getDbFirstNodeReference();
        storageReference = DB.getStorageReference();
    }

    public static void createNewUserAccount(final Context context, final User user, final Activity activity) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            Query checkDuplicateAcc = databaseReference.orderByChild("email").equalTo(user.getEmail());

            email_identifier = StringOperations.removeInvalidCharsFromIdentifier(user.getEmail());

            checkDuplicateAcc.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //Account already exists
                        progressDialog.dismiss();
                        Toast.makeText(context, "Account already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            //Account Created
                            databaseReference.child(email_identifier).setValue(user);
                            Toast.makeText(context, "New Account Created Successfully", Toast.LENGTH_SHORT).show();
                            new SessionManagement(context).setSession(user.getEmail());
                            context.startActivity(new Intent(context, DashboardActivity.class));
                            progressDialog.dismiss();
                            activity.finish();
                        } catch (Exception e) {
                            progressDialog.dismiss();
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

    public static void getFullName(final Context context, final TextView textView) {
        try {
            email_identifier = new SessionManagement(context).getEmailIdentifier();
            Query checkAccount = databaseReference.child(email_identifier);
            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String first_name = dataSnapshot.child("first_name").getValue(String.class);
                        String last_name = dataSnapshot.child("last_name").getValue(String.class);
                        String full_name = first_name + " " + last_name;
                        textView.setText(StringOperations.capitalizeString(full_name));
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
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        try {
            Query checkAccount = databaseReference.orderByChild("email").equalTo(email);

            email_identifier = StringOperations.removeInvalidCharsFromIdentifier(email);

            checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String passFromDB = dataSnapshot.child(email_identifier).child("password").getValue(String.class);
                        if (password.equals(passFromDB)) {
                            new SessionManagement(context).setSession(email);
                            progressDialog.dismiss();
                            context.startActivity(new Intent(context, DashboardActivity.class));
                            activity.finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
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

    public static void deleteFile(final Activity activity, final String file_key, final String file_identifier) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Deleting File...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            email_identifier = new SessionManagement(activity).getEmailIdentifier();
            Task<Void> task = storageReference.child(file_key).delete();
            task.addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReference.child(email_identifier).child(FILES_KEY).child(file_identifier)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(activity, "File Deleted Successfully", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            activity.finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(activity, "Failed to Delete File", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void deleteFile(final Context context, final String file_key, final String file_identifier) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting File...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            email_identifier = new SessionManagement(context).getEmailIdentifier();
            Task<Void> task = storageReference.child(file_key).delete();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReference.child(email_identifier).child(FILES_KEY).child(file_identifier)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "File Deleted Successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(context, "Failed to Delete File", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void uploadFile(final Context context, final String file_icon_uri, final String file_name, final String file_extension, final String file_type, final Uri file_uri, final String file_identifier, final String file_size) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Checking Database...");
        progressDialog.show();

        try {
            email_identifier = new SessionManagement(context).getEmailIdentifier();

            DatabaseReference childReference = databaseReference.child(email_identifier).child(FILES_KEY).child(file_identifier);
            Query checkuser = childReference.orderByChild("file_name");
            checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(context, "File Already Exists", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        final CustomProgressbar pbar = new CustomProgressbar(context);
                        final String file_storage_key = System.currentTimeMillis() + "";
                        final UploadTask uploadTask = storageReference.child(file_storage_key).putFile(file_uri);
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
                                        final UserFile userFile = new UserFile(file_icon_uri, file_name, file_extension, file_type, uri.toString(), file_size, file_identifier, file_storage_key);
                                        databaseReference.child(email_identifier).child(FILES_KEY).child(file_identifier).setValue(userFile);
                                        Toast.makeText(context, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                pbar.dismissDialog();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, "Upload Cancelled " + databaseError, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void renameFile(final Activity activity, final String new_file_name, final String new_file_id, final String old_file_id) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Renaming File Name ...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            DatabaseReference childReference = databaseReference.child(email_identifier).child(FILES_KEY).child(old_file_id);
            childReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final UserFile userFile = dataSnapshot.getValue(UserFile.class);
                        userFile.setFile_key(new_file_id);
                        userFile.setFile_name(new_file_name);
                        Task<Void> task = databaseReference.child(email_identifier).child(FILES_KEY).child(new_file_id).setValue(userFile);
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(email_identifier).child(FILES_KEY).child(old_file_id).setValue(null);
                                Intent it = new Intent(activity, OnlineFileViewer.class);
                                it.putExtra("USER_FILE", userFile);
                                progressDialog.dismiss();
                                activity.finish();
                                activity.startActivity(it);
                                Toast.makeText(activity, "File Renamed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void createNotes(final Context context) {
        try {

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

    public static void deleteNotes(final Context context) {
        try {

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }
}
