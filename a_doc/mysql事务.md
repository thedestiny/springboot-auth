
[深度整理总结MySQL——二阶段提交工作原理](https://blog.csdn.net/qq_45852626/article/details/145589737)
binlog 会缓存在 binlog cache
redo log 会缓存在 redo log buffer
它们持久化到磁盘的时机分别由下面这两个参数控制.
一般我们为了避免日志丢失的风险，会将这两个参数设置为 1：
当 sync_binlog = 1 的时候,每次提交事务都会将 binlog cache 里的 binlog 直接持久到磁盘；
当 innodb_flush_log_at_trx_commit = 1 时,每次事务提交时，都将缓存在 redo log buffer 里的 redo log 直接持久化到磁盘.
所以在每个事务提交过程中， 都会至少调用 2 次刷盘操作，一次是 redo log 刷盘，一次是 binlog 落盘.
