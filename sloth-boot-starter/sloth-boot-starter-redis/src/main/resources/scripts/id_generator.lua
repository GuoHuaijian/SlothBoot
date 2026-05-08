local key = KEYS[1]
local ttl = tonumber(ARGV[1])

local sequence = redis.call('INCR', key)

if sequence == 1 then
    redis.call('EXPIRE', key, ttl)
end

return sequence
