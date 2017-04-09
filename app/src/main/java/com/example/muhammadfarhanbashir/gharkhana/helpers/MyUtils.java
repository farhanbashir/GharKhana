package com.example.muhammadfarhanbashir.gharkhana.helpers;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.muhammadfarhanbashir.gharkhana.HomeActivity;
import com.example.muhammadfarhanbashir.gharkhana.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;
import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;
/**
 * Created by fbashir on 9/29/2016.
 */

public class MyUtils {
    public void SlideUP(View view, Context context)
    {
//        view.startAnimation(AnimationUtils.loadAnimation(context,
//                R.anim.slide_down));
    }

    public static boolean ifNetworkPresent(Context context)
    {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                result = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                result = true;
            }
        } else {
            // not connected to the internet
            result = false;
        }

        return result;
    }

    public void SlideDown(View view, Context context)
    {
//        view.startAnimation(AnimationUtils.loadAnimation(context,
//                R.anim.slide_up));
    }

    public static String formatMysqlDate(String mDate)
    {
        String parse_date = "";
        SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat writeFormat = new SimpleDateFormat( "dd MMM, yyyy");

        try{
            Date d = readFormat.parse(mDate);
            parse_date = writeFormat.format(d);
        }
        catch (ParseException ex)
        {
            parse_date = mDate;
        }

        return parse_date;
    }

    public static void logout(Context context)
    {
        //fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        for(int i=0; i<fragmentManager.getBackStackEntryCount(); i++)
//        {
//            fragmentManager.popBackStack();
//        }
        SharedPreference.getInstance().clearSharedPreference(context);
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }

    public static void redirectIfLoggedIn(Context context)
    {
//        Gson gson = new Gson();
//        String user = SharedPreference.getInstance().getValue(context, "user");
//        if(!user.equals(""))
//        {
//            LoginBasicClass user_object = gson.fromJson(user, LoginBasicClass.class);
//
//            if(user_object.role_id.equals(context.getResources().getString(R.string.chef_roleid)))
//            {
//                Intent intent = new Intent(context, ChefActivity.class);
//                context.startActivity(intent);
//            }
//            else
//            {
//                Intent intent = new Intent(context, ConsumerActivity.class);
//                context.startActivity(intent);
//            }
//        }
    }

    public static ArrayList<String> removeValue(ArrayList<String> values, String removeValue)
    {
        for(int i=0; i<values.size();i++)
        {
            if(values.get(i).equals(removeValue))
            {
                values.remove(i);
                break;
            }
        }
        return values;
    }

    public static boolean valueExists(ArrayList<String> values, String compareValue)
    {
        for(int i=0; i<values.size();i++)
        {
            if(values.get(i).equals(compareValue))
            {
                return true;
            }
        }

        return false;
    }

    public static void showAlert(Context context, String msg)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        //alertDialogBuilder.setTitle(R.string.alert);

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
//                .setNegativeButton("No",new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,int id) {
//                        // if this button is clicked, just close
//                        // the dialog box and do nothing
//                        dialog.cancel();
//                    }
//                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static void openDialer(Context context, String phone_number)
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:"+phone_number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openMailer(Context context, String msg)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");

        if(!msg.equals(""))
        {
            intent.putExtra(Intent.EXTRA_TEXT, msg);
        }
        //intent.putExtra(Intent.EXTRA_STREAM,pdf_uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent mailer = Intent.createChooser(intent, null);
        context.startActivity(mailer);
    }

    public static void openMap(Context context, String lat, String lng, String label)
    {
        String uri = "geo:0,0?q="+lat+","+lng+"("+label+")";
        //Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lng+"(Google+Sydney)");
        //Uri gmmIntentUri = Uri.parse("geo:0,0?q=-33.8666,151.1957(Google+Sydney)");
        Uri gmmIntentUri = Uri.parse(uri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

    public static void openViewer(Context context, String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public static Uri writeResponseBodyToDisk(ResponseBody body, String name, Uri pdf_uri) {
        try {
            // todo change the file location/name according to your needs
            String[] file_name = name.split("/");

            File path = Environment.getExternalStorageDirectory();
            File futureStudioIconFile = new File(path, file_name[file_name.length -1]);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                pdf_uri = Uri.fromFile(futureStudioIconFile);
                return pdf_uri;
            } catch (IOException e) {
                return pdf_uri;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return pdf_uri;
        }
    }

}
