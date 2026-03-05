
#### 创建 sftp 账号
```
# 1 创建文件上传目录
mkdir /data/upload

# 2 创建用户组  egrep sftp /etc/group 查询用户组信息
groupadd sftp

# 3 创建sftp 用户并创建文件上传目录并设置用户密码
useradd -d /data/upload/clptsftp -m -g sftp -s /sbin/nologin clptsftp
mkdir -p /data/upload/clptsftp/upload
passwd clptsftp

# 4 修改用目录属性 root 和 clptsftp
chown root:root -R /data/upload/clptsftp
chmod 755 -R /data/upload/clptsftp
chown -R clptsftp:sftp /data/upload/clptsftp/upload
chmod 755 /data/upload/clptsftp/upload

# 5 修改 /etc/ssh/sshd_config 文件
#Subsystem      sftp    /usr/libexec/openssh/sftp-server  #需要注释
Subsystem sftp internal-sftp  #使用系统自带的internal-sftp
Match Group sftp
Match User clptsftp
ChrootDirectory /data/upload/clptsftp
ForceCommand internal-sftp
AllowTcpForwarding no
X11Forwarding no

# 6 重新启动 sshd 
systemctl restart sshd

# 7 尝试连接,如果连接失败查看 tail /var/log/secure -n 20 进行排查
sftp clptsftp@ip 

cat /etc/ssh/sshd_config

# 创建用户
# -d 登录时的目录 -m 自动建立登录目录 -g 指定用户组
# useradd -d /data/upload/clptsftp -m g sftp -s /sbin/nologin clptsftp


# 查看配置文件 cat /etc/ssh/sshd_config
cat /etc/ssh/sshd_config | grep -v  ^#  |grep -v ^$

#Subsystem      sftp    /usr/libexec/openssh/sftp-server  #需要注释
Subsystem sftp internal-sftp
ChallengeResponseAuthentication yes
Protocol 2
## 配置SFTP
Match Group sftp                #使用用户组为sftp
X11Forwarding no
ChrootDirectory data/upload     #指定sftp访问的目录
ForceCommand internal-sftp
AllowTcpForwarding no

Subsystem sftp internal-sftp  #使用系统自带的internal-sftp
Match Group sftp
ChrootDirectory /data/upload/
# Match User clptsftp
# ChrootDirectory /data/upload/clptsftp
ForceCommand internal-sftp
AllowTcpForwarding no
X11Forwarding no

# 重新启动 sshd 
systemctl restart sshd



```


```
# 展示 20行数据，查看sftp 登录信息
tail /var/log/secure -n 20
# 查看 crontab 日志
tail -f /var/log/messages
```
