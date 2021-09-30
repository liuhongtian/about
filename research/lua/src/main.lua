local cjson = require "cjson"

local uri_string = "a/b/d"
local data_json = '{"e":"e1","f":"f1"}'
local data_table = cjson.decode(data_json)

local ssd_filename_default = "default.json";
local ssd_file_default = io.open(ssd_filename_default, "r")
local ssd_json_default = ssd_file_default:read("*a")
io.close(ssd_file_default)
local ssd_table = cjson.decode(ssd_json_default)
--print(ssd_table)

local value = ssd_table
--print(value)
for item in string.gmatch(uri_string, "([^/]+)") do
    if (string.sub(item, -1, -1) == ")") then
        local s, e = string.find(item, "%(")
        local key = string.sub(item, 0, s - 1)
        local index = tonumber(string.sub(item, s + 1, -2))
        value = value[key][index]
        --print(value)
    else
        local key = item
        value = value[key]
        --print(value)
    end
end

--print(cjson.encode(value))
--r = clone(data_table, value)
--print(cjson.encode(value))
--print(cjson.encode(ssd_table))

print(getmetatable(value))


-- print(cjson.encode(value))