# Индивидуальное домашнее задание по курсу “Конструирование программного обеспечения” на тему «Мультиагентная система управления рестораном»

---

### Запуск
! В проекте используется java 13, gradle также собирает под java 13.
Поэтому если стоит Java другой версии, то можно в файле build.gradle изменить версию java на свою.
```
java {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
}
```
Для запуска программы необходимо выполнить команды:
```shell
gradle build
java -jar build/libs/java_IDZ_2_year
```