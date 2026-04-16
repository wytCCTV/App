# 自动喂养系统 APP

## 项目简介

本项目是一个基于 Android 的宠物自动喂养系统控制 APP，用于连接下位机设备并完成状态查看、参数设置、工作模式切换与设备控制。

APP 主要面向自动喂养场景，支持查看温度、水位、食物重量等数据，并可设置定时投喂、阈值参数，以及控制水泵、喂食和加热等功能。

## 主要功能

- 连接设备：通过局域网连接下位机，默认连接地址为 `192.168.4.1:8080`
- 实时数据显示：显示温度、水位、食物重量、加热状态、水泵状态、报警状态等信息
- 动物类型选择：支持选择不同宠物类型
- 自动投喂量计算：输入宠物重量后，自动计算推荐投喂量
- 定时投喂设置：支持 3 组投喂时间设置
- 阈值设置：支持温度、水位、食物重量等阈值输入
- 设备控制：支持水泵、喂食、加热开关控制
- 模式切换：支持主页、时间、阈值、设备控制等工作模式切换

## 运行环境

- Android Studio Hedgehog 或更高版本
- Android SDK 34
- 最低支持 Android 7.0（API 24）
- 建议使用 JDK 17 构建 Android Gradle 插件环境

## 项目结构

```text
.
├─ app
│  ├─ src/main/java/com/example/application_demo
│  │  ├─ DeviceControlActivity.java   # 主界面与主要业务逻辑
│  │  └─ NetUtils.java                # 网络相关辅助类
│  ├─ src/main/res/layout
│  │  └─ activity_main2.xml           # 主页面布局
│  ├─ src/main/res/drawable           # 背景、输入框、按钮等资源
│  └─ build.gradle                    # APP 模块构建配置
├─ gradle/wrapper                     # Gradle Wrapper
├─ build.gradle                       # 根构建配置
└─ settings.gradle                    # 工程配置
```

## 构建方式

### 方式一：Android Studio

1. 使用 Android Studio 打开项目根目录
2. 等待 Gradle 同步完成
3. 连接设备或启动模拟器
4. 点击运行按钮进行调试或打包

### 方式二：命令行

在项目根目录执行：

```bash
./gradlew assembleDebug
```

Windows 下执行：

```powershell
.\gradlew.bat assembleDebug
```

## 通信说明

- APP 当前通过 Socket 与设备通信
- 默认地址为 `192.168.4.1`
- 默认端口为 `8080`
- 主逻辑位于 `DeviceControlActivity.java`

如果下位机通信协议或地址发生变化，请同步修改代码中的连接参数与消息格式。

## 当前界面补充

本版本已新增一个“输入宠物重量，自动计算投喂量”的显示模块，位于主页面动物选择区域下方。输入宠物重量后，系统会根据当前选择的动物类型计算推荐投喂量。

## 注意事项

- 仓库中不应提交 `.idea/`、`build/`、`release APK` 等生成文件
- 如在 GitHub Actions 或其他 CI 平台编译，请确认 JDK 版本与 Android Gradle 插件版本兼容
- 如需适配真实硬件，请保证手机与下位机处于同一网络环境

## 后续可扩展方向

- 将设备 IP 和端口改为可配置
- 将投喂量计算规则抽离为独立配置
- 增加数据记录与历史查询功能
- 增加登录、宠物档案与多设备支持
