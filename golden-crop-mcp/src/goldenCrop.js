/**
 * Golden crop math (same rules as golden-crop/image-rect-select.html).
 * Pure functions: image dimensions + object rectangle → crop, snapped object, grid.
 */

export const PHI = (1 + Math.sqrt(5)) / 2;

export const RECT_TYPES = {
  '1xphi': { label: '1×φ (landscape)', gw: 1, gh: PHI },
  phix1: { label: 'φ×1 (portrait)', gw: PHI, gh: 1 },
  '2x2phi': { label: '2×2φ', gw: 2, gh: 2 * PHI },
  '2phix2': { label: '2φ×2', gw: 2 * PHI, gh: 2 },
  '3x3phi': { label: '3×3φ', gw: 3, gh: 3 * PHI },
  '3phix3': { label: '3φ×3', gw: 3 * PHI, gh: 3 }
};

export function findClosestGoldenType(ratio) {
  let bestId = '';
  let bestDiff = Infinity;
  let bestR = 0;
  for (const [id, rt] of Object.entries(RECT_TYPES)) {
    const r = rt.gw / rt.gh;
    const diff = Math.abs(Math.log(ratio) - Math.log(r));
    if (diff < bestDiff) {
      bestDiff = diff;
      bestId = id;
      bestR = r;
    }
  }
  return { id: bestId, label: RECT_TYPES[bestId].label, ratio: bestR };
}

export function clampCropToImage(crop, imgW, imgH) {
  const r = { x: crop.x, y: crop.y, width: crop.width, height: crop.height };
  r.x = Math.max(0, Math.min(r.x, imgW - 1));
  r.y = Math.max(0, Math.min(r.y, imgH - 1));
  r.width = Math.min(r.width, imgW - r.x);
  r.height = Math.min(r.height, imgH - r.y);
  r.width = Math.max(1, r.width);
  r.height = Math.max(1, r.height);
  return r;
}

export function clampSelectionToImage(sel, imgW, imgH) {
  const r = { x: sel.x, y: sel.y, width: sel.width, height: sel.height };
  r.x = Math.max(0, Math.min(r.x, imgW - 1));
  r.y = Math.max(0, Math.min(r.y, imgH - 1));
  const maxW = imgW - r.x;
  const maxH = imgH - r.y;
  r.width = Math.max(1, Math.min(r.width, maxW));
  r.height = Math.max(1, Math.min(r.height, maxH));
  return r;
}

function clampRectToImage(r, imgW, imgH) {
  const out = { ...r };
  if (out.x < 0) {
    out.width += out.x;
    out.x = 0;
  }
  if (out.y < 0) {
    out.height += out.y;
    out.y = 0;
  }
  if (out.x + out.width > imgW) out.width = imgW - out.x;
  if (out.y + out.height > imgH) out.height = imgH - out.y;
  return out;
}

export function goldenCropFromSelection(sel, imgW, imgH) {
  const cx = sel.x + sel.width / 2;
  const cy = sel.y + sel.height / 2;
  const selW = sel.width;
  const selH = sel.height;
  const selRatio = selW / selH;
  const { id, label, ratio: R } = findClosestGoldenType(selRatio);

  let outW;
  let outH;
  if (R < 1) {
    const invR = 1 / R;
    if (selW * invR >= selH) {
      outW = selW;
      outH = selW * invR;
    } else {
      outH = selH;
      outW = selH * R;
    }
  } else if (selH * R >= selW) {
    outH = selH;
    outW = selH * R;
  } else {
    outW = selW;
    outH = selW / R;
  }

  const circumscribed = {
    x: cx - outW / 2,
    y: cy - outH / 2,
    width: outW,
    height: outH
  };

  const cropCenterX = circumscribed.x + circumscribed.width / 2;
  const cropCenterY = circumscribed.y + circumscribed.height / 2;
  const w = circumscribed.width;
  const h = circumscribed.height;
  const scaleMaxX =
    w > 0 ? Math.min((2 * cropCenterX) / w, (2 * (imgW - cropCenterX)) / w) : 1;
  const scaleMaxY =
    h > 0 ? Math.min((2 * cropCenterY) / h, (2 * (imgH - cropCenterY)) / h) : 1;
  const scaleMax = Math.max(1, Math.min(scaleMaxX, scaleMaxY));
  let expanded = {
    x: cropCenterX - (w * scaleMax) / 2,
    y: cropCenterY - (h * scaleMax) / 2,
    width: w * scaleMax,
    height: h * scaleMax
  };
  expanded.y = cy - expanded.height / 3;
  expanded.y = Math.max(0, Math.min(expanded.y, imgH - expanded.height));

  return {
    matchedTypeId: id,
    matchedTypeLabel: label,
    circumscribedGoldenFrame: circumscribed,
    crop: clampRectToImage(expanded, imgW, imgH)
  };
}

export function getGridPositionsFromCrop(crop) {
  const cx = crop.x;
  const cy = crop.y;
  const cw = crop.width;
  const ch = crop.height;
  const v1 = cx + cw / PHI;
  const v2 = cx + cw - cw / PHI;
  const h1 = cy + ch / PHI;
  const h2 = cy + ch - ch / PHI;
  const cols = [cx, v1, v2, cx + cw];
  const rows = [cy, h1, h2, cy + ch];
  const posX = [];
  const posY = [];
  for (let i = 0; i < 3; i++) {
    const a = cols[i];
    const b = cols[i + 1];
    posX.push(a);
    posX.push(a + (b - a) / PHI);
    posX.push(b - (b - a) / PHI);
  }
  posX.push(cols[3]);
  for (let j = 0; j < 3; j++) {
    const a = rows[j];
    const b = rows[j + 1];
    posY.push(a);
    posY.push(a + (b - a) / PHI);
    posY.push(b - (b - a) / PHI);
  }
  posY.push(rows[3]);
  posX.sort((u, v) => u - v);
  posY.sort((u, v) => u - v);
  return { posX, posY };
}

function snapToNearest(arr, val) {
  let best = arr[0];
  let bestD = Math.abs(arr[0] - val);
  for (let k = 1; k < arr.length; k++) {
    const d = Math.abs(arr[k] - val);
    if (d < bestD) {
      bestD = d;
      best = arr[k];
    }
  }
  return best;
}

export function snapSelectionToGrid(sel, crop, imgW, imgH) {
  const { posX, posY } = getGridPositionsFromCrop(crop);
  let left = snapToNearest(posX, sel.x);
  let right = snapToNearest(posX, sel.x + sel.width);
  let top = snapToNearest(posY, sel.y);
  let bottom = snapToNearest(posY, sel.y + sel.height);
  if (right <= left) {
    for (let k = 0; k < posX.length; k++) {
      if (posX[k] > left) {
        right = posX[k];
        break;
      }
    }
    if (right <= left) right = left + 1;
  }
  if (bottom <= top) {
    for (let k = 0; k < posY.length; k++) {
      if (posY[k] > top) {
        bottom = posY[k];
        break;
      }
    }
    if (bottom <= top) bottom = top + 1;
  }
  const snapped = { x: left, y: top, width: right - left, height: bottom - top };
  return clampSelectionToImage(snapped, imgW, imgH);
}

/**
 * Full pipeline: validate inputs, compute crop, snap object to crop grid, extras.
 */
export function computeGoldenCrop(imageWidth, imageHeight, objectRect) {
  const imgW = Math.floor(Number(imageWidth));
  const imgH = Math.floor(Number(imageHeight));
  if (!Number.isFinite(imgW) || !Number.isFinite(imgH) || imgW < 1 || imgH < 1) {
    throw new Error('imageWidth and imageHeight must be positive integers');
  }
  const obj = {
    x: Number(objectRect.x),
    y: Number(objectRect.y),
    width: Number(objectRect.width),
    height: Number(objectRect.height)
  };
  if (
    !Number.isFinite(obj.x) ||
    !Number.isFinite(obj.y) ||
    !Number.isFinite(obj.width) ||
    !Number.isFinite(obj.height) ||
    obj.width < 1 ||
    obj.height < 1
  ) {
    throw new Error('object rectangle must have positive width and height');
  }

  const sel = clampSelectionToImage(obj, imgW, imgH);
  const { matchedTypeId, matchedTypeLabel, circumscribedGoldenFrame, crop: rawCrop } =
    goldenCropFromSelection(sel, imgW, imgH);
  const crop = clampCropToImage(rawCrop, imgW, imgH);
  const objectSnappedToGrid = snapSelectionToGrid(sel, crop, imgW, imgH);
  const { posX, posY } = getGridPositionsFromCrop(crop);

  const upperThird = {
    x: crop.x + crop.width / 2,
    y: crop.y + crop.height / 3
  };

  return {
    image: { width: imgW, height: imgH },
    object: { x: sel.x, y: sel.y, width: sel.width, height: sel.height },
    matchedGoldenType: { id: matchedTypeId, label: matchedTypeLabel },
    circumscribedGoldenFrame,
    crop,
    objectSnappedToGrid,
    upperThirdPoint: upperThird,
    gridLinesInImageSpace: { x: posX, y: posY },
    phi: PHI
  };
}
