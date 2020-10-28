package com.android.documentationrecordviafingerprint.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.documentationrecordviafingerprint.main.DashboardActivity;
import com.android.documentationrecordviafingerprint.model.DB;
import com.android.documentationrecordviafingerprint.model.User;
import com.android.documentationrecordviafingerprint.model.UserDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public final class FirebaseController {
    private final DatabaseReference parent_node;
    private final FirebaseStorage storage;
    private String email_identifier;
    private final Context context;
    private final ProgressDialog progressDialog;

    public FirebaseController(Context context) {
        parent_node = DB.getFirstNodeReference();
        storage = FirebaseStorage.getInstance();
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
    }

    public void createNewUserAccount(final User user, final Activity activity) {
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        Query checkDuplicateAcc = parent_node.orderByChild("email").equalTo(user.getEmail());

        char[] type = {'-', '#', '$', '[', ']', '@', '.'};
        for (char c : type) {
            email_identifier = user.getEmail().replace(c, '_');
        }

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
                        parent_node.child(email_identifier).setValue(user);
                        progressDialog.dismiss();
                        Toast.makeText(context, "New Account Created Successfully", Toast.LENGTH_SHORT).show();
                        SessionManagement user_session = new SessionManagement(context);
                        user_session.setSession(user.getEmail());
                        context.startActivity(new Intent(context, DashboardActivity.class));
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
    }

    public void getFullName(final TextView textView) {
        email_identifier = new SessionManagement(context).getEmailIdentifier();
        Query checkAccount = parent_node.child(email_identifier);
        checkAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String first_name = dataSnapshot.child("first_name").getValue(String.class);
                    String last_name = dataSnapshot.child("last_name").getValue(String.class);
                    textView.setText(first_name + " " + last_name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void verifyLoginCredentials(final String email, final String password, final Activity activity) {
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        Query checkAccount = parent_node.orderByChild("email").equalTo(email);

        char[] type = {'-', '#', '$', '[', ']', '@', '.'};
        for (char c : type) {
            email_identifier = email.replace(c, '_');
        }

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
    }

    public void uploadFile(final String file_icon_uri, final String file_name, final String file_type, final Uri file_uri, final String file_identifier, final String file_size) {
        progressDialog.setTitle("Checking Database...");
        progressDialog.show();

        email_identifier = new SessionManagement(context).getEmailIdentifier();

        DatabaseReference childReference = parent_node.child(email_identifier).child("files").child(file_identifier);
        Query checkuser = childReference.orderByChild("file_name");
        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(context, "File Already Exists", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    final ProgressDialog prodialog = new ProgressDialog(context);
                    prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    prodialog.setTitle("Uploading File...");
                    prodialog.setProgress(0);
                    prodialog.show();
                    String random_file_name = System.currentTimeMillis() + "";
                    StorageReference storageReference = storage.getReference();
                    UploadTask uploadTask = storageReference.child("Uploads").child(random_file_name).putFile(file_uri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    final UserDocument userDocument = new UserDocument(file_icon_uri, file_name, file_type, uri.toString(), file_size);
                                    parent_node.child(email_identifier).child("files").child(file_identifier).setValue(userDocument);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Upload Failed ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            prodialog.setProgress(currentProgress);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            prodialog.dismiss();
                            Toast.makeText(context, "File Uploaded and Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Upload Cancelled " + databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }

}
