# Hash Utils

This project contains utilities to hash files with SHA256/SHA512.

### Algorithms

## MD5

```java
Path path = …
String hash = HashUtils.computeMD5(path);
```

## SHA256

```java
Path path = …
String hash = HashUtils.computeSHA256(path);
byte[] binHash = HashUtils.computeBinSHA256(path);
```

## SHA512

```java
Path path = …
String hash = HashUtils.computeSHA512(path);
byte[] binHash = HashUtils.computeBinSHA512(path);
```

## Chunk Hash

The chunk hash method will only hash the first 5 MB or 5% of the provided path. This method may be used when the performance / precision tradeof is acceptable.

```java
String hash = HashUtils.computeChunkHash(path);
byte[] binHash = HashUtils.computeBinChunkHash(path);
```

## Implementations

This project currently contains three different hasher implementations

```java
//  Return the default hasher
Hasher hasher = DefaultHasher.getHasher();

// Or create one from a specific implementation
Hasher hasher = new FileChannelHasher();
```


| Name                          | Description |
|-------------------------------|-------------|
| MmapHasher        (default)   | Uses JNI to make use of Mmap to map file regions to direct memory.                                                                    |
| FileChannelHasher             | Uses a filechannel to map regions of the file to memory. This implementation only supports files which are smaller Integer.MAX_VALUE  |
| MemorySegmentHasher           | Uses incubator classes from the JDK to map data to a memory segment.<br>Required JDK 14+ and additional JVM flags to module.          |
