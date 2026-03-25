/**
 * HTTP API for AWS App Runner (and local: PORT defaults to 8080).
 * Same compute pipeline as MCP tool compute_golden_crop.
 *
 * API key (optional): set API_KEY. One key or comma-separated keys (rotation).
 * Send via header X-API-Key: <key> or Authorization: Bearer <key>
 * GET /health stays public (load balancers / App Runner). When API_KEY is set,
 * GET / and POST /v1/* require a valid key. OPTIONS is never authenticated (CORS).
 */
import crypto from 'node:crypto';
import http from 'node:http';
import { computeGoldenCrop } from './goldenCrop.js';

const PORT = Number(process.env.PORT || 8080);
const MAX_BODY_BYTES = Number(process.env.MAX_BODY_BYTES || 1048576);
/** Comma-separated origins, or * for Access-Control-Allow-Origin: * */
const CORS_ORIGINS = (process.env.CORS_ORIGINS || '')
  .split(',')
  .map((s) => s.trim())
  .filter(Boolean);

const VALID_API_KEYS = (process.env.API_KEY || '')
  .split(',')
  .map((s) => s.trim())
  .filter(Boolean);

function timingSafeKeyMatch(provided, secret) {
  if (typeof provided !== 'string' || typeof secret !== 'string') return false;
  const a = Buffer.from(provided, 'utf8');
  const b = Buffer.from(secret, 'utf8');
  if (a.length !== b.length) return false;
  return crypto.timingSafeEqual(a, b);
}

function isApiKeyValid(req) {
  if (VALID_API_KEYS.length === 0) return true;
  const headerKey = req.headers['x-api-key'];
  let provided = '';
  if (typeof headerKey === 'string' && headerKey.trim()) {
    provided = headerKey.trim();
  } else {
    const auth = req.headers.authorization;
    if (typeof auth === 'string' && auth.length > 7) {
      const m = auth.match(/^Bearer\s+(.+)$/i);
      if (m) provided = m[1].trim();
    }
  }
  if (!provided) return false;
  return VALID_API_KEYS.some((k) => timingSafeKeyMatch(provided, k));
}

function json(res, status, obj, extraHeaders = {}) {
  const body = JSON.stringify(obj);
  res.writeHead(status, {
    ...extraHeaders,
    'Content-Type': 'application/json; charset=utf-8',
    'Content-Length': Buffer.byteLength(body)
  });
  res.end(body);
}

function corsHeaders(req) {
  const h = {};
  if (CORS_ORIGINS.length === 0) return h;
  const origin = req.headers.origin;
  if (CORS_ORIGINS.includes('*')) {
    h['Access-Control-Allow-Origin'] = '*';
  } else if (origin && CORS_ORIGINS.includes(origin)) {
    h['Access-Control-Allow-Origin'] = origin;
    h['Vary'] = 'Origin';
  }
  h['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
  h['Access-Control-Allow-Headers'] = 'Content-Type, X-API-Key, Authorization';
  h['Access-Control-Max-Age'] = '86400';
  return h;
}

function readBody(req) {
  return new Promise((resolve, reject) => {
    let total = 0;
    const chunks = [];
    req.on('data', (chunk) => {
      total += chunk.length;
      if (total > MAX_BODY_BYTES) {
        reject(new Error('request body too large'));
        req.destroy();
        return;
      }
      chunks.push(chunk);
    });
    req.on('end', () => {
      const raw = Buffer.concat(chunks).toString('utf8');
      if (!raw) {
        resolve({});
        return;
      }
      try {
        resolve(JSON.parse(raw));
      } catch (e) {
        reject(new Error('invalid JSON'));
      }
    });
    req.on('error', reject);
  });
}

async function handlePostGoldenCrop(req, res, extraHeaders = {}) {
  let body;
  try {
    body = await readBody(req);
  } catch (e) {
    json(res, 400, { error: e instanceof Error ? e.message : String(e) }, extraHeaders);
    return;
  }
  try {
    const { imageWidth, imageHeight, object } = body;
    const result = computeGoldenCrop(imageWidth, imageHeight, object);
    json(res, 200, result, extraHeaders);
  } catch (e) {
    const message = e instanceof Error ? e.message : String(e);
    json(res, 400, { error: message }, extraHeaders);
  }
}

const server = http.createServer(async (req, res) => {
  const headers = corsHeaders(req);

  if (req.method === 'OPTIONS') {
    res.writeHead(204, headers);
    res.end();
    return;
  }

  const url = new URL(req.url || '/', `http://${req.headers.host || 'localhost'}`);

  if (req.method === 'GET' && (url.pathname === '/health' || url.pathname === '/')) {
    if (url.pathname === '/health') {
      res.writeHead(200, { ...headers, 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ status: 'ok' }));
      return;
    }
    if (!isApiKeyValid(req)) {
      res.writeHead(401, { ...headers, 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'unauthorized' }));
      return;
    }
    res.writeHead(200, { ...headers, 'Content-Type': 'application/json' });
    res.end(
      JSON.stringify({
        service: 'golden-crop-api',
        endpoints: {
          health: 'GET /health',
          compute: 'POST /v1/golden-crop',
          mcpNote: 'MCP stdio client uses node src/index.js locally, not this HTTP server'
        }
      })
    );
    return;
  }

  if (
    req.method === 'POST' &&
    (url.pathname === '/v1/golden-crop' || url.pathname === '/compute_golden_crop')
  ) {
    const h = corsHeaders(req);
    if (!isApiKeyValid(req)) {
      json(res, 401, { error: 'unauthorized' }, h);
      return;
    }
    await handlePostGoldenCrop(req, res, h);
    return;
  }

  res.writeHead(404, { ...headers, 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ error: 'not found' }));
});

server.listen(PORT, '0.0.0.0', () => {
  console.error(`golden-crop-api listening on 0.0.0.0:${PORT}`);
});
