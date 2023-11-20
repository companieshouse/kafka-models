kafka-models
=====================

Library containing models for use with Avro which will be auto-generated from Kafka schemas.

Things To consider
--

+ Always make sure any code changes you make get copied across to `main-8` (compatible with Java 8) also to `main` (compatible with Java 21)

###### Changes Specific to Java 8

+ Please create branch only from `main-8`
+ Please raise a PR to merge your changes only to [main-8](https://github.com/companieshouse/kafka-models/tree/main-8) branch
+ Use Java 8 Major tags generated from pipeline in your references (example : tags 1.x.x for java 8)

###### Changes Specific to Java 21

+ Please create branch only from `main`
+ Please merge your changes only to [main](https://github.com/companieshouse/kafka-models) branch
+ Use Java 21 Major tags generated from pipeline in your references (example : tags 3.x.x for java 21)

###### Pipeline

+ Please use this [Pipeline](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/kafka-models) and make sure respective `source-code-main` or `source-code-main-8` task gets started once the PR is created or after the PR is merged to `main` or `main-8` and once the pipeline tasks are complete then use the created tags respectively.

Cloning the repository
------------

Please use `git clone git@github.com:companieshouse/kafka-models.git --recursive` to include submodules.

Requirements
------------

In order to run the service locally you will need the following:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads)

Building the models
------------

The models can be built using Maven

``` bash
mvn clean install
```

Using the library
------------

The library can be imported as a Maven dependency

``` XML
<dependency>
    <groupId>uk.gov.companieshouse</groupId>
    <artifactId>kafka-models</artifactId>
    <version>${kafka-models.version}</version>
</dependency>
```

Updating a model
------------

1. Update the schema in [chs-kafka-schemas](https://github.com/companieshouse/chs-kafka-schemas).
2. Update the version of the chs-kafka-schemas submodule

   ``` bash
   git submodule update --remote
   ```

3. When adding a new avro file (not updating the contents of an existing avro file) - Add your new `avsc` file to the include list in pom.xml for avro-maven-plugin under source directory:

   ``` bash
   <sourceDirectory>${project.basedir}/chs-kafka-schemas/schemas</sourceDirectory>
   ```

4. The models will be auto-generated when the code is built.

Using the model for serialising with Avro
------------

``` Java
public byte[] serialize([Avro model] data) throws IOException {
    DatumWriter<[Avro model]> datumWriter = new SpecificDatumWriter<>();

    try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        datumWriter.setSchema(data.getSchema());
        datumWriter.write(data, encoder);
        encoder.flush();

        byte[] serializedData = out.toByteArray();
        encoder.flush();

        return serializedData;
    }
}
```

Using the model for deserialising with Avro
------------

When the models are auto-generated the schema is embedded within the model.

``` Java
public [Avro model] deserialize(Message message, Schema schema) throws IOException {
    DatumReader<[Avro model]> reader = new SpecificDatumReader<>();
    reader.setSchema(schema);

    try(ByteArrayInputStream in = new ByteArrayInputStream(message.getValue())) {
    Decoder decoder = DecoderFactory.get().binaryDecoder(in, null);

    return reader.read(null, decoder);
}
```
