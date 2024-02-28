/**
 * This class is part of the V.I.S.O.R app.
 * Permission helper - Responsible checking and requesting all permissions in a list.
 * Requires "onRequestPermissionsResult" to be passed from the activity class!
 *
 * @version 1.0
 * @since 0/01/2024
 */

package com.matt.visor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Iterator;
import java.util.List;

public class PermissionHelper {

    private static final int PERMISSION_ALL = 1;

    private final Context _context;
    private final List<String> _list;
    private final Callback _callback;

    private boolean _showInfoDialog = true;


    /**
     * Permission helper
     *
     * @param context   The context of the application.
     * @param list      List of permissions to be checked
     * @param callback  Callback
     */
    public PermissionHelper(Context context, List<String> list, Callback callback) {
        _context = context;
        _list = list;
        _callback = callback;
    }


    /**
     * Iterates over the list of permissions, checks for each and if permission is NOT granted, call for the request.
     * If all permissions are granted, onAllPermissionsGranted callback is invoked.
     */
    public void start() {
        Iterator<String> iterator = _list.iterator();
        while(iterator.hasNext()) {
            String permission = iterator.next();

            System.out.println("Checking: " + permission);

            if(!hasPermissions(permission)) {
                System.out.println("Requesting: " + permission);
                requestNewPermission(permission);
                return;
            }
            else {
                iterator.remove();
                System.out.println("Removing: " + permission);
            }
        }

        System.out.println("All permissions are ok");
        _callback.onAllPermissionsGranted();
    }


    /**
     * Callback for the result from requesting permissions
     *
     * @param requestCode The request code passed in requestPermissions
     * @param permissions The requested permissions. Never null.
     * @param grantResults  The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("onRequestPermissionsResult!");

        if(requestCode == PERMISSION_ALL && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission granted: " + permissions[0]);
            start();
            return;
        }

        System.out.println("Permission unknown or denied");
        _callback.onPermissionDenied(permissions[0]);
    }


    /**
     * Checks if specified permission is granted.
     *
     * @param permissions The permission to check.
     * @return True if the permission is granted, false otherwise.
     */
    private boolean hasPermissions(String permissions) {
        if (_context != null && permissions != null) {
            return ActivityCompat.checkSelfPermission(_context, permissions) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }


    /**
     * Requests a permission, if this is called for the first time, also displays infoDialog
     *
     * @param permission name of the permission
     */
    private void requestNewPermission(String permission) {
        if(_showInfoDialog) {
            _showInfoDialog = false;
            showInfoDialog(permission);
        }
        else {
            ActivityCompat.requestPermissions((Activity)_context, new String[]{permission}, PERMISSION_ALL);
        }
    }


    /**
     * Displays info dialog, informing user that the app will require some permissions
     *
     * @param permission name of the permission
     */
    private void showInfoDialog(String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context, R.style.MyDialogTheme);

        builder.setTitle(R.string.dialog_confirm);
        builder.setMessage(R.string.permission_request);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            ActivityCompat.requestPermissions((Activity)_context, new String[]{permission}, PERMISSION_ALL);
        });

        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
            _callback.onPermissionDenied(permission);
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Callback interface
     */
    public interface Callback {
        void onAllPermissionsGranted();
        void onPermissionDenied(String permission);

    }

}
