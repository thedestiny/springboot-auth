
#### 创建 sftp 账号
```
# 创建文件上传目录 创建用户组
mkdir /data/upload
chown root:root -R /data/upload/
chmod 755 -R /data/upload/

# 创建用户组
groupadd sftp

# 创建用户
# -d 登录时的目录 -m 自动建立登录目录 -g 指定用户组
# useradd -d /data/upload/clptsftp -m g sftp -s /sbin/nologin clptsftp
useradd -d /data/upload/clptsftp -m g sftp -s /sbin/nologin clptsftp

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




# 重新启动 sshd 
systemctl restart sshd



```
