-- 原子释放幂等锁：仅当 Redis 中的值与 requestId 一致时才删除
-- 避免误删其他请求持有的锁
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end
