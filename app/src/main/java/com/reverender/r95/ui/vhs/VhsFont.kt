package com.reverender.r95.ui.vhs

/**
 * The VCR on-screen-display font: a 5×7 dot matrix like the menus of 1980s video recorders,
 * with the transport and menu symbols those menus used. Lower case is shown as capitals.
 */
object VhsFont {
    const val W = 5
    const val H = 7
    private val glyphs: Map<Char, IntArray> = mapOf(
        'A' to intArrayOf(14, 17, 17, 31, 17, 17, 17),
        'B' to intArrayOf(30, 17, 17, 30, 17, 17, 30),
        'C' to intArrayOf(14, 17, 16, 16, 16, 17, 14),
        'D' to intArrayOf(30, 17, 17, 17, 17, 17, 30),
        'E' to intArrayOf(31, 16, 16, 30, 16, 16, 31),
        'F' to intArrayOf(31, 16, 16, 30, 16, 16, 16),
        'G' to intArrayOf(14, 17, 16, 23, 17, 17, 15),
        'H' to intArrayOf(17, 17, 17, 31, 17, 17, 17),
        'I' to intArrayOf(14, 4, 4, 4, 4, 4, 14),
        'J' to intArrayOf(7, 2, 2, 2, 2, 18, 12),
        'K' to intArrayOf(17, 18, 20, 24, 20, 18, 17),
        'L' to intArrayOf(16, 16, 16, 16, 16, 16, 31),
        'M' to intArrayOf(17, 27, 21, 21, 17, 17, 17),
        'N' to intArrayOf(17, 17, 25, 21, 19, 17, 17),
        'O' to intArrayOf(14, 17, 17, 17, 17, 17, 14),
        'P' to intArrayOf(30, 17, 17, 30, 16, 16, 16),
        'Q' to intArrayOf(14, 17, 17, 17, 21, 18, 13),
        'R' to intArrayOf(30, 17, 17, 30, 20, 18, 17),
        'S' to intArrayOf(15, 16, 16, 14, 1, 1, 30),
        'T' to intArrayOf(31, 4, 4, 4, 4, 4, 4),
        'U' to intArrayOf(17, 17, 17, 17, 17, 17, 14),
        'V' to intArrayOf(17, 17, 17, 17, 17, 10, 4),
        'W' to intArrayOf(17, 17, 17, 21, 21, 21, 10),
        'X' to intArrayOf(17, 17, 10, 4, 10, 17, 17),
        'Y' to intArrayOf(17, 17, 10, 4, 4, 4, 4),
        'Z' to intArrayOf(31, 1, 2, 4, 8, 16, 31),
        '0' to intArrayOf(14, 17, 19, 21, 25, 17, 14),
        '1' to intArrayOf(4, 12, 4, 4, 4, 4, 14),
        '2' to intArrayOf(14, 17, 1, 2, 4, 8, 31),
        '3' to intArrayOf(31, 2, 4, 2, 1, 17, 14),
        '4' to intArrayOf(2, 6, 10, 18, 31, 2, 2),
        '5' to intArrayOf(31, 16, 30, 1, 1, 17, 14),
        '6' to intArrayOf(6, 8, 16, 30, 17, 17, 14),
        '7' to intArrayOf(31, 1, 2, 4, 8, 8, 8),
        '8' to intArrayOf(14, 17, 17, 14, 17, 17, 14),
        '9' to intArrayOf(14, 17, 17, 15, 1, 2, 12),
        '&' to intArrayOf(12, 18, 20, 8, 21, 18, 13),
        '-' to intArrayOf(0, 0, 0, 31, 0, 0, 0),
        ':' to intArrayOf(0, 12, 12, 0, 12, 12, 0),
        '.' to intArrayOf(0, 0, 0, 0, 0, 12, 12),
        '+' to intArrayOf(0, 4, 4, 31, 4, 4, 0),
        '=' to intArrayOf(0, 0, 31, 0, 31, 0, 0),
        '/' to intArrayOf(0, 1, 2, 4, 8, 16, 0),
        '?' to intArrayOf(14, 17, 1, 2, 4, 0, 4),
        '!' to intArrayOf(4, 4, 4, 4, 4, 0, 4),
        '(' to intArrayOf(2, 4, 8, 8, 8, 4, 2),
        ')' to intArrayOf(8, 4, 2, 2, 2, 4, 8),
        '[' to intArrayOf(14, 8, 8, 8, 8, 8, 14),
        ']' to intArrayOf(14, 2, 2, 2, 2, 2, 14),
        ',' to intArrayOf(0, 0, 0, 0, 12, 4, 8),
        '\'' to intArrayOf(4, 4, 8, 0, 0, 0, 0),
        '%' to intArrayOf(24, 25, 2, 4, 8, 19, 3),
        '#' to intArrayOf(10, 10, 31, 10, 31, 10, 10),
        '_' to intArrayOf(0, 0, 0, 0, 0, 0, 31),
        '"' to intArrayOf(10, 10, 0, 0, 0, 0, 0),
        '*' to intArrayOf(0, 21, 14, 31, 14, 21, 0),
        '<' to intArrayOf(2, 4, 8, 16, 8, 4, 2),
        '>' to intArrayOf(8, 4, 2, 1, 2, 4, 8),
        '▶' to intArrayOf(16, 24, 28, 30, 28, 24, 16),
        '◀' to intArrayOf(1, 3, 7, 15, 7, 3, 1),
        '▲' to intArrayOf(0, 0, 4, 14, 31, 0, 0),
        '▼' to intArrayOf(0, 0, 31, 14, 4, 0, 0),
        '■' to intArrayOf(0, 31, 31, 31, 31, 31, 0),
        '❚' to intArrayOf(0, 27, 27, 27, 27, 27, 0),
        '●' to intArrayOf(0, 14, 31, 31, 31, 14, 0),
        '★' to intArrayOf(4, 4, 31, 14, 10, 17, 0),
        '♪' to intArrayOf(7, 5, 4, 4, 28, 28, 0),
        '◆' to intArrayOf(0, 4, 14, 31, 14, 4, 0),
        '→' to intArrayOf(0, 2, 1, 31, 1, 2, 0),
        '←' to intArrayOf(0, 8, 16, 31, 16, 8, 0),
        '█' to intArrayOf(31, 31, 31, 31, 31, 31, 31),
        '@' to intArrayOf(14, 17, 23, 21, 23, 16, 14),
        '$' to intArrayOf(4, 15, 20, 14, 5, 30, 4),
        ';' to intArrayOf(0, 12, 12, 0, 12, 4, 8),
        '|' to intArrayOf(4, 4, 4, 4, 4, 4, 4),
        '~' to intArrayOf(0, 0, 8, 21, 2, 0, 0),
        '^' to intArrayOf(4, 10, 17, 0, 0, 0, 0),
        '{' to intArrayOf(6, 8, 8, 16, 8, 8, 6),
        '}' to intArrayOf(12, 2, 2, 1, 2, 2, 12),
        '\\' to intArrayOf(0, 16, 8, 4, 2, 1, 0),
    )
    private val alias = mapOf(
        '⏸' to '❚', '⏹' to '■', '⏺' to '●', '⏏' to '▲', '…' to '.', '•' to '●', '×' to 'X',
        // typography radio stations and song titles use: dashes, dots, curly quotes
        '—' to '-', '–' to '-', '‐' to '-', '‑' to '-', '−' to '-', '·' to '.', '∙' to '.',
        '’' to '\'', '‘' to '\'', '`' to '\'', '´' to '\'', '“' to '"', '”' to '"', '„' to '"', '«' to '<', '»' to '>',
        '♫' to '♪', '♩' to '♪', '►' to '▶', '◄' to '◀', '¿' to '?', '¡' to '!', 'ß' to 'S', 'Æ' to 'A', 'Ø' to 'O', 'Œ' to 'O',
    )
    private val cache = HashMap<Char, IntArray?>()

    /**
     * Rows of the glyph, bit 4 = leftmost column; null for a blank. Characters the font doesn't have fall back
     * to their plain letter (É → E, Ñ → N, …) or a look-alike; anything else (emoji, flags, other scripts)
     * is left blank instead of showing as "?".
     */
    fun rows(c: Char): IntArray? {
        val u = c.uppercaseChar()
        glyphs[u]?.let { return it }
        alias[u]?.let { a -> return glyphs[a] }
        if (u == ' ') return null
        return cache.getOrPut(u) {
            val base = java.text.Normalizer.normalize(u.toString(), java.text.Normalizer.Form.NFD).firstOrNull()?.uppercaseChar()
            if (base != null && base != u) glyphs[base] else null
        }
    }

    /** every glyph the font has, for the search keyboard and tests */
    val chars: Set<Char> get() = glyphs.keys
}
