package pl.eryk.batch;

public record VideoGame(int rank, String name, String platform, int year, String genre, String publisher,
                        float na_sales, float eu_sales, float jp_sales, float other_sales, float global_sales) {
}