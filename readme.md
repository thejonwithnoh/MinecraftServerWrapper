Minecraft Server Wrapper 
========================

Copyright (c) 2016-2021 Jonathan Faulch

About
-----

Minecraft Server Wrapper, as the name suggests, is a wrapper for Minecraft
servers.  It is currently intended to give players and server administrators the
ability to execute new commands which are written as scripts.

Players and server administrators are able to execute scripts by submitting
commands in the following form:

    :<script-name> [optional-arguments]

The scripts can be written in any language that is supported by the Java
Scripting API.  These scripts are installed by placing them in a directory along
side the server.

The wrapper works by intercepting text flowing through the server's standard
input and output streams, parses it, and executes scripts in response to textual
triggers.  This simple approach allows the wrapper to be lightweight,
transparent, and robust against server updates.  While the wrapper was
originally intended to extend the capabilities of *vanilla* Minecraft servers,
it is just as capable of working with modded servers as well.

License
-------

Minecraft Server Wrapper is licensed under the MIT License.  See license.md.
