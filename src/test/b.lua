
local a =1;
local b = 2;

local function xx()
 print("hello")
 local i = 0;
 local function yy()
    i=i+1;
 end

    return yy
end

xx();

xx()()