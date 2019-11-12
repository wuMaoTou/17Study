##Git实用实例

### 1.解决git在添加ignore文件之前就提交了项目无法再过滤问题

由于未添加ignore文件造成提交的项目很大(包含生成的二进制文件)。所以我们可以将编译生成的文件进行过滤，避免添加到版本库中了。

* 首先为避免冲突需要先同步下远程仓库
`$ git pull`

* 在本地项目目录下删除缓存
`$ git rm -r --cached .`

* 新建.gitignore文件
在项目的根目录下面新建.gitignore文件并添加相应的过滤规则

* 再次add所有文件
输入以下命令，再次将项目中所有文件添加到本地仓库缓存中
`$ git add .`

* 再次添加commit
这次commit是为了说明添加ignore文件的。
`$ git commit -m "add ignore"`

* 最后提交到远程仓库即可
`$ git push`

----

### 2.git合并分支上的commit为一条commit到master

按普通的提交流程是:
```
 git checkout master //切换回主分支
 git pull //拉取master代码
 git merge dev //如无冲突就已经合并成功如遇冲突就解决冲突再提交代码
```
如果想合并分支commit到主分支可以按如下走法:
```
 git checkout master //切换回主分支
 git pull //拉取master代码
 git merge dev --squash //如遇冲突就解决冲突
 git commit -m "这里是注释"
```

### 3.删除git仓库已提交的文件
在github上只能删除仓库,却无法删除文件夹或文件, 所以只能通过命令来解决

```
$ git --help  # 帮助命令

$ git pull origin master # 将远程仓库里面的项目拉下来

$ dir  # 查看有哪些文件夹
$ git rm -r --cached .idea # 删除.idea文件夹
$ git commit -m '删除.idea' # 提交,添加操作说明
$ git push -u origin master # 将本次更改更新到github项目上去
```

### 4.合并多次commit信息
右键点击项目 - git - repository - reset HEAD
to commit: HEAD^^^^ (^代表回退的次数)
再重新add文件commit - push 就可以了