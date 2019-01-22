## 解决Git在添加ignore文件之前就提交了项目无法再过滤问题

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


test