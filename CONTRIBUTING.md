# Getting Started

本项目基于java8开发，所以需要提前安装JDK8

1. 下载源码

```shell
git clone https://github.com/linyimin0812/spring-startup-analyzer.git
cd spring-startup-analyzer
```

2. 编译

```shell
make all
```

# 其他依赖

本项目依赖了jaeger、jaeger-ui和async-profiler，并对其进行了一些扩展

## jaeger-ui

添加了`Summary`和`FlameGraph`两个展示UI

```shell
git clone https://github.com/linyimin0812/jaeger-ui.git
```

修改完成之后，push代码到仓库之后，需要打个tag

```shell
git tag ${version}
git push ${version}
```

**编译打包**

```shell
cd jaeger-ui
yarn build
tar -zcvf assets.tar.gz ./packages/jaeger-ui/build
```

上传asserts.tar.gz到[github releases](https://github.com/linyimin0812/jaeger-ui/releases)

## jaeger

添加了上传火焰图和markdown文件接口

```shell
git clone https://github.com/linyimin0812/jaeger.git

cd jaeger/jaeger-ui
# 拉取jaeger-ui最新代码
git pull
```

**编译打包镜像**

1. 修改`./script/local/setenv.sh`

```shell
# dockerhub配置
export DOCKERHUB_USERNAME=xxxxxx
export DOCKERHUB_TOKEN=xxxxxx
export DOCKERHUB_NAME_SPACE=xxxxxx

export BRANCH=main
# 镜像tag
export TAG=v2.0.0
```

2. create-baseimg

```shell
make create-baseimg
```

3. 打包/上传镜像
```shell
bash ./scripts/local/build-all-in-one-image.sh
```

## async-profiler

指定线程名称采样，支持正则匹配

```shell
git clone https://github.com/linyimin0812/async-profier.git
cd async-profier

git checkout feat/20230505_support_sample_specify_thread_1

```

切换到`feat/20230505_support_sample_specify_thread_1`分支，修改完成后，进行编译

```shell
make
```

编译结果会放到build文件夹下，需要将so文件移动到`spring-startup-analyzer`项目文件中

```shell
# linux
mv ./build/libasyncProfiler.so ${dir}/spring-startup-analyzer/spring-profiler-extension/async-profiler/libasyncProfiler-linux-x64.so
# mac
mv ./build/libasyncProfiler.so ${dir}/spring-startup-analyzer/spring-profiler-extension/async-profiler/libasyncProfiler-mac.so
```