package com.reverender.r95.ui.apps

/** VECTOR DREAMS // 1989 content, copied from the website. */
internal class VdBuilding(val id: String, val name: String, val icon: String, val base: Double, val rate: Double, val desc: String)
internal class VdUpgrade(val id: String, val name: String, val desc: String, val cost: Double, val effect: String, val target: String?, val mult: Double)
internal class VdPrestige(val id: String, val name: String, val desc: String, val base: Int, val max: Int, val icon: String)

internal object VdData {
    val buildings = listOf(
        VdBuilding("cursor", "Vector Cursor", "⌁", 12.0, 0.12, "Tiny drones trace the first neon lines."),
        VdBuilding("sign", "Neon Sign Foundry", "▱", 90.0, 1.1, "Factories forge glowing polygon signs."),
        VdBuilding("grid", "Grid Generator", "▦", 650.0, 8.0, "Extends the infinite perspective grid."),
        VdBuilding("sculptor", "Polygon Sculptor", "◇", 4800.0, 48.0, "Carves crystalline vector geometry."),
        VdBuilding("arcade", "Outrun Arcade", "▣", 36000.0, 290.0, "Players feed the city with high scores."),
        VdBuilding("tower", "Synthwave Tower", "△", 280000.0, 1800.0, "Broadcasts energy across the skyline."),
        VdBuilding("portal", "Vapor Portal", "◉", 2200000.0, 11500.0, "Connects distant polygon dimensions."),
        VdBuilding("sun", "Artificial Sunset", "☼", 18000000.0, 78000.0, "Bottles the last light of 1989."),
        VdBuilding("dimension", "Fractal Dimension", "✧", 150000000.0, 560000.0, "Repeats the city into infinity."),
        VdBuilding("core", "The Reverender Core", "◈", 1300000000.0, 4200000.0, "Turns the whole desktop into a reactor."),
        VdBuilding("hologram", "Hologram District", "▧", 11000000000.0, 32000000.0, "Projects entire neon neighbourhoods into the skyline."),
        VdBuilding("soundwave", "Soundwave Reactor", "♫", 95000000000.0, 260000000.0, "Converts vaporwave frequencies into raw VECTRA."),
        VdBuilding("skybridge", "Skyline Hyperbridge", "⌁", 850000000000.0, 2200000000.0, "Links distant city sectors through luminous geometry."),
        VdBuilding("dreamforge", "Dreamforge", "♢", 7800000000000.0, 19000000000.0, "Manufactures solid structures from collective dreams."),
        VdBuilding("nebulacore", "Nebula Core", "✺", 72000000000000.0, 170000000000.0, "Harvests energy from the neon clouds above the grid."),
        VdBuilding("timeloop", "1989 Time Loop", "⟳", 680000000000000.0, 1500000000000.0, "Repeats the golden moment and compounds its output."),
        VdBuilding("omnigrid", "Omni-Grid", "▦", 6500000000000000.0, 13500000000000.0, "Extends the city grid beyond measurable dimensions."),
        VdBuilding("singularity", "VECTRA Singularity", "✹", 62000000000000000.0, 120000000000000.0, "A living polygon engine powering an endless synthwave universe."),
    )
    val upgrades = listOf(
        VdUpgrade("pulse2", "Overclocked Pulse", "Harvest pulse power ×2", 120.0, "click", null, 2.00),
        VdUpgrade("pulse5", "Laser-Touch Interface", "Harvest pulse power ×5", 8500.0, "click", null, 5.00),
        VdUpgrade("cursor2", "Ghost Cursor Protocol", "Vector Cursors produce ×2", 500.0, "building", "cursor", 2.00),
        VdUpgrade("grid2", "Infinite Grid Lines", "Grid Generators produce ×2", 16000.0, "building", "grid", 2.00),
        VdUpgrade("sculpt2", "Prismatic Alloy", "Polygon Sculptors produce ×2", 90000.0, "building", "sculptor", 2.00),
        VdUpgrade("arcade2", "CRT Score Boost", "Outrun Arcades produce ×2", 700000.0, "building", "arcade", 2.00),
        VdUpgrade("tower2", "Skyline Resonance", "Synthwave Towers produce ×2", 5000000.0, "building", "tower", 2.00),
        VdUpgrade("portal2", "Dimensional Tuning", "Vapor Portals produce ×2", 42000000.0, "building", "portal", 2.00),
        VdUpgrade("sun2", "Eternal Sunset Array", "Artificial Sunsets produce ×2", 360000000.0, "building", "sun", 2.00),
        VdUpgrade("dimension2", "Recursive Geometry", "Fractal Dimensions produce ×2", 3200000000.0, "building", "dimension", 2.00),
        VdUpgrade("core2", "Desktop Reactor Link", "Reverender Cores produce ×2", 28000000000.0, "building", "core", 2.00),
        VdUpgrade("hologram2", "Holographic Density", "Hologram Districts produce ×2", 240000000000.0, "building", "hologram", 2.00),
        VdUpgrade("soundwave2", "Bassline Harmonics", "Soundwave Reactors produce ×2", 2100000000000.0, "building", "soundwave", 2.00),
        VdUpgrade("skybridge2", "Hyperbridge Overclock", "Skyline Hyperbridges produce ×2", 19000000000000.0, "building", "skybridge", 2.00),
        VdUpgrade("dreamforge2", "Lucid Manufacturing", "Dreamforges produce ×2", 175000000000000.0, "building", "dreamforge", 2.00),
        VdUpgrade("nebula2", "Nebula Compression", "Nebula Cores produce ×2", 1600000000000000.0, "building", "nebulacore", 2.00),
        VdUpgrade("timeloop2", "Temporal Feedback", "1989 Time Loops produce ×2", 15000000000000000.0, "building", "timeloop", 2.00),
        VdUpgrade("omnigrid2", "Infinite Coordinates", "Omni-Grids produce ×2", 140000000000000000.0, "building", "omnigrid", 2.00),
        VdUpgrade("singularity2", "Absolute Vector State", "VECTRA Singularities produce ×2", 1300000000000000000.0, "building", "singularity", 2.00),
        VdUpgrade("click10", "Quantum Touchscreen", "Harvest pulse power ×10", 850000.0, "click", null, 10.00),
        VdUpgrade("click25", "Neon Fingerprint Matrix", "Harvest pulse power ×25", 65000000.0, "click", null, 25.00),
        VdUpgrade("all1", "Chromatic Overdrive", "All structures produce 25% more", 75000.0, "all", null, 1.25),
        VdUpgrade("all2", "Neon Singularity", "All structures produce 75% more", 12000000.0, "all", null, 1.75),
        VdUpgrade("all3", "1989 Forever", "All structures produce ×3", 900000000.0, "all", null, 3.00),
        VdUpgrade("all4", "Vaporwave Ascension", "All structures produce ×2", 80000000000.0, "all", null, 2.00),
        VdUpgrade("all5", "Reality Render Override", "All structures produce ×5", 7500000000000.0, "all", null, 5.00),
        VdUpgrade("all6", "Infinite Summer", "All structures produce ×10", 700000000000000.0, "all", null, 10.00),
    )
    val prestige = listOf(
        VdPrestige("echo", "Echo Resonance", "+25% permanent production per rank", 2, 20, "◉"),
        VdPrestige("memory", "Memory Lattice", "Start each prestige with bonus VECTRA", 5, 10, "▦"),
        VdPrestige("touch", "Quantum Fingerprint", "Double core click power per rank", 8, 10, "ϟ"),
        VdPrestige("offline", "Timefold Storage", "+6 hours of offline progress per rank", 12, 10, "⌛"),
        VdPrestige("architect", "Architect Protocol", "+50% permanent global production per rank", 20, 10, "◇"),
        VdPrestige("legacy", "REVERENDER Legacy", "+18% permanent VECTRA production per rank", 28, 15, "R"),
        VdPrestige("dreamxp", "Dream XP Matrix", "+30% XP earned from all activities per rank", 16, 15, "✧"),
        VdPrestige("economy", "Neon Economy", "Building costs reduced by 5% per rank", 24, 10, "＄"),
        VdPrestige("overclock", "Core Overclock", "+50% core click power per rank", 18, 15, "ϟ"),
        VdPrestige("echoYield", "Echo Yield Protocol", "+10% Echo Shards from milestone and Prestige rewards per rank", 8, 10, "✦"),
        VdPrestige("afterhours", "Afterhours Continuity", "+2 additional hours of offline progress per rank", 22, 10, "☾"),
        VdPrestige("synth", "Synthwave Harmonics", "+20% production from all city structures per rank", 32, 15, "♫"),
    )
}
