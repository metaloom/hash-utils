# Hash Utils

This project contains utilities to hash files with SHA256/SHA512.

```java
Path file = …
String h1 = DefaultHasher.getHasher().computeSHA512(file);

// is equivalent to ..
String h2 = HashUtils.computeSHA512(file);

// or by selecting the hasher implementation manually
new FileChannelHasher()..computeSHA512(file);
```

This project currently contains three different hasher implementations


| Name                 | Description |
|----------------------|-------------|
| FileChannelHasher    | Uses a filechannel to map regions of the file to memory.              |
| MmapHasher           | Uses JNI to make use of Mmap to map file regions to direct memory.         |
| MemorySegmentHasher  | Uses incubator classes from the JDK to map data to a memory segment.<br>Required JDK 14+ and additional JVM flags to module.   |
