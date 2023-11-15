# Hash Utils

This project contains utilities to hash files with SHA256/SHA512.


## Maven

```xml
<dependency>
  <groupId>io.metaloom.utils</groupId>
  <artifactId>hash-utils</artifactId>
  <version>0.2.0</version>
</dependency>
```

### Algorithms

## MD5

```java
Path path = Paths.get("pom.xml");
String hash = HashUtils.computeMD5(path);
byte[] binHash = HashUtils.computeBinMD5(path);
```

## SHA256

```java
Path path = Paths.get("pom.xml");
String hash = HashUtils.computeSHA256(path);
byte[] binHash = HashUtils.computeBinSHA256(path);
```

## SHA512

```java
Path path = Paths.get("pom.xml");
String hash = HashUtils.computeSHA512(path);
byte[] binHash = HashUtils.computeBinSHA512(path);
```

## Chunk Hash

The chunk hash method will only hash the first 5 MB or 5% of the provided file. This method may be used when the performance / precision tradeof is acceptable.

```java
Path path = Paths.get("pom.xml");
String hash = HashUtils.computeChunkHash(path);
byte[] binHash = HashUtils.computeBinChunkHash(path);
```

## Zero Chunks

The zero chunk count method computes the amount of 4k byte chunks which consists of zeros.

```java
Path path = Paths.get("pom.xml");
int count = HashUtils.computeZeroChunkCount(path);
```

## Implementations

This project currently contains different hasher implementations:

```java
//  Return the default hasher
Hasher hasher = DefaultHasher.getHasher();

// Or create one from a specific implementation
Hasher hasher = new FileChannelHasher();
```

| Name                   | State             |            Description  |
|------------------------|-------------------|-------------------------|
| MemorySegmentHasher    | (default)         | Uses the JEP 424 foreign memory allocation API to memory map files.              
| FileChannelHasher      | -                 | Uses a filechannel to map regions of the file to memory. This implementation only supports files which are smaller Integer.MAX_VALUE  |
| MmapHasher             | (legacy/removed)  | Uses JNI to make use of Mmap to map file regions to direct memory.                                                                    |


## Release Process

```bash
# Update maven version to next release
mvn versions:set -DgenerateBackupPoms=false

# Now run tests locally or via GitHub actions
mvn clean package

# Deploy to maven central and auto-close staging repo. 
# Adding the property will trigger the profiles in the parent pom to include gpg,javadoc...
mvn clean deploy -Drelease
```
