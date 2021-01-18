kafka-models
=====================
Library containing models for use with Avro which will be auto-generated from Kafka schemas.

Cloning the repository
------------
Please use `git clone git@github.com:companieshouse/kafka-models.git --recursive` to include submodules.

Requirements
------------
In order to run the service locally you will need the following:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Building the models 
------------
The models can be built using Maven

```
mvn clean install
```

Using the library
------------
The library can be imported as a Maven dependency

```
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
   ```
   git submodule update --remote
   ```
3. The models will be auto-generated when the code is built.

Using the model for serialising with Avro
------------
```
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

```
public [Avro model] deserialize(Message message, Schema schema) throws IOException {
    DatumReader<[Avro model]> reader = new SpecificDatumReader<>();
    reader.setSchema(schema);

    try(ByteArrayInputStream in = new ByteArrayInputStream(message.getValue())) {
    Decoder decoder = DecoderFactory.get().binaryDecoder(in, null);

    return reader.read(null, decoder);
}
```