#!/usr/bin/env node
import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import {
  CallToolRequestSchema,
  ListToolsRequestSchema
} from '@modelcontextprotocol/sdk/types.js';
import { computeGoldenCrop } from './goldenCrop.js';

const server = new Server(
  {
    name: 'golden-crop',
    version: '1.0.0'
  },
  {
    capabilities: {
      tools: {}
    }
  }
);

const TOOL_NAME = 'compute_golden_crop';

const toolInputSchema = {
  type: 'object',
  properties: {
    imageWidth: {
      type: 'number',
      description: 'Image width in pixels (natural size)'
    },
    imageHeight: {
      type: 'number',
      description: 'Image height in pixels (natural size)'
    },
    object: {
      type: 'object',
      description: 'Bounding rectangle of the subject in image pixel coordinates',
      properties: {
        x: { type: 'number' },
        y: { type: 'number' },
        width: { type: 'number' },
        height: { type: 'number' }
      },
      required: ['x', 'y', 'width', 'height']
    }
  },
  required: ['imageWidth', 'imageHeight', 'object']
};

server.setRequestHandler(ListToolsRequestSchema, async () => ({
  tools: [
    {
      name: TOOL_NAME,
      description:
        'Compute a golden-ratio crop around an object rectangle: matches the closest golden cell type, builds the circumscribed golden frame, maximizes within the image, biases vertical position to upper third, clamps to image bounds, and returns the crop-based φ grid lines plus the object snapped to that grid.',
      inputSchema: toolInputSchema
    }
  ]
}));

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  if (request.params.name !== TOOL_NAME) {
    throw new Error(`Unknown tool: ${request.params.name}`);
  }
  const args = request.params.arguments ?? {};
  try {
    const result = computeGoldenCrop(args.imageWidth, args.imageHeight, args.object);
    return {
      content: [
        {
          type: 'text',
          text: JSON.stringify(result, null, 2)
        }
      ]
    };
  } catch (e) {
    const message = e instanceof Error ? e.message : String(e);
    return {
      content: [{ type: 'text', text: JSON.stringify({ error: message }) }],
      isError: true
    };
  }
});

async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
