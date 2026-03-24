# Golden crop MCP server

Stdio MCP server that exposes **`compute_golden_crop`**: given image width/height and an object rectangle `{ x, y, width, height }`, returns the same golden crop pipeline as `golden-crop/image-rect-select.html` (matched type, circumscribed frame, maximized crop, upper-third bias, clamped bounds, φ grid lines, object snapped to the crop grid).

## Install

```bash
cd golden-crop-mcp
npm install
```

## Run (stdio, for Cursor / Claude Desktop)

```bash
node /absolute/path/to/golden-crop-mcp/src/index.js
```

### Cursor (`mcp.json`)

```json
{
  "mcpServers": {
    "golden-crop": {
      "command": "node",
      "args": ["/absolute/path/to/Automata/golden-crop-mcp/src/index.js"]
    }
  }
}
```

## Tool: `compute_golden_crop`

**Arguments**

| Field | Type | Description |
|--------|------|-------------|
| `imageWidth` | number | Natural width in pixels |
| `imageHeight` | number | Natural height in pixels |
| `object` | object | `{ x, y, width, height }` in image space |

**Response** (JSON string in tool result text): `image`, `object`, `matchedGoldenType`, `circumscribedGoldenFrame`, `crop`, `objectSnappedToGrid`, `upperThirdPoint`, `gridLinesInImageSpace`, `phi`.

## Tests

```bash
npm test
```
