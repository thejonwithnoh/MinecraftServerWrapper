// clamp value to y value within minecraft world
function heightClamp(y)
{
	return Math.min(Math.max(y, 0), 255);
}

// y value of ax + by + cz = d
function height(a, b, c, d, x, z)
{
	return (d - a * x - c * z) / b;
}

// point is inside the (closed) triangle defined by 1, 2, and 3
function pointInTriangle(x, y, x1, y1, x2, y2, x3, y3)
{
	return sameSide(x, y, x1, y1, x2, y2, x3, y3) &&
	       sameSide(x, y, x2, y2, x3, y3, x1, y1) &&
	       sameSide(x, y, x3, y3, x1, y1, x2, y2);
}

// 1 and 2 are on the same side of the line defined by a and b
// if either 1 or 2 is on the line then this is always true
function sameSide(x1, y1, x2, y2, xa, ya, xb, yb)
{
	return crossProdMag(xb - xa, yb - ya, x1 - xa, y1 - ya) *
	       crossProdMag(xb - xa, yb - ya, x2 - xa, y2 - ya) >= 0;
}

// magnitude of the cross product of two vectors
function crossProdMag(x1, y1, x2, y2)
{
	return x1 * y2 - y1 * x2;
}

if (args.length < 12)
{
	invoker.printError("Usage: " + args[0] + " <x1> <y1> <z1> <x2> <y2> <z2> <x3> <y3> <z3> <height> <tileName> [dataValue] [oldBlockHandling] [dataTag]");
}
else
{
	var
		x1 = +args[1], y1 = +args[2], z1 = +args[3],
		x2 = +args[4], y2 = +args[5], z2 = +args[6],
		x3 = +args[7], y3 = +args[8], z3 = +args[9],
		h = heightClamp(+args[10]),
		tail = Array.prototype.slice.call(args, 11).join(" "),
		
		a = y1 * (z2 - z3) + y2 *(z3 - z1) + y3 * (z1 - z2),
		b = z1 * (x2 - x3) + z2 *(x3 - x1) + z3 * (x1 - x2),
		c = x1 * (y2 - y3) + x2 *(y3 - y1) + x3 * (y1 - y2),
		d = x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1),
		
		minX = Math.min(x1, x2, x3),
		maxX = Math.max(x1, x2, x3),
		minZ = Math.min(z1, z2, z3),
		maxZ = Math.max(z1, z2, z3);
	
	for (var mz = minZ; mz <= maxZ; mz++)
	{
		for (var mx = minX; mx <= maxX; mx++)
		{
			if (pointInTriangle(mx, mz, x1, z1, x2, z2, x3, z3))
			{
				var th = heightClamp(height(a, b, c, d, mx, mz));
				console.execute
					("fill " + mx + " " +  h + " " + mz + " " +
					           mx + " " + th + " " + mz + " " + tail);
			}
		}
	}
}