const fs = require("fs");
const path = require("path");

const root = process.cwd();
const outRoot = path.join(root, "docs", "project-structure");
const packagesRoot = path.join(outRoot, "packages");

const roots = [
  "core/src/main/java",
  "core/src/test/java",
  "core.backend/src/main/java",
  "core.backend.lwjgl.nanovg/src/main/java",
  "demo.simple/src/main/java",
  "demo.complex/src/main/java",
  "spinygui/src/main/java",
];

const moduleDescriptions = {
  core: "Core DOM-like node model, CSS parser/style system, layout, events, input, fonts, animation, and platform abstraction.",
  "core.backend": "Renderer backend API shared by concrete rendering implementations.",
  "core.backend.lwjgl.nanovg": "LWJGL/NanoVG renderer implementation for drawing the core scene graph.",
  "demo.simple": "Small launcher-style examples for exercising the aggregate SpinyGUI module.",
  "demo.complex": "GLFW/LWJGL demo harness and NanoVG example runner.",
  spinygui: "Aggregate module that re-exports the core and default backend modules.",
};

const packageDescriptions = new Map(Object.entries({
  "com": "Top-level Java namespace folder for project packages.",
  "com.spinyowl": "SpinyOwl namespace folder.",
  "com.spinyowl.spinygui": "SpinyGUI namespace folder aggregating core, backend, and demo packages.",
  "com.spinyowl.spinygui.core": "Top-level core configuration and shared entry points for the GUI engine.",
  "com.spinyowl.spinygui.core.backend": "Backend namespace folder for renderer APIs and implementations.",
  "com.spinyowl.spinygui.core.animation": "Frame-time animation contracts and a simple animator loop.",
  "com.spinyowl.spinygui.core.clipboard": "Clipboard abstraction used by platform integrations.",
  "com.spinyowl.spinygui.core.cursor": "Cursor model and cursor service abstraction.",
  "com.spinyowl.spinygui.core.event": "Application-level events emitted to nodes and event targets.",
  "com.spinyowl.spinygui.core.event.listener": "Generic event listener contract for application events.",
  "com.spinyowl.spinygui.core.event.processor": "Dispatch logic for routing application events to node listeners.",
  "com.spinyowl.spinygui.core.font": "CSS-like font value objects: family, size, stretch, style, and weight.",
  "com.spinyowl.spinygui.core.image": "Image abstraction used by style and rendering layers.",
  "com.spinyowl.spinygui.core.input": "Input domain model for keyboard, mouse, shortcuts, and user-facing key mappings.",
  "com.spinyowl.spinygui.core.input.impl": "Default mutable implementations of input services.",
  "com.spinyowl.spinygui.core.layout": "Layout contracts, layout context, and text/element layout interfaces.",
  "com.spinyowl.spinygui.core.layout.impl": "Concrete layout algorithms and utilities for block, flex, none, text, and layout tree updates.",
  "com.spinyowl.spinygui.core.node": "DOM-like node hierarchy: frames, elements, empty elements, text nodes, and builders.",
  "com.spinyowl.spinygui.core.node.intersection": "Hit-testing strategy objects for node intersection checks.",
  "com.spinyowl.spinygui.core.node.layout": "Box-model geometry value objects used by layout and rendering.",
  "com.spinyowl.spinygui.core.parser": "Parser interfaces for HTML-like node trees and stylesheets.",
  "com.spinyowl.spinygui.core.parser.impl": "Default parser implementations and parser factory code.",
  "com.spinyowl.spinygui.core.parser.impl.css": "CSS parser namespace containing generated ANTLR artifacts and handwritten semantic visitors.",
  "com.spinyowl.spinygui.core.parser.impl.css.antlr": "Generated ANTLR CSS3 lexer/parser/listener/visitor artifacts. Regenerate from the grammar instead of hand-editing.",
  "com.spinyowl.spinygui.core.parser.impl.css.visitor": "ANTLR visitors that convert CSS parse trees into stylesheet, selector, declaration, and term model objects.",
  "com.spinyowl.spinygui.core.style": "Resolved style state applied to nodes after rule matching and property conversion.",
  "com.spinyowl.spinygui.core.style.manager": "Style manager contract and implementation for applying stylesheets to node trees.",
  "com.spinyowl.spinygui.core.style.stylesheet": "CSS stylesheet domain model: properties, rulesets, declarations, terms, specificity, and provider registry.",
  "com.spinyowl.spinygui.core.style.stylesheet.annotation": "Annotations used by stylesheet property providers.",
  "com.spinyowl.spinygui.core.style.stylesheet.atrule": "CSS at-rule model objects.",
  "com.spinyowl.spinygui.core.style.stylesheet.impl": "Default property-store implementation and provider scanner integration.",
  "com.spinyowl.spinygui.core.style.stylesheet.property": "CSS property providers that parse declarations into typed style values.",
  "com.spinyowl.spinygui.core.style.stylesheet.selector": "Selector contracts and base selector types.",
  "com.spinyowl.spinygui.core.style.stylesheet.selector.combinator": "Combinator selectors for descendant, child, sibling, adjacent sibling, and compound matching.",
  "com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoclass": "Pseudo-class selector implementations.",
  "com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement": "Pseudo-element selector implementations.",
  "com.spinyowl.spinygui.core.style.stylesheet.selector.simple": "Simple selectors for all, element, class, and id matching.",
  "com.spinyowl.spinygui.core.style.stylesheet.term": "Typed CSS term values produced by parser visitors.",
  "com.spinyowl.spinygui.core.style.stylesheet.util": "Utility functions for converting and validating stylesheet values.",
  "com.spinyowl.spinygui.core.style.types": "Typed CSS value objects and constants for non-nested style domains.",
  "com.spinyowl.spinygui.core.style.types.background": "Background-origin, repeat, and sizing value objects.",
  "com.spinyowl.spinygui.core.style.types.border": "Border item and border-style value objects.",
  "com.spinyowl.spinygui.core.style.types.flex": "Flexbox alignment, direction, wrapping, and justification value constants.",
  "com.spinyowl.spinygui.core.style.types.length": "CSS length units, length wrappers, and conversion contract.",
  "com.spinyowl.spinygui.core.system.event": "Raw platform/window/input events before conversion into application-level events.",
  "com.spinyowl.spinygui.core.system.event.listener": "Adapters that translate raw system events into core event processing and state changes.",
  "com.spinyowl.spinygui.core.system.event.processor": "System-event processor contract and implementation for dispatching platform events.",
  "com.spinyowl.spinygui.core.system.event.provider": "Provider for mapping raw system event classes to listener instances.",
  "com.spinyowl.spinygui.core.system.font": "Platform font loading, text metrics, and font storage abstractions.",
  "com.spinyowl.spinygui.core.system.font.impl": "Default font service, storage, and platform-specific font directory discovery.",
  "com.spinyowl.spinygui.core.system.input": "Platform-facing key, modifier, action, and mouse-button enums.",
  "com.spinyowl.spinygui.core.time": "Time service abstraction for animation and frame timing.",
  "com.spinyowl.spinygui.core.util": "Small utilities for class-key maps, IO, node visibility, references, and text handling.",
  "com.spinyowl.spinygui.core.backend.renderer": "Renderer SPI consumed by backend implementations.",
  "com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg": "NanoVG renderer orchestration and specialized element/text/border renderers.",
  "com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util": "NanoVG drawing and color helpers.",
  "com.spinyowl.spinygui.demo.simple": "Simple demo entry points.",
  "com.spinyowl.spinygui.demo.complex": "Windowed GLFW/LWJGL demo framework and concrete NanoVG demo.",
  "com.spinyowl.spinygui.demo": "Demo namespace folder for runnable examples.",
}));

function walk(dir) {
  if (!fs.existsSync(dir)) return [];
  const entries = fs.readdirSync(dir, { withFileTypes: true });
  return entries.flatMap((entry) => {
    const p = path.join(dir, entry.name);
    if (entry.isDirectory()) return walk(p);
    return [p];
  });
}

function rel(p) {
  return path.relative(root, p).replace(/\\/g, "/");
}

function moduleName(file) {
  const r = rel(file);
  const first = r.split("/")[0];
  return first;
}

function sourceSet(file) {
  const r = rel(file);
  if (r.includes("/src/test/")) return "test";
  if (r.includes("/src/main/antlr/")) return "main/antlr";
  return "main";
}

function extractJavadocs(text, index) {
  const before = text.slice(0, index);
  const match = before.match(/\/\*\*([\s\S]*?)\*\/\s*(?:@\w[^\n]*\s*)*$/);
  if (!match) return "";
  const cleaned = match[1]
    .split(/\r?\n/)
    .map((line) => line.replace(/^\s*\*\s?/, "").trim())
    .filter((line) => line && !line.startsWith("@"))
    .join(" ")
    .replace(/\{@link\s+([^}]+)\}/g, "$1")
    .replace(/<[^>]+>/g, "")
    .replace(/\s+/g, " ")
    .trim();
  const sentence = cleaned.match(/^(.+?[.!?])(?:\s|$)/);
  return sentence ? sentence[1] : cleaned;
}

function declarationSummary(name, kind, declaration, file) {
  const base = path.basename(file);
  if (name.startsWith("CSS3") && file.includes(`${path.sep}antlr${path.sep}`)) {
    return "Generated ANTLR CSS3 parser artifact; regenerate it from CSS3.g4 rather than editing by hand.";
  }
  if (name.endsWith("Test")) return "JUnit test fixture for the corresponding production component.";
  if (kind === "interface" && name.endsWith("Service")) return "Service contract for the related subsystem.";
  if (kind === "interface" && name.endsWith("Provider")) return "Provider contract for supplying subsystem collaborators.";
  if (kind === "interface" && name.endsWith("Parser")) return "Parser contract for converting external text into core model objects.";
  if (kind === "interface" && name.endsWith("Renderer")) return "Renderer contract for drawing a frame through a backend.";
  if (name.endsWith("Impl")) return "Default implementation of the matching interface.";
  if (name.endsWith("Provider")) return "Provider implementation or registry for constructing subsystem collaborators.";
  if (name.endsWith("Visitor")) return "ANTLR visitor that maps parse-tree nodes into the stylesheet domain model.";
  if (name.endsWith("Selector")) return "CSS selector implementation used to match elements and calculate specificity.";
  if (name.endsWith("PropertyProvider")) return "CSS property parser that converts declaration terms into typed style properties.";
  if (name.endsWith("EventListener")) return "System-event listener/adapter for translating platform events into core behavior.";
  if (name.endsWith("Event")) return "Event payload object for the named input/window/node change.";
  if (name.endsWith("Layout")) return "Layout algorithm or layout contract for the named display/text mode.";
  if (name.endsWith("Util") || name.endsWith("Utils") || name.endsWith("Utilities")) return "Static helper methods for the named subsystem.";
  if (kind === "enum") return "Enumerates supported values for the named domain concept.";
  if (declaration.includes("extends Event")) return "Application-level event payload.";
  if (declaration.includes("extends SystemEvent")) return "Raw platform event payload.";
  if (declaration.includes("implements Unit")) return "CSS unit/value object used by style conversion.";
  if (base === "module-info.java") return "Java module descriptor.";
  return `Represents ${name.replace(/([a-z])([A-Z])/g, "$1 $2").toLowerCase()} in this package.`;
}

function parseJava(file) {
  const text = fs.readFileSync(file, "utf8");
  const pkg = (text.match(/^\s*package\s+([\w.]+)\s*;/m) || [])[1];
  if (!pkg) return null;
  const classes = [];
  const textWithoutComments = text
    .replace(/\/\*[\s\S]*?\*\//g, (m) => " ".repeat(m.length))
    .replace(/\/\/[^\n\r]*/g, (m) => " ".repeat(m.length));
  const depthText = textWithoutComments
    .replace(/"(?:\\.|[^"\\])*"/g, (m) => " ".repeat(m.length))
    .replace(/'(?:\\.|[^'\\])*'/g, (m) => " ".repeat(m.length));
  const topLevelDepthAt = (index) => {
    let depth = 0;
    for (let i = 0; i < index; i++) {
      const c = depthText[i];
      if (c === "{") depth++;
      else if (c === "}") depth--;
    }
    return depth;
  };
  const re = /((?:public|protected|private|abstract|final|static|sealed|non-sealed)\s+)*(class|interface|enum|record|@interface)\s+(\w+)([^{;]*)/g;
  let m;
  while ((m = re.exec(textWithoutComments))) {
    if (topLevelDepthAt(m.index) !== 0) continue;
    const kind = m[2] === "@interface" ? "annotation" : m[2];
    const name = m[3];
    const declaration = `${(m[1] || "").trim()} ${m[2]} ${name}${m[4] || ""}`.replace(/\s+/g, " ").trim();
    const javadoc = extractJavadocs(text, m.index);
    classes.push({
      name,
      kind,
      declaration,
      source: rel(file),
      summary: javadoc || declarationSummary(name, kind, declaration, file),
    });
  }
  return { pkg, module: moduleName(file), sourceSet: sourceSet(file), classes };
}

const javaFiles = roots.flatMap((r) => walk(path.join(root, r))).filter((f) => f.endsWith(".java"));
const packageMap = new Map();
for (const file of javaFiles) {
  const parsed = parseJava(file);
  if (!parsed) continue;
  if (!packageMap.has(parsed.pkg)) {
    packageMap.set(parsed.pkg, { pkg: parsed.pkg, modules: new Set(), sourceSets: new Set(), classes: [] });
  }
  const pkg = packageMap.get(parsed.pkg);
  pkg.modules.add(parsed.module);
  pkg.sourceSets.add(parsed.sourceSet);
  pkg.classes.push(...parsed.classes);
}

for (const pkg of [...packageMap.keys()]) {
  const segments = pkg.split(".");
  for (let i = 1; i < segments.length; i++) {
    const prefix = segments.slice(0, i).join(".");
    if (!packageMap.has(prefix)) {
      packageMap.set(prefix, { pkg: prefix, modules: new Set(), sourceSets: new Set(), classes: [] });
    }
  }
}

for (const pkg of [...packageMap.keys()]) {
  const info = packageMap.get(pkg);
  const segments = pkg.split(".");
  for (let i = 1; i < segments.length; i++) {
    const prefixInfo = packageMap.get(segments.slice(0, i).join("."));
    for (const module of info.modules) prefixInfo.modules.add(module);
    for (const sourceSet of info.sourceSets) prefixInfo.sourceSets.add(sourceSet);
  }
}

function packageDir(pkg) {
  return path.join(packagesRoot, ...pkg.split("."));
}

function linkToPackage(pkg, fromFile) {
  const target = path.join(packageDir(pkg), "README.md");
  return path.relative(path.dirname(fromFile), target).replace(/\\/g, "/");
}

function directChildren(pkg, allPackages) {
  const prefix = `${pkg}.`;
  return allPackages
    .filter((p) => p.startsWith(prefix))
    .map((p) => p.slice(prefix.length).split(".")[0])
    .filter((v, i, a) => a.indexOf(v) === i)
    .map((segment) => `${prefix}${segment}`)
    .filter((child) => allPackages.includes(child));
}

function descendants(pkg, allPackages) {
  const prefix = `${pkg}.`;
  return allPackages.filter((p) => p.startsWith(prefix));
}

function packageDescription(pkg) {
  if (packageDescriptions.has(pkg)) return packageDescriptions.get(pkg);
  const last = pkg.split(".").pop();
  return `Package for ${last.replace(/([a-z])([A-Z])/g, "$1 $2").toLowerCase()} related classes.`;
}

function packageReferenceSentence(pkg) {
  const info = packageMap.get(pkg);
  const classCount = info ? info.classes.length : 0;
  const descendantCount = info ? descendants(pkg, [...packageMap.keys()]).length : 0;
  const classText = classCount === 1 ? "1 direct class" : `${classCount} direct classes`;
  const descendantText =
    descendantCount === 1
      ? "1 descendant package"
      : `${descendantCount} descendant packages`;
  const description = packageDescription(pkg)
    .replace(/\s+/g, " ")
    .replace(/\.\s+/g, "; ")
    .replace(/\.$/, "");
  return `This reference describes ${description}, lists ${classText}, and aggregates ${descendantText}.`;
}

function writePackageDoc(pkg, allPackages) {
  const info = packageMap.get(pkg);
  const outFile = path.join(packageDir(pkg), "README.md");
  fs.mkdirSync(path.dirname(outFile), { recursive: true });
  const children = directChildren(pkg, allPackages);
  const desc = descendants(pkg, allPackages);
  const lines = [];
  lines.push(`# ${pkg}`);
  lines.push("");
  lines.push(packageDescription(pkg));
  lines.push("");
  lines.push(`- Modules: ${[...info.modules].sort().join(", ") || "none directly"}`);
  lines.push(`- Source sets: ${[...info.sourceSets].sort().join(", ") || "none directly"}`);
  lines.push(`- Direct classes: ${info.classes.length}`);
  lines.push(`- Descendant packages: ${desc.length}`);
  lines.push("");
  if (info.classes.length) {
    lines.push("## Classes");
    lines.push("");
    for (const cls of info.classes.sort((a, b) => a.name.localeCompare(b.name))) {
      lines.push(`### ${cls.name}`);
      lines.push("");
      lines.push(`- Kind: ${cls.kind}`);
      lines.push(`- Source: \`${cls.source}\``);
      lines.push(`- Declaration: \`${cls.declaration}\``);
      lines.push(`- Responsibility: ${cls.summary}`);
      lines.push("");
    }
  } else {
    lines.push("## Classes");
    lines.push("");
    lines.push("No direct Java classes were found in this package.");
    lines.push("");
  }
  if (children.length) {
    lines.push("## Child Packages");
    lines.push("");
    for (const child of children.sort()) {
      lines.push(`- [${child}](${linkToPackage(child, outFile)}) - ${packageReferenceSentence(child)}`);
    }
    lines.push("");
  }
  if (desc.length) {
    lines.push("## Aggregated Contents");
    lines.push("");
    lines.push(`This package aggregates ${desc.length} descendant package(s) with ${desc.reduce((n, p) => n + packageMap.get(p).classes.length, 0)} descendant class(es).`);
    lines.push("");
  }
  fs.writeFileSync(outFile, lines.join("\n"), "utf8");
}

function packageDepth(pkg) {
  return pkg.split(".").length;
}

function rootPackageList(allPackages) {
  return allPackages
    .filter((p) => !allPackages.some((q) => p !== q && p.startsWith(`${q}.`) && packageDepth(q) === packageDepth(p) - 1))
    .sort();
}

function writeRootDoc(allPackages) {
  const file = path.join(root, "PROJECT_STRUCTURE.md");
  const lines = [];
  lines.push("# Project Structure");
  lines.push("");
  lines.push("SpinyGUI is a modular Java GUI library with a browser-engine-like split between node tree, CSS parsing/style resolution, layout, event processing, and rendering backends.");
  lines.push("");
  lines.push("Generated package documents are stored under `docs/project-structure/packages/`. Each package document lists direct classes first, then links to child packages so the documentation can be read from deepest packages upward.");
  lines.push("");
  lines.push("## Gradle Modules");
  lines.push("");
  for (const [name, description] of Object.entries(moduleDescriptions)) {
    lines.push(`- \`${name}\` - ${description}`);
  }
  lines.push("");
  lines.push("## Main Subsystems");
  lines.push("");
  const subsystems = [
    ["Node tree", "core node classes model frames, elements, text, attributes, parent/child links, pseudo-state, and box geometry."],
    ["Style and CSS", "stylesheet model, selectors, property providers, ANTLR visitors, typed CSS values, and `ResolvedStyle` convert parsed CSS into values usable by layout/rendering."],
    ["Layout", "layout contracts and implementations calculate box-model rectangles, text metrics, normal-flow/positioned layout trees, scroll sizes, and client sizes."],
    ["Events and input", "system events are translated by system listeners/processors into application events and node state changes."],
    ["Fonts and metrics", "font service/storage abstractions load platform fonts and expose text metrics for layout/rendering."],
    ["Rendering", "backend SPI defines `Renderer`; the LWJGL/NanoVG backend traverses layout nodes and draws elements, borders, and text."],
    ["Demos", "simple and complex demo modules exercise the aggregate API and NanoVG backend."],
  ];
  for (const [name, text] of subsystems) lines.push(`- ${name}: ${text}`);
  lines.push("");
  lines.push("## Package Index");
  lines.push("");
  for (const pkg of allPackages) {
    lines.push(`- [${pkg}](${path.relative(root, path.join(packageDir(pkg), "README.md")).replace(/\\/g, "/")}) - ${packageReferenceSentence(pkg)}`);
  }
  lines.push("");
  lines.push("## Reading Order");
  lines.push("");
  lines.push("For bottom-up navigation, start with the deepest packages such as `style.stylesheet.selector.*`, `style.stylesheet.term`, `style.types.*`, `system.event.listener`, `layout.impl`, and `backend.renderer.lwjgl.nanovg.util`; then move upward through their parent package documents and finish with this root overview.");
  fs.writeFileSync(file, lines.join("\n"), "utf8");
}

function writeIndex(allPackages) {
  fs.mkdirSync(outRoot, { recursive: true });
  const file = path.join(outRoot, "README.md");
  const lines = [];
  lines.push("# Project Structure Documentation");
  lines.push("");
  lines.push("This folder mirrors Java package documentation generated from the project source tree.");
  lines.push("");
  lines.push("- [`PROJECT_STRUCTURE.md`](../../PROJECT_STRUCTURE.md) - This reference gives the root-level overview of Gradle modules, main subsystems, and the full package index.");
  lines.push("- [`AGENTS_CODE_STYLE.md`](../../AGENTS_CODE_STYLE.md) - This reference describes the coding style, architecture principles, package conventions, and caution areas for future agents.");
  lines.push("");
  lines.push("## Packages by Depth");
  lines.push("");
  for (const pkg of [...allPackages].sort((a, b) => packageDepth(b) - packageDepth(a) || a.localeCompare(b))) {
    lines.push(`- [${pkg}](${path.relative(path.dirname(file), path.join(packageDir(pkg), "README.md")).replace(/\\/g, "/")}) - ${packageReferenceSentence(pkg)}`);
  }
  fs.writeFileSync(file, lines.join("\n"), "utf8");
}

if (fs.existsSync(outRoot)) {
  fs.rmSync(outRoot, { recursive: true, force: true });
}

const allPackages = [...packageMap.keys()].sort();
for (const pkg of [...allPackages].sort((a, b) => packageDepth(b) - packageDepth(a) || a.localeCompare(b))) {
  writePackageDoc(pkg, allPackages);
}
writeIndex(allPackages);
writeRootDoc(allPackages);

console.log(`Wrote ${allPackages.length} package docs under ${rel(outRoot)}.`);
