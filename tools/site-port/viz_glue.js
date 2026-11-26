// The visualizer half of the site's animate(): same order, same arguments.
function rrVizFrame(y, d, sb, sm, sh, cs, c) {
  renderHypercubeVisualizer(y, sb, sm, c);
  renderWaterfallVisualizer(sb, c);
  renderTunnelVisualizer(y, d, sb, sm, c);
  renderPlasmaVisualizer(y, sb, sh, c);
  renderLissajousVisualizer(y, sb, sm, c);
  renderCyberCanyonVisualizer(y, cs, sb, c);
  renderMatrixVisualizer(y, sb, sh, c);
  renderRibbonVisualizer(y, sb, sm, c);
  renderNewVisualizers(y, sb, sm, sh, c);
  renderVuMeterVisualizer(sb, sm, sh, c);
  renderEqVisualizer(y, sb, c);
  renderFxVisualizer(y, sb, sm, c);
  renderCassetteVisualizer(y, d, sb, sm, sh, c);
}
function rrVizTheme(key) { return THEMES[key] || THEMES.vaporwave; }
function rrVizSetMesh(m) { setMeshType(m); }
