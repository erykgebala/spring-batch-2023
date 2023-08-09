package pl.eryk.batch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class VideoGameBatchConfiguration {

    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager tx;

    @Value("/video_games_sales.csv")
    Resource resource;

    public VideoGameBatchConfiguration(JobRepository jobRepository, DataSource dataSource, PlatformTransactionManager tx) {
        this.jobRepository = jobRepository;
        this.dataSource = dataSource;
        this.tx = tx;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
        return taskExecutor;
    }

    @Bean
    Step step() {
        return new StepBuilder("videGameStep", jobRepository)
            .<VideoGame, VideoGame>chunk(1000, tx)
            .reader(reader())
            .writer(writer())
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return ExitStatus.COMPLETED;
                    }
                })
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    Step errorStep() {
        return new StepBuilder("errorStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("error handler...");
                return RepeatStatus.FINISHED;
            }, tx)
            .build();
    }

    @Bean
    Step successStep() {
        return new StepBuilder("successStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("success processing...");
                return RepeatStatus.FINISHED;
            }, tx)
            .build();
    }

    @Bean
    FlatFileItemReader<VideoGame> reader() {
        return new FlatFileItemReaderBuilder<VideoGame>()
            .resource(resource)
            .name("csvReader")
            .delimited().delimiter(",")
            .names("rank,name,platform,year,genre,publisher,na_sales,eu_sales,jp_sales,other_sales,global_sales".split(","))
            .linesToSkip(1)
            .fieldSetMapper(fieldSet -> new VideoGame(
                fieldSet.readInt("rank"),
                fieldSet.readString("name"),
                fieldSet.readString("platform"),
                StringUtils.isBlank(fieldSet.readString("year")) ? 0 : fieldSet.readInt("year"),
                fieldSet.readString("genre"),
                fieldSet.readString("publisher"),
                fieldSet.readFloat("na_sales"),
                fieldSet.readFloat("eu_sales"),
                fieldSet.readFloat("jp_sales"),
                fieldSet.readFloat("other_sales"),
                fieldSet.readFloat("global_sales")
            ))
            .build();
    }

    @Bean
    JdbcBatchItemWriter<VideoGame> writer() {
        var sql = """
            insert into video_game_sales (
            rank,name,platform,year,genre,publisher,na_sales,eu_sales,jp_sales,other_sales,global_sales) 
            values (
                :rank,
                :name,
                :platform,
                :year,
                :genre,
                :publisher,
                :na_sales,
                :eu_sales,
                :jp_sales,
                :other_sales,
                :global_sales
            )
            """;
        return new JdbcBatchItemWriterBuilder<VideoGame>()
            .sql(sql)
            .dataSource(dataSource)
            .itemSqlParameterSourceProvider(item -> {
                var map = new HashMap<String, Object>();
                map.putAll(Map.of(
                    "rank", item.rank(),
                    "name", item.name().trim(),
                    "platform", item.platform().trim(),
                    "year", item.year(),
                    "genre", item.genre(),
                    "publisher", item.publisher())
                );
                map.putAll(Map.of(
                    "na_sales", item.na_sales(),
                    "eu_sales", item.eu_sales(),
                    "jp_sales", item.jp_sales(),
                    "other_sales", item.other_sales(),
                    "global_sales", item.global_sales()));
                return new MapSqlParameterSource(map);
            })
            .build();
    }
}
