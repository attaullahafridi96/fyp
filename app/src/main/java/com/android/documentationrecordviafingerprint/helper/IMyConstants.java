
package com.android.documentationrecordviafingerprint.helper;

public interface IMyConstants {
    String FILE_TYPE_DOCS = "doc";
    String FILE_TYPE_IMAGE = "image";
    String FILE_TYPE_NOTES = "notes";

    String FILE_EXTENSION_PDF = ".pdf";
    String FILE_EXTENSION_TEXT = ".txt";
    String FILE_EXTENSION_WORD = ".docx";

    String EXTRA_USER_NOTES = "USER_NOTES";
    String EXTRA_USER_FILE = "USER_FILE";

    String NO_INTERNET_CONNECTION = "No internet connection";
    String INTERNET_CONNECTED = "Internet connected";

    ////////////Database Keys or IDs//////////////////
    String USER_KEY_FIRST_NAME = "firstName";
    String USER_KEY_LAST_NAME = "lastName";
    String USER_KEY_EMAIL = "email";
    String USER_KEY_PASSWORD = "password";
    /////////////////////////////////////////////////

    ////////////Firebase Uploads Keys//////////////////
    String KEY_TITLE = "title";
    String KEY_TYPE = "type";
    String KEY_DATE_UPLOAD = "dateUpload";
    String KEY_FILE_STORAGE_ID = "fileStorageId";
    /////////////////////////////////////////////////

    ////////////Firebase IDs//////////////////
    String ID_FILES = "files";
    String ID_NOTES = "notes";
    /////////////////////////////////////////////////

    String[] SUPPORTED_FILE_EXTENSIONS = {
            "pdf", "rtf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "csv", "txt", "jpeg", "jpg", "png",
            "gif", "bmp"
    };

    String FILE_ICON_URI_TXT = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Ftxt_96px.png?alt=media&amp;token=0a020731-8a35-4737-8900-2af40a5b8b6c";
    String FILE_ICON_URI_PDF = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fpdf_96px.png?alt=media&amp;token=33ae2d52-7b4d-40b3-983f-00ebfd7410b3";
    String FILE_ICON_URI_WORD = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fword_96px.png?alt=media&amp;token=bca25a91-c60d-461f-ba2e-1d526baa3c5a";
    String FILE_ICON_URI_POWERPOINT = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fpowerpoint_96px.png?alt=media&amp;token=5ad9745a-120b-47fd-a908-df939bb665ab";
    String FILE_ICON_URI_EXCEL = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fexcel_96px.png?alt=media&amp;token=a10b4a20-a73c-42b2-accb-b098be9da824";
    String FILE_ICON_URI_RTF = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Frtf_96px.png?alt=media&amp;token=2fa49bbb-d4a8-4d4b-9aba-5d31d53e4082";
    //String FILE_ICON_URI_NOTES = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fnote_96px.png?alt=media&amp;token=e5fe1995-eeb6-4ee9-b5ad-b0855a142a18";
    String FILE_ICON_URI_JPEG = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fjpg_96px.png?alt=media&amp;token=0a1c3294-36d4-4ed5-9c13-2aa153bc370e";
    String FILE_ICON_URI_PNG = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fpng_96px.png?alt=media&amp;token=6c9b7dba-afb6-477d-9c2b-83e54bc903e9";
    String FILE_ICON_URI_GIF = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fgif_96px.png?alt=media&amp;token=50709bc9-6ad1-4d6a-9ad3-7f7449715c3e";
    String FILE_ICON_URI_BMP = "https://firebasestorage.googleapis.com/v0/b/notesviafingerprint.appspot.com/o/icons%2Fimage_96px.png?alt=media&amp;token=93251f1b-73eb-4047-8f41-70050c80cbc6";
}
