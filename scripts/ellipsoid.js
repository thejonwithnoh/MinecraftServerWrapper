function clamp(x)
{
	return Math.min(Math.max(x, 0), 255);
}

if (args.length < 8)
{
	invoker.printError("Usage: " + args[0] + " <x1> <y1> <z1> <rx> <ry> <rz> <tileName> [dataValue] [oldBlockHandling] [dataTag]");
}
else
{
	var x  = +args[1], y  = +args[2], z  = +args[3],
		rx = +args[4], ry = +args[5], rz = +args[6],
		tail = Array.prototype.slice.call(args, 7).join(" ");
	
	for (var mz = -rz; mz <= rz; mz++)
	{
		for (var mx = -rx; mx <= rx; mx++)
		{
			var miny = Number.POSITIVE_INFINITY, maxy = Number.NEGATIVE_INFINITY;
			for (var my = -ry; my <= ry; my++)
			{
				if
				(
					mx * mx * ry * ry * rz * rz +
					rx * rx * my * my * rz * rz +
					rx * rx * ry * ry * mz * mz <=
					rx * rx * ry * ry * rz * rz
				)
				{
					miny = Math.min(miny, my);
					maxy = Math.max(maxy, my);
				}
			}
			if (miny != Number.POSITIVE_INFINITY && maxy != Number.NEGATIVE_INFINITY)
			{
				console.execute
					("fill " + (x + mx) + " " + clamp(y + miny) + " " + (z + mz) + " " +
					           (x + mx) + " " + clamp(y + maxy) + " " + (z + mz) + " " + tail);
			}
		}
	}
}