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

## HTTP API (AWS App Runner & local)

App Runner runs a **container** that executes `node src/server.js` (not the MCP stdio entrypoint).

- `GET /health` — App Runner health check (**no API key**)  
- `GET /` — small JSON discovery document (**requires API key when `API_KEY` is set**)  
- `POST /v1/golden-crop` — same payload as the MCP tool: JSON `{ "imageWidth", "imageHeight", "object" }`  
- `POST /compute_golden_crop` — alias  

**Authentication:** If env **`API_KEY`** is set (non-empty), send the key as **`X-API-Key: <key>`** or **`Authorization: Bearer <key>`**. Multiple keys are allowed as a comma-separated list in `API_KEY` (any match succeeds). If `API_KEY` is unset or empty, the HTTP API does not enforce auth (use only for local dev).

Optional env: `PORT` (default `8080`), `CORS_ORIGINS`, `MAX_BODY_BYTES`.

Local:

```bash
npm run start:http
# with auth:
API_KEY=your-secret npm run start:http
```

### Docker image

From `golden-crop-mcp/`:

```bash
docker build -t golden-crop-api .
docker run --rm -p 8080:8080 -e API_KEY=your-secret golden-crop-api
```

---

## IaC — AWS App Runner

ECR has no image until you push; create infra first, push, then create the service (or use Terraform’s two-step flag).

### CloudFormation

1. Deploy **01** (ECR + IAM access role). Use the **same stack name** as `EcrStackName` in step 2 (default `golden-crop-ecr`).

```bash
aws cloudformation deploy \
  --stack-name golden-crop-ecr \
  --template-file infrastructure/cloudformation/01-ecr-and-access-role.yaml \
  --capabilities CAPABILITY_IAM
```

2. Authenticate Docker, build, push (replace `region` / account if needed):

```bash
AWS_REGION=eu-north-1
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $(aws sts get-caller-identity --query Account --output text).dkr.ecr.$AWS_REGION.amazonaws.com
REPO=$(aws cloudformation describe-stacks --stack-name golden-crop-ecr --query "Stacks[0].Outputs[?OutputKey=='RepositoryUri'].OutputValue" --output text)
docker build -t golden-crop-api .
docker tag golden-crop-api:latest $REPO:latest
docker push $REPO:latest
```

3. Deploy **02** (App Runner). `EcrStackName` must match the stack name from step 1.

```bash
aws cloudformation deploy \
  --stack-name golden-crop-app \
  --template-file infrastructure/cloudformation/02-apprunner-service.yaml \
  --parameter-overrides EcrStackName=golden-crop-ecr ImageTag=latest ApiKey=your-secret \
  --capabilities CAPABILITY_IAM
```

Omit `ApiKey` or set it to empty to deploy without HTTP API key enforcement (not recommended for public endpoints).

### Terraform

`infrastructure/terraform/` — first apply **without** the App Runner service (no image yet), push, then enable the service.

```bash
cd infrastructure/terraform
terraform init

# ECR + IAM only
terraform apply -var="create_apprunner_service=false"

# docker login, build, tag to output ecr_repository_url, push ...

# Full service
terraform apply -var="create_apprunner_service=true" -var="image_tag=latest" -var="api_key=your-secret"
```

Use `-var='api_key='` for no key (local/dev only).

## Tests

```bash
npm test
```
