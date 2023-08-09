package pl.eryk.other;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.UUID;

public class TestBatch {

//    @Bean
//    ApplicationRunner applicationRunner(JobLauncher jobLauncher, Job job) {
//        return args -> {
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("uuid", UUID.randomUUID().toString())
//                    .toJobParameters();
//            var run = jobLauncher.run(job, jobParameters);
//            System.out.println("id: " + run.getJobInstance().getInstanceId());
//        };
//    }

    //@Bean
   // @StepScope
    Tasklet tasklet(@Value("#{jobParameters['uuid']}") String uuid) {
        return ((contribution, chunkContext) -> {
            System.out.println("Hello world " + uuid);
            return RepeatStatus.FINISHED;
        });
    }

    //@Bean
    Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }

        //@Bean
        Step step1(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager tx) {
            return new StepBuilder("step1", jobRepository)
                    .tasklet(tasklet, tx)
                    .build();
        }
}
