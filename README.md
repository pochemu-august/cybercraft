# Cybercraft ⚡

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square)](https://github.com/augustuz/cybercraft)
[![Fabric Loader](https://img.shields.io/badge/Fabric-0.19.5+-00d2ff?style=flat-square&logo=fabric&logoColor=white)](https://fabricmc.net/)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-26.3-informational?style=flat-square&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Java Version](https://img.shields.io/badge/Java-25%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](LICENSE)

**Cybercraft** — киберпанк-модификация для Minecraft на базе **Fabric Loader**, переносящая механики нетраннинга, тактических нейровизоров и удаленного исполнения протоколов взлома (Quickhacks) в блочный мир.

---

## 👁️ О проекте (About)

В мире Cybercraft игрок получает доступ к продвинутым кибернетическим интерфейсам. Надевая тактический нейровизор (кибердеку), вы активируете аугментированное зрение, анализируете жизненные показатели целей сквозь дистанцию и загружаете разрушительные скрипты прямо в нейросети мобов через беспроводные ICE-протоколы.

Мод спроектирован с учетом современных стандартов Fabric:
- **Раздельные среды (Split SourceSets):** строгая изоляция клиентской и серверной логики (`src/main` и `src/client`).
- **Сетевой стек CustomPayload:** строго типизированная C2S/S2C коммуникация через `StreamCodec` с гарантированной диспетчеризацией в главном серверном потоке.
- **Vanilla Data Components:** аппаратные характеристики деки хранятся в неизменяемых компонентах `ItemStack` с сетевой синхронизацией.

---

## ⚡ Игровые механики (Features)

### 1. Экипировка нейровизора (`EquipmentSlot.HEAD`)
- Нейровизор (Кибердека) устанавливается исключительно в слот головы игрока.
- Поддерживает быстрое надевание по правому клику (компонент `DataComponents.EQUIPPABLE`).
- Обладает аппаратной спецификацией (`CyberdeckSpec`: Tier, Max RAM, RAM Regen).

### 2. Тактический режим сканера (HUD Scanner)
- Активируется удержанием клавиши **`Tab`** при надетом визоре.
- Отображает неоновый киберпанк-прицел и анализирует окружающее пространство.
- При наведении на живую цель захватывает хост на дистанции до 20 блоков:
  - Выводит имя моба в видоискателе.
  - Динамическая цветовая полоса HP (зеленый / желтый / красный) и числовой индикатор здоровья.
- **Оптимизация производительности:** рейкаст выполняется строго на клиентских тиках (20 Hz) с кэшированием сущности, исключая просадки FPS при рендеринге кадров HUD.

### 3. Скрипт быстрого взлома: "Short Circuit" (Короткое замыкание)
- Мгновенная удаленная перегрузка сервоприводов и нервной системы цели.
- Наносит чистый электрический урон (data-driven тип урона `cybercraft:short_circuit`).
- Накладывает дебафф замедления (Slowness II) на 4 секунды.
- Сопровождается электрическими искрами (`ELECTRIC_SPARK`) и звуком деактивации ICE.

---

## 🎮 Управление (Controls)

| Клавиша (по умолчанию) | Действие | Описание |
| :--- | :--- | :--- |
| **ПКМ** (с предметом в руке) | Экипировать визор | Надевает визор в слот шлема (`EquipmentSlot.HEAD`) |
| **`Tab`** (удержание) | Сканирование | Активирует интерфейс сканера и захват целей |
| **`R`** (во время сканирования) | Применить Quickhack | Удаленно загружает протокол *Short Circuit* в захваченную цель |

> *Все привязки клавиш можно переназначить в стандартном меню Minecraft: Настройки -> Управление -> Категория "Cybercraft".*

---

## 🛠️ Инструкция для разработчиков (Building & Setup)

### Требования
- **JDK 25+** (или совместимый с текущей конфигурацией проекта JDK 21+).
- Установленная переменная окружения `JAVA_HOME` или конфигурация в `gradle.properties`.

### Команды сборки и запуска

```bash
# Клонирование репозитория
git clone https://github.com/augustuz/cybercraft.git
cd cybercraft

# Сборка мода (артефакт .jar в build/libs/)
./gradlew build

# Запуск клиента Minecraft для отладки
./gradlew runClient

# Запуск выделенного сервера Fabric
./gradlew runServer
```

---

## 📁 Структура репозитория

```
cybercraft/
├── src/
│   ├── main/                            # Общий и серверный код (Common/Server)
│   │   ├── java/com/cybercraft/
│   │   │   ├── Cybercraft.java          # Главный входной класс ModInitializer
│   │   │   ├── hack/                    # Quickhacks, реестр и цели взлома (ShortCircuit, HackTarget)
│   │   │   ├── item/                    # Предметы (CyberdeckItem) и их компоненты (CyberdeckSpec)
│   │   │   ├── network/                 # Сетевой стек: ExecuteHackPayload, ModNetworking
│   │   │   ├── registry/                # Регистрация предметов, компонентов и типов урона
│   │   │   └── util/                    # Common-утилиты трассировки луча (NetrunnerRaycast)
│   │   └── resources/                   # Модели, текстуры, рецепты, типы урона, JSON-данные
│   │
│   └── client/                          # Строго клиентский код (Physical Client)
│       └── java/com/cybercraft/client/
│           ├── CybercraftClient.java    # Клиентский инициализатор ClientModInitializer
│           ├── hud/                     # Рендеринг HUD оверлея сканера (ScannerHudOverlay)
│           ├── key/                     # Регистрация KeyMapping (ModKeyBindings)
│           └── scanner/                 # Клиентский менеджер сканера и кэширования (ScannerManager)
│
├── build.gradle                         # Конфигурация Loom, dependencies, sourceSets
├── gradle.properties                    # Версии Minecraft, Loom, Fabric Loader и Fabric API
├── LICENSE                              # Текст лицензии MIT License
└── README.md                            # Документация проекта
```

---

## 📄 Лицензия

Проект распространяется под открытой лицензией [MIT License](LICENSE).  
Автор: **augustuz** (2026).
