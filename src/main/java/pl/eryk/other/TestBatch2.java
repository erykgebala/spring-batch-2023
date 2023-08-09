package pl.eryk.other;

public class TestBatch2 {

    //    @Bean
//    @StepScope
//    Tasklet tasklet(@Value("#{jobParameters['date']}") Date date) {
//        return ((contribution, chunkContext) -> {
//            System.out.println("Hello world " + date);
//            return RepeatStatus.FINISHED;
//        });
//    }
//    @Bean
//    Step csvToDb(JobRepository jobRepository, PlatformTransactionManager tx,
//	 	@Value("/video_games_sales.csv") Resource resource) throws IOException {
//
//        return new StepBuilder("ctvToDb", jobRepository)
//                .<String, String>chunk(100, tx)
//				.reader(new ListItemReader<>(Arrays.asList("test", "test2")))
//				.writer(chunk -> {
//					var items = chunk.getItems();
//					System.out.println(items);
//				})
//                .build();
//    }

//    @Bean
//    Step step(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager tx) {
//        return new StepBuilder("step1", jobRepository)
//                .tasklet(tasklet, tx)
//                .build();
//    }



    //		try (var r = new InputStreamReader(resource.getInputStream())) {
//			var s = FileCopyUtils.copyToString(r);
//			var lines = s.split(System.lineSeparator());
//			System.out.println("lines " + lines.length);
//		}
}
