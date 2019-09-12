package com.fpliu.newton.ui.statusbar

import android.os.Build
import android.text.TextUtils

import com.fpliu.newton.util.getprop
import java.util.*

object OSUtils {

    private const val ROM_MIUI = "MIUI"
    private const val ROM_EMUI = "EMUI"
    private const val ROM_FLYME = "FLYME"
    private const val ROM_OPPO = "OPPO"
    private const val ROM_SMARTISAN = "SMARTISAN"
    private const val ROM_VIVO = "VIVO"
    private const val ROM_QIKU = "QIKU"

    private const val KEY_VERSION_MIUI = "ro.miui.ui.version.name"
    private const val KEY_VERSION_EMUI = "ro.build.version.emui"
    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"
    private const val KEY_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"

    private var sName: String? = null

    val isEmui: Boolean
        get() = check(ROM_EMUI)

    val isMiui: Boolean
        get() = check(ROM_MIUI)

    val isVivo: Boolean
        get() = check(ROM_VIVO)

    val isOppo: Boolean
        get() = check(ROM_OPPO)

    val isFlyme: Boolean
        get() = check(ROM_FLYME)

    val is360: Boolean
        get() = check(ROM_QIKU) || check("360")

    val isSmartisan: Boolean
        get() = check(ROM_SMARTISAN)

    fun check(rom: String): Boolean {
        if (sName != null) {
            return sName == rom
        }

        if (!TextUtils.isEmpty(getprop(KEY_VERSION_MIUI))) {
            sName = ROM_MIUI
        } else if (!TextUtils.isEmpty(getprop(KEY_VERSION_EMUI))) {
            sName = ROM_EMUI
        } else if (!TextUtils.isEmpty(getprop(KEY_VERSION_OPPO))) {
            sName = ROM_OPPO
        } else if (!TextUtils.isEmpty(getprop(KEY_VERSION_VIVO))) {
            sName = ROM_VIVO
        } else if (!TextUtils.isEmpty(getprop(KEY_VERSION_SMARTISAN))) {
            sName = ROM_SMARTISAN
        } else {
            if (Build.DISPLAY.toUpperCase(Locale.ENGLISH).contains(ROM_FLYME)) {
                sName = ROM_FLYME
            } else {
                sName = Build.MANUFACTURER.toUpperCase(Locale.ENGLISH)
            }
        }
        return sName == rom
    }
}
