Log.d(TAG, "Activando GPS...");
LocationRequest locationRequest = new LocationRequest()
        .setInterval(LOCATION_INTERVAL)
        .setFastestInterval(LOCATION_FAST_INTERVAL)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest);

final Activity activity = getActivity();
if (activity != null) {
    SettingsClient client = LocationServices.getSettingsClient(activity);
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
    task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
        @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }
    });
    task.addOnFailureListener(activity, new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        }
    });
}
