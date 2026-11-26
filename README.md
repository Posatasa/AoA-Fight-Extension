# 盔甲转化台 (Armor Trans Table)

一个针对 **虚无世界 3 (Advent of Ascension 3)** 的 Forge 1.16.5 附属模组。

把一个 AoA3 盔甲部件放进输入格，即可将它转化为 **同一套盔甲** 的其它部件：

- **保留附魔**（以及其它 NBT，例如自定义名称）
- **耐久按比例换算**（例如输入剩 50% 耐久，输出也是 50%）
- 输出固定对应四个部位：头盔 / 胸甲 / 护腿 / 靴子

## 版本与前置

- Minecraft **1.16.5**
- Forge **36.2.34**（`[36,)`）
- 虚无世界 3 (AoA3) **3.6.11**（`[1.16.5-3.6.11,)`，必装前置）

> 未来计划移植到 1.21.1。

## 使用

1. 放置「盔甲转化台」，右键打开 GUI。
2. 在左边的大格子里放入一件 AoA3 盔甲（盔甲/胸甲/护腿/靴子均可）。
3. 右边四个格子会显示同系列对应的四个部件预览。
4. 左键点击某个输出格（或 Shift 点击）即可完成转化：消耗输入、得到对应部件。

**不可转化**（作为输入的独立头部装备会被忽略）：夜视镜、技能头盔（`helm_of_the_*`）、
以及三件水下供氧头饰（海神潜水盔 `oceanus_helmet`、海洋领主潜水盔 `sealord_helmet`、面罩 `face_mask`）。

## 构建

本仓库假设你已经有 Forge 1.16.5 的 Gradle 缓存（与 `AoA-3.6.11-Fix-Addon` 相同的环境）。
使用 Java 8 运行 Gradle 7.6.4 构建：

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-1.8'
$env:GRADLE_USER_HOME = 'C:\Users\admin\Documents\GitHub\.gradle-home'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# 使用工作区里已解压的 Gradle 7.6.4（离线构建）
& 'C:\Users\admin\Documents\GitHub\.cache\gradle\gradle-7.6.4\bin\gradle.bat' build --offline
```

产物在 `build\libs\convtable-1.0.0.jar`。

也可以直接用包装器（需要能访问 `services.gradle.org` 或已缓存发行版）：

```powershell
.\gradlew build
```

## 资源说明

- 方块纹理（16×16）来自 `convtable_gpt_pack.zip` 的 `block_front/side/top.png`；
  底面 `armor_trans_table_bottom.png` 复用侧面纹理（压缩包内未提供独立的底面）。
- GUI 纹理由脚本重绘为 **240×210** 的紧凑布局（标准 18px 背包槽位），确保在“自动”界面尺寸下
  完整显示；标题文字由代码绘制，走本地化 `lang/zh_cn.json` 与 `en_us.json`。
- 左侧输入格 36px、右侧四个输出格 28px，格内物品按格子大小放大居中渲染。

## 目录结构

- `src/main/java/com/convtable/`：模组源码
  - `block/ArmorTransTableBlock`、`tile/ArmorTransTableTile`、`container/ArmorTransTableContainer`、
    `container/ArmorTransSlot`、`client/screen/ArmorTransTableScreen`、`client/ClientEvents`
  - `ArmorTransformer`：盔甲识别与转化核心逻辑（注册名 `aoa3:<set>_<part>`，`legs` 结尾）
- `src/main/resources/assets/convtable/`：模型、方块状态、语言文件、纹理
- `src/main/resources/META-INF/mods.toml`：模组元数据与 AoA3 依赖声明
