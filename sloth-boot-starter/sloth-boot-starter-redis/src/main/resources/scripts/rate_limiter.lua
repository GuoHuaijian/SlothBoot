local key = KEYS[1]
local now = tonumber(ARGV[1])
local period = tonumber(ARGV[2])
local count = tonumber(ARGV[3])
local member = ARGV[4]
local window_start = now - (period * 1000)

redis.call('ZREMRANGEBYSCORE', key, 0, window_start)
local current = redis.call('ZCARD', key)
if current >= count then
    redis.call('PEXPIRE', key, period * 1000)
    return 0
end

redis.call('ZADD', key, now, member)
redis.call('PEXPIRE', key, period * 1000)
return 1
