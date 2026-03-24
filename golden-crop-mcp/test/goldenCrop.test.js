import test from 'node:test';
import assert from 'node:assert/strict';
import {
  PHI,
  computeGoldenCrop,
  findClosestGoldenType,
  getGridPositionsFromCrop
} from '../src/goldenCrop.js';

test('PHI is golden ratio', () => {
  assert.ok(PHI > 1.618 && PHI < 1.619);
});

test('findClosestGoldenType matches wide ratio to φ (portrait cell as frame)', () => {
  const { id, ratio } = findClosestGoldenType(2);
  assert.ok(['phix1', '2phix2', '3phix3'].includes(id));
  assert.ok(Math.abs(ratio - PHI) < 1e-9);
});

test('computeGoldenCrop returns crop inside image', () => {
  const r = computeGoldenCrop(1920, 1080, { x: 800, y: 400, width: 200, height: 200 });
  assert.ok(r.crop.x >= 0);
  assert.ok(r.crop.y >= 0);
  assert.ok(r.crop.x + r.crop.width <= 1920);
  assert.ok(r.crop.y + r.crop.height <= 1080);
  assert.ok(r.gridLinesInImageSpace.x.length > 0);
});

test('getGridPositionsFromCrop is sorted and bounded', () => {
  const crop = { x: 10, y: 20, width: 100, height: 80 };
  const { posX, posY } = getGridPositionsFromCrop(crop);
  assert.deepEqual(posX, [...posX].sort((a, b) => a - b));
  assert.equal(posX[0], 10);
  assert.equal(posX[posX.length - 1], 110);
});

test('computeGoldenCrop rejects invalid size', () => {
  assert.throws(() => computeGoldenCrop(0, 100, { x: 0, y: 0, width: 1, height: 1 }));
});
