package com.reverender.r95.ui.win95

import androidx.compose.ui.graphics.Color

/** The site's classic (Win95) CSS variables per theme: :root plus body[data-theme=…] overrides. */
class ClassicVars(
    val winBg: Color,
    val light: Color,
    val midLight: Color,
    val dark: Color,
    val black: Color,
    val text: Color,
    val title1: Color,
    val title2: Color,
    val inactive1: Color,
    val inactive2: Color,
    val cyan: Color,
    val pink: Color,
    val purple: Color,
    /** B&W NOIR applies grayscale(100%) contrast(110%) to the whole page. */
    val noir: Boolean = false,
)

val CLASSIC_THEMES: Map<String, ClassicVars> = mapOf(
    "vaporwave" to ClassicVars(Color(0xFFB8B8B8), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF000066), Color(0xFFB8007A), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFF00F7FF), Color(0xFFFF00A0), Color(0xFF9D00FF)),
    "bw" to ClassicVars(Color(0xFFC4C4C4), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF000000), Color(0xFF444444), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFFFFFFF), Color(0xFFCFCFCF), Color(0xFF888888), noir = true),
    "matrix" to ClassicVars(Color(0xFF9AB09A), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF002B00), Color(0xFF00802B), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFF00FF66), Color(0xFF88FFAA), Color(0xFF008F39)),
    "outrun" to ClassicVars(Color(0xFFC9A18B), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF4A0714), Color(0xFFE04416), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFFFE45E), Color(0xFFFF3B30), Color(0xFFB51D35)),
    "ice" to ClassicVars(Color(0xFFB0C2CC), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF002244), Color(0xFF0077AA), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFAAFFFF), Color(0xFF33BBFF), Color(0xFF0055AA)),
    "sakura" to ClassicVars(Color(0xFFD8BFC8), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF5E0B35), Color(0xFFE05297), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFFFE6F2), Color(0xFFFF4DA6), Color(0xFFA61C5C)),
    "acid" to ClassicVars(Color(0xFFB5C49A), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF1F004D), Color(0xFF66CC00), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFCCFF00), Color(0xFF00FF99), Color(0xFF7700FF)),
    "amber" to ClassicVars(Color(0xFFC2B092), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF331A00), Color(0xFFB35900), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFFFB000), Color(0xFFFF6A00), Color(0xFF803300)),
    "mallsoft" to ClassicVars(Color(0xFFC4D4D0), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF1F5C5C), Color(0xFFE07A9B), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFF7FFFD4), Color(0xFFFF8DA1), Color(0xFF8B72BE)),
    "midnight" to ClassicVars(Color(0xFF9D94B8), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF140024), Color(0xFF990033), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFFF0055), Color(0xFF00E5FF), Color(0xFF6200EA)),
    "synthwave" to ClassicVars(Color(0xFFB9A2D0), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF240044), Color(0xFFB000D6), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFF00F5FF), Color(0xFFFF3CAC), Color(0xFF784BFF)),
    "toxic" to ClassicVars(Color(0xFFAAB89A), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF102B25), Color(0xFF4D6F19), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFBAFF00), Color(0xFF00E5A8), Color(0xFF527F35)),
    "ocean" to ClassicVars(Color(0xFFB8A4D8), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF241044), Color(0xFF6040A8), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFE3B7FF), Color(0xFFFF8EC7), Color(0xFF8A6CFF)),
    "crimson" to ClassicVars(Color(0xFFC99EAA), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF3D0010), Color(0xFFA0002B), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFFFB3C1), Color(0xFFFF1744), Color(0xFF8F0030)),
    "lavender" to ClassicVars(Color(0xFFC7C0DF), Color(0xFFFFFFFF), Color(0xFFDCDCDC), Color(0xFF555555), Color(0xFF000000), Color(0xFF000000), Color(0xFF30205C), Color(0xFF7958B8), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFD9CCFF), Color(0xFFFF9DE2), Color(0xFF9B7BFF)),
    "backrooms" to ClassicVars(Color(0xFFC7BC8F), Color(0xFFF2E8BE), Color(0xFFDFD4A6), Color(0xFF6E6338), Color(0xFF241E09), Color(0xFF1C1704), Color(0xFF4A3C0C), Color(0xFFA88D32), Color(0xFF4A4A4A), Color(0xFF707070), Color(0xFFFFF394), Color(0xFFD4AF37), Color(0xFF8C7322)),
)
