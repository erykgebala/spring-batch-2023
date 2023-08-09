package pl.eryk.batch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(JobLauncher jobLauncher, Job job) {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("date", new Date())
                    .toJobParameters();
            var run = jobLauncher.run(job, jobParameters);
            System.out.println("id: " + run.getJobInstance().getInstanceId());
        };
    }

    //flow
    @Bean
    Job job(JobRepository jobRepository, VideoGameBatchConfiguration batchConfiguration) {
        return new JobBuilder("job", jobRepository)
            .start(batchConfiguration.step()).on("EMPTY").to(batchConfiguration.errorStep())
            .from(batchConfiguration.step()).on("*").to(batchConfiguration.successStep())
            .build()
            .build();
    }

//    @Bean
//    Job job(JobRepository jobRepository, VideoGameBatchConfiguration batchConfiguration) {
//        return new JobBuilder("job", jobRepository)
//                .start(batchConfiguration.step())
//                .build();
//    }
}
