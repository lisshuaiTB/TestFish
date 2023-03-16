package com.ls.view.fish

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.content.ContextCompat


fun Context.getBitmapFromVectorDrawable( drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth, drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

inline fun Context.fixDensity() {
    val adm: DisplayMetrics = resources.displayMetrics
    //density = px/dp
//        val td = adm.widthPixels / 1920f
    val td = adm.heightPixels / 760f
    LLog.d("fixDensity -- ${adm.widthPixels}  ${adm.heightPixels}  ${adm.density} ${adm.scaledDensity}  td=$td  ")
    // px = dp * (dpi / 160)
    val dpi = (160 * td).toInt()
    //密度
    adm.density = td
    adm.scaledDensity = td

    adm.densityDpi = dpi
}

val Number.dip
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
