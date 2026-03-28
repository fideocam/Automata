# Golden Framer

Software that **steers the camera**—on a **real drone** or in a **3D viewport**—so that whatever is **locked in the frame** (a subject, a target, a tracked region) stays in **composition-optimal** positions as the scene or platform moves.

## Idea

1. **Lock** — You designate what matters in the image (manual ROI, tracker, or click-to-lock). That region or its anchor point is the thing to hold in the “good” part of the frame.

2. **Optimal placement** — Rules come from the same family as the rest of this repo: golden-ratio divisions, upper-third bias, grid cells, etc. (see `golden-crop`, `golden-rectangles`). The system turns those rules into **target coordinates in normalized image space** (e.g. where the subject center *should* sit).

3. **Control** — On each update (video frame or render tick), compare **actual** vs **target** placement. The error drives **commands**:
   - **Drone** — gimbal pitch/yaw (and optionally position or yaw of the aircraft within policy/safety limits), respecting latency and rate limits.
   - **3D modeling camera** — orbit, pan, tilt, dolly, or constrained camera rig so the locked subject drifts toward the optimal overlay without fighting the artist’s other constraints.

4. **Continuous framing** — As the subject or background moves, the controller keeps nudging the camera so the lock stays **near** the optimal locus (with smoothing and deadband to avoid jitter).

## Object direction and initial slot choice

**Which golden slot (or horizontal line) you pick first** should depend on how the locked object is oriented and moving in the world, not only on “wide vs close” and headroom.

- **Facing / gaze** — A person or creature (or any “front”) that looks left or right needs **space in front of the nose** (*lead room*, *look room*). That biases the initial anchor to the **opposite** side of the frame: e.g. subject facing frame-right → prefer a slot on the **left** half so the composition opens into the direction they look. Facing **toward camera** weakens left–right bias; **center** or **symmetric** slots (or a vertical φ line through center) often read better than a strong corner slot.

- **Motion vector** — Same idea as gaze: if the lock tracks something moving frame-left, put the subject slightly **frame-right** so there is **travel space** ahead. For drones following a subject, the predicted **velocity in image space** (or world velocity projected to the view) can choose between **left vs right** φ intersections before you refine with golden horizontals and headroom.

- **Object axis / silhouette** — Elongated subjects (vehicle, tree, building edge) align with **grid lines**: the long axis can sit near a **vertical** or **horizontal** φ line while the “important” end (front, top) sits on the matching perpendicular—so direction picks **which intersection family** is primary (horizontal emphasis vs vertical).

- **Aerial / map north** — For map-like views, **world heading** (camera path vs subject path) can map to a default slot quadrant so “forward” in the mission still has room in frame.

**Summary:** direction supplies a **left–right (or quadrant) prior** on top of distance (wide/close), **headroom** (close-up), and **slot vs φ-line vs center**. A controller can implement that as: compute candidate golden anchors → **score or filter** by facing/motion → choose initial target → then run the continuous framing loop.

## Terrain and the solid angle of valid viewpoints

Where the object sits in the **world** limits where the camera *can* be, before composition picks among legal shots.

- **Flat or gently open ground** — Treat the local support as a **plane**. The drone (or walkable camera) usually stays **above** that plane and outside no-fly buffers. From the object’s anchor, admissible viewing directions are roughly a **hemisphere** (or slightly less than half a full sphere if you forbid steep down-shots or enforce minimum altitude): you do **not** get an equal “full ball” of positions through the ground. **Azimuth** may still sweep a wide arc at usable height, but **elevation** and collision bounds carve out something closer to **half a hemisphere** of *meaningful* orbit options than a full \(4\pi\) steradians.

- **Mountainous / broken 3D terrain** — Valleys, ridges, multiple altitude bands, and flyable corridors add **extra approach directions** that are not available on a plain: you can frame from **above**, **below** (across a drop), or **around** the mass. Obstacles still remove wedges of space, but the **feasible set** of camera positions around the subject tends toward a **much larger solid angle**—in the limit, something like a **full sphere** of *candidate* directions (minus occluders and regulations), not only the “dome above a flat earth.”

So **terrain type** should feed the planner: first build a **valid viewpoint manifold** (collision, slope, geofence, line of sight), *then* intersect with **golden slot / direction / headroom** preferences. On a plain, some “ideal” compositions may be **unreachable**; on a mountain, more φ-based orbits may be realizable.

## Minimum subject scale in frame

Independently of terrain, the locked object should **not be too small** in the image: below a threshold, composition rules are hard to read and tracking is unstable. The controller should enforce a **minimum size** (e.g. minimum height or area of the lock in **normalized frame coordinates**, or minimum pixels at 1080p) by **moving closer**, **longer focal length**, or **rejecting** a shot until range improves—subject to the terrain-feasible manifold above.

## Camera motion vs moving object: what moves the “selected position”?

Treat two things separately:

- **Target anchor** \(p^\*\) — Where the lock *should* sit in **normalized image space** (golden slot, φ line, center). It changes when **you** change framing mode, direction prior, or terrain forces a different preset—not every frame.
- **Measured lock** \(p\) — Where the tracker says the subject is **this** frame. It moves because **the object moved**, **the camera moved**, or both.

Panning, zooming, and translating the camera all change \(p\). The object’s own motion also changes \(p\). The controller’s job is to command the camera so \(p \to p^\*\) while respecting smoothness, min size, and the feasible viewpoint set.

### Roles of pan, zoom, and position

- **Pan / tilt (gimbal or virtual orbit)** — First line of defense for **2D offset** in the frame: cheap, fast, keeps range and perspective roughly stable. Use for **small and medium** tracking error and for **predicted short-horizon drift** of the subject in image space.
- **Zoom (FOV / focal length)** — Primarily controls **scale** (subject too small/large). Use when **size constraints** bite; remember zoom also **amplifies** pixel-level jitter, so pair with smoothing and sane rate limits.
- **Translate camera in world** (dolly, strafe, orbit path, drone position) — Fixes **persistent** image error when gimbal hits limits, when you need **new parallax** or **line of sight**, or when **terrain** only allows certain arcs. Slower and safety-critical on real aircraft.

### Known vs unknown object motion

- **Idea of motion** (velocity, path, intent, or a filter estimate) — Add **feed-forward**: predict \(p\) one or a few frames ahead and apply camera motion to **cancel expected drift** before it shows up as large error. Same math for 3D: project predicted world position of the lock into the view. This reduces lag and overshoot when the object moves smoothly.
- **Unknown / “random” motion** — Rely on **feedback** only: each frame, \(e = p^\* - p\), map \(e\) to rates (gimbal first, then position/zoom as needed). Use a **velocity estimator** (Kalman, α-β, optical-flow-assisted) on \(p\) itself so you still get a little predictive damping without a mission model.

### Should \(p^\*\) itself move when the camera moves?

**Default:** **No.** The golden point stays fixed in the overlay; the camera and gimbal do the work. That keeps composition readable.

**Optional:** **Slowly blend** \(p^\*\) when switching presets (slot A → slot B) or when direction/terrain mode changes—never snap unless the user asks.

**Avoid** tying \(p^\*\) to raw tracker noise; that turns the grid into jitter. If you want “floating” composition, low-pass \(p^\*\) on a long time constant.

### Practical loop (each tick)

1. Update **measurement** \(p\) and lock size from the tracker.  
2. Resolve **target** \(p^\*\) from mode + direction + terrain (may be unchanged).  
3. Optional: **predict** \(p\) at \(t+\Delta t\) from motion model → **feed-forward** command.  
4. **Feedback** from \(e = p^\* - p\) (and size error vs min/max).  
5. **Saturate and allocate** to pan/tilt, then zoom, then world translation, with rate limits and obstacle checks.  
6. **Smooth** outputs (low-pass, slew limits) to avoid visible stepping.

This folder is the home for specs, prototypes, and integration code for that loop. It sits alongside `golden-crop` and `golden-rectangles`, which supply the **geometry of “optimal”**; Golden Framer adds **sensing + actuation** (or **viewport API**) to maintain it.
