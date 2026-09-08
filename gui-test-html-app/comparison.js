/* Loaded only by the automated runner; the ordinary manual preview stays usable. */
window.visualReady = (async () => {
  const response = await fetch('/case.json');
  if (!response.ok) throw new Error('Failed to load comparison input');
  const view = await response.json();
  document.title = view.id;
  const hostStyle = document.createElement('style');
  hostStyle.textContent = `
    @font-face { font-family: Roboto; src: url('/fonts/Roboto-Regular.ttf'); font-weight: 400; }
    @font-face { font-family: Roboto; src: url('/fonts/Roboto-Light.ttf'); font-weight: 300; }
    @font-face { font-family: Roboto; src: url('/fonts/Roboto-Bold.ttf'); font-weight: 700; }
    html, body { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background: white; }
    #app { width: ${view.width}px; height: ${view.height}px; }
    /* A native Frame is a rooted formatting context: child margins cannot move it. */
    #app > winframe { position: absolute; left: 0; top: 0; }
  `;
  document.head.append(hostStyle);
  const style = document.createElement('style');
  style.textContent = view.css;
  document.head.append(style);
  document.getElementById('app').innerHTML = view.xml;
  await Promise.all([300, 400, 700].map(weight => document.fonts.load(`${weight} 14px Roboto`)));
  await document.fonts.ready;
  for (const weight of [300, 400, 700]) {
    if (!document.fonts.check(`${weight} 14px Roboto`)) throw new Error('Roboto not loaded');
  }
  if (view.scrollId) {
    const target = document.getElementById(view.scrollId);
    if (!target) throw new Error('Missing scroll target ' + view.scrollId);
    target.scrollLeft = view.scrollX;
    target.scrollTop = view.scrollY;
  }
  await new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)));
  return [...document.querySelector('winframe').querySelectorAll('[id]')]
    .concat(document.querySelector('winframe')).map(element => {
      const rect = element.getBoundingClientRect();
      return { id: element.id, x: rect.x, y: rect.y, width: rect.width, height: rect.height,
        clientWidth: element.clientWidth, clientHeight: element.clientHeight,
        scrollWidth: element.scrollWidth, scrollHeight: element.scrollHeight,
        scrollLeft: element.scrollLeft, scrollTop: element.scrollTop };
    });
})();
