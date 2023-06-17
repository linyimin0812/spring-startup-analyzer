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

本项目依赖了async-profiler，并对其进行了一些扩展

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