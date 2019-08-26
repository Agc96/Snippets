/**
 * Crea una imagen temporal en el directorio reservado para uso de la aplicación.
 * @param context Contexto de la aplicación, generalmente un Activity
 * @return Un archivo temporal creado para guardar una foto, o NULL si es que no se pudo crear.
 */
public static File createImage(Context context) {
    // Formulamos el formato del archivo (sin la extensión)
    SimpleDateFormat now = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    String filename = "RTC_PHOTO_" + now.format(new Date());
    // Obtenemos un directorio reservado para uso de la aplicación
    File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    // Creamos una imagen temporal en el directorio especificado.
    try {
        return File.createTempFile(filename, ".jpg", directory);
    } catch (IOException ex) {
        Log.e(TAG, "Error al crear la imagen", ex);
        return null;
    }
}
