# Graph Loader

Use batch loaders to optimize the load of graph/tree structure.

## Introduction

Graph Loader is a tiny library to load a graph of objects,
for instance one or more DTOs, that should be batched for acceptable performances.

The idea comes from use cases that, in the context of a business application, are
usually resolved by using GraphQL and/or `java-dataloader`.
One of the main goal of GL is *to be tiny* so async or `CompletableFuture` are right now not
supported.

### Why this library

When an endpoint should prepare a response, mostly in JSON format, to be used
to compose several UI widget,  there is a point in the algorithm
that can, and usually does, raise the N+1 problem. No matter if you load data from
a database, from a DDD *repository*, or from a microservice through ReST.

Imagine a list of posts each one with the user that posted it. You are probably able
to load the list of posts with a first batch load. To load the users you will have
the N+1 problem or you will do extra work to batch this second load.
Imagine a single post has several property like the user, or the user itself has
properties that require additional load requests.
If you are querying a SQL database you can join more tables, for instance the
post and the user tables. If you have 10 property to load a select with 10 different
table get messy quickly.

One solution is to wait and delay the load of a detail until somehow the list of the detail
of the same kind (users) has been collected so a batch load can be executed.

`java-dataloader` is able to load a graph layer after layer together with graphql-java
with handling of multi-threading and async loads. Unfortunately there is no easy why
to use only `java-dataloader` to achieve this task.

Graph Loader splits the composition of the result is several sequential operations
of *load* and *assemble*.
If the last *assemble* operation have not requested any load this means that
the result is complete.

### Concepts

1. *load* and *assemble*: the result gets calculated by successive approximations indeed objects resolved by GL
should be mutable. I like to use immutable objects whenever possible,
but here mutable objects seemed good enough as I plan to use GL just to prepare data
before the conversion to JSON or XML.

1. *batched load*: should be possible to batch data loading, for instance load several database records or ReST
resources at once.

### Classes

One can start designing the API from the types GL will assemble, this is how your response
data will look like. In `test` the examples use `PostResource`, `UserResource`, etc.

At the other side of the algorithm there are the objects loaded from you medium.
These can be records loaded from the database of other resources loaded via ReST.
In `test` they are `Post`, `User`, etc., let's call them *entities*.

Then one have to describe how these entities are loaded in batch. This is specified
with a set of `MappedBatchLoader` each one taking a set of keys and returning
a `Map<Key,Entity>`. `MappedBatchLoader`s are organized into a `MappedBatchLoaderRegistry`.

These `MappedBatchLoader` are wrapped transparently served as `DataLoader` that you use into an assembler
when you need to load a child entity.

After an entity gets loaded it is transformed by an assembler, see `GLAssembler`. It receives
a complete context `GLAssemblerContext` containing:
  - GLContext: context that last for the life span of GL, probably for the whole application.
  - DataLoaderRegistry: so you can load more child entity.
  - ExecutionContext: context of a single GL.resolve().

The characteristic of GL is to required `DataLoader` that take a `Consumer<Entity>`
that is invoked as soon as the entity gets loaded. This consumer is usally just the execution
of an assembler.

`GL` is the main class with two methods `resolve()` and `resolveMany()` that return a
`GLResult<D>` or a `GLResult<List<D>>` respectively. The `K` generic indicates the
type of the key, of a loaded value indicated with `V` that is processed by an assembler
that produces a result of type
`D`.
 
 ### Internal classes

- GLDataLoader: wrapper around a DataLoader plus an ExecutionState.
- MappedDataLoader.
- StatedDataLoaderRegistry: wrapper around DataLoaderRegistry instrumented with counters
required for statistics and dispatch.

### Example

See tests.

## Benchmarks

GL is not really comparable with graphql-java as it is a complete implementation
of GraphQL. However, I could not find a better pair project for benchmarks.
Said that if you don't want to use GraphQL, want to express you DTO and (assembler)
in Java, this is an approach, with or without GL, worth a try.
There is a quick comparison, probably to be refined, launch:

```bash
./gradlew jmh
```

```
Benchmark                                                             Mode  Cnt       Score       Error   Units
GLBenchmark.glAvgTime                                                 avgt    3       1.212 ±     0.033   us/op
GLBenchmark.glAvgTime:·gc.alloc.rate.norm                             avgt    3    2728.000 ±     0.001    B/op
GLBenchmark.glAvgTime:·gc.churn.PS_Eden_Space.norm                    avgt    3    2730.117 ±   205.321    B/op
GraphQLJavaBenchmark.graphqlAvgTime                                   avgt    3      94.926 ±     5.929   us/op
GraphQLJavaBenchmark.graphqlAvgTime:·gc.alloc.rate.norm               avgt    3  162968.507 ±   267.677    B/op
GraphQLJavaBenchmark.graphqlAvgTime:·gc.churn.PS_Eden_Space.norm      avgt    3  163431.654 ± 16347.211    B/op
```

## Credits

* https://github.com/graphql-java/java-dataloader
