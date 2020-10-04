package com.github.mar9000.graphloader.benchmark;

import com.github.mar9000.graphloader.test.data.*;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;
import org.dataloader.MappedBatchLoader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * @author ML
 * @since 1.0.0
 */
@State(Scope.Benchmark)
public class GraphQLJavaBenchmark {
    GraphQL graphql;
    DataLoaderRegistry dlRegistry() {
        DataLoaderOptions dlOptions = new DataLoaderOptions();
        dlOptions.setCachingEnabled(false);
        DataLoaderRegistry dlRegistry = new DataLoaderRegistry();
        MappedBatchLoader<Long, Post> postLoader = new MappedBatchLoader<Long, Post>() {
            @Override
            public CompletionStage<Map<Long, Post>> load(Set<Long> keys) {
                return CompletableFuture.supplyAsync(() -> {
                    Map<Long, Post> result = new HashMap<>();
                    PostRepository.posts.values().forEach(p -> {
                        if (keys.contains(p.id))
                            result.put(p.id, p);
                    });
                    return result;
                });
            }
        };
        dlRegistry.register("postLoader", DataLoader.newMappedDataLoader(postLoader, dlOptions));
        MappedBatchLoader<Long, User> userLoader = new MappedBatchLoader<Long, User>() {
            @Override
            public CompletionStage<Map<Long, User>> load(Set<Long> keys) {
                return CompletableFuture.supplyAsync(() -> {
                    return UserRepository.get(keys).stream()
                            .collect(Collectors.toMap(user -> user.id, user -> user));
                });
            }
        };
        dlRegistry.register("userLoader", DataLoader.newMappedDataLoader(userLoader, dlOptions));
        return dlRegistry;
    };
    @Setup
    public void setup() {
        // Prepare objects that span multiple GL.resolve() invocations.
        PrepareData.defaultData();
        //
        DataLoaderDispatcherInstrumentationOptions options = DataLoaderDispatcherInstrumentationOptions
                .newOptions().includeStatistics(true);
        DataLoaderDispatcherInstrumentation dispatcherInstrumentation
                = new DataLoaderDispatcherInstrumentation(options);
        graphql = GraphQL.newGraphQL(buildSchema())
                .instrumentation(dispatcherInstrumentation)
                .build();
    }
    private GraphQLSchema buildSchema() {
        String schema = "type Query{posts(ids:[Int]): [Post]}"
                + " type Post {path: String date: String author: User}"
                + " type User {name:String}";
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);
        DataFetcher<List<Post>> postFetcher = new DataFetcher<List<Post>>() {
            @Override
            public List<Post> get(DataFetchingEnvironment environment) throws Exception {
                List<Integer> ids = environment.getArgument("ids");
                HashSet<Long> lids = new HashSet<>();
                ids.forEach(i -> lids.add(new Long(i)));
                Map<Long, Post> posts = PostRepository.load(lids);
                return new ArrayList<>(posts.values());
            }
        };
        DataFetcher<CompletableFuture<User>> authorFetcher = new DataFetcher<CompletableFuture<User>>() {
            @Override
            public CompletableFuture<User> get(DataFetchingEnvironment environment) throws Exception {
                DataLoaderRegistry registry = environment.getDataLoaderRegistry();
                DataLoader<Long, User> userLoader = registry.getDataLoader("userLoader");
                Post post = environment.getSource();
                return userLoader.load(post.authorId);
            }
        };
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("posts", postFetcher))
                .type("Post", builder -> builder.dataFetcher("author", authorFetcher))
                .build();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        return graphQLSchema;
    }
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void graphqlAvgTime(Blackhole blackhole) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("query {posts(ids:[1,2]) {date path author{name}}}")
                .dataLoaderRegistry(dlRegistry())
                .build();
        ExecutionResult result = graphql.execute(executionInput);
        if (result.getErrors().size() > 0)
            throw new RuntimeException("errors: " + result.getErrors().size());
        blackhole.consume(result.getData());
    }
}
