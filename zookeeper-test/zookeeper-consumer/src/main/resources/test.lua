local key = KEYS[1]

local val = KEYS[2]

local expire = ARGV[1]

if redis.call("get", key) == false then

    if redis.call("set", key, val) then

        if tonumber(expire) > 0 then
            -- 设置过期时间
            redis.call("expire", key, expire)
        end
        return true
    end
    return false
else
    return false
end