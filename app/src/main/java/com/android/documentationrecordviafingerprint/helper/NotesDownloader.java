package com.android.documentationrecordviafingerprint.helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.android.documentationrecordviafingerprint.model.IMyConstants;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomProgressDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomToast;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public final class NotesDownloader implements IMyConstants {

    public static void saveNotes(final Context context, final String notesTitle, final String notesData) {
        if (notesTitle.isEmpty()) {
            new CustomMsgDialog(context, "Notes title Empty!", "Can not leave Notes title empty");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save File As")
                .setSingleChoiceItems(new String[]{"Text file", "PDF file", "Word file"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected_item) {
                        switch (selected_item) {
                            case 0:
                                saveFileOnDevice(context, notesTitle, notesData, FILE_EXTENSION_TEXT);
                                break;
                            case 1:
                                saveFileOnDevice(context, notesTitle, notesData, FILE_EXTENSION_PDF);
                                break;
                            case 2:
                                saveFileOnDevice(context, notesTitle, notesData, FILE_EXTENSION_WORD);
                        }
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private static void saveFileOnDevice(final Context context, final String notesTitle,
                                         final String notesData, final String file_extension) {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
            if (!dir.exists())
                dir.mkdirs();
            String full_file_name = notesTitle.trim() + file_extension;
            File file = new File(dir, full_file_name);
            switch (file_extension) {
                case FILE_EXTENSION_TEXT:
                    FileWriter writer = new FileWriter(file);
                    writer.append(notesData);
                    writer.flush();
                    writer.close();
                    new CustomMsgDialog(context, "File Saved",
                            "Saved as " + full_file_name + " in Downloads folder");
                    break;
                case FILE_EXTENSION_PDF:
                    saveAsPdf(context, file, notesData);
                    break;
                case FILE_EXTENSION_WORD:
                    saveAsMsWord(context, dir, full_file_name, notesData);
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private static void saveAsPdf(final Context context, final File file, final String data) {
        try {
            com.itextpdf.text.Document mDoc = new com.itextpdf.text.Document();
            PdfWriter.getInstance(mDoc, new FileOutputStream(file));
            mDoc.open();
            mDoc.add(new Paragraph(data));
            mDoc.close();
            new CustomMsgDialog(context, "File Saved",
                    "Saved as " + file.getName() + " in Downloads folder");
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static void saveAsMsWord(final Context context, final File dir, final String full_file_name, final String file_data) {
        new AsyncTask<Void, Void, Boolean>() {
            private final CustomProgressDialog customProgressDialog = new CustomProgressDialog(context, "Processing . . .");

            @Override
            protected void onPreExecute() {
                customProgressDialog.showDialog();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    com.aspose.words.Document doc = new Document();
                    DocumentBuilder documentBuilder = new DocumentBuilder(doc);
                    documentBuilder.write(file_data);
                    doc.save(dir + "/" + full_file_name);
                    return true;
                } catch (final Exception e) {
                    CustomToast.makeToast(context, "Error: " + e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                customProgressDialog.dismissDialog();
                if (result) {
                    new CustomMsgDialog(context, "File Saved",
                            "Saved as " + full_file_name + " in Downloads folder");
                } else {
                    new CustomMsgDialog(context, "Saved Failed", "Unable to Save file");
                }
            }
        }.execute();
    }
}
