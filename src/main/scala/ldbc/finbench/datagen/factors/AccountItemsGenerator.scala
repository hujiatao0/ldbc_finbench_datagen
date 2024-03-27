package ldbc.finbench.datagen.factors

import org.apache.spark.sql.{SparkSession, functions => F}
import org.apache.spark.sql.functions.max
import org.apache.spark.sql.functions.lit


object AccountItemsGenerator {
  def generateAccountItems(implicit spark: SparkSession): Unit = {
    import spark.implicits._

    val accountDf = spark.read
      .format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
      .option("header", "true")
      .option("delimiter", "|")
      .load("./out/raw/account/*.csv")

    val transferDf = spark.read
      .format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
      .option("header", "true")
      .option("delimiter", "|")
      .load("./out/raw/transfer/*.csv")

    val withdrawDf = spark.read
      .format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
      .option("header", "true")
      .option("delimiter", "|")
      .load("./out/raw/withdraw/*.csv")

    val combinedDf = transferDf.select($"fromId", $"toId", $"amount".cast("double"))
      .union(withdrawDf.select($"fromId", $"toId", $"amount".cast("double")))

    val maxAmountDf = combinedDf.groupBy($"fromId", $"toId")
      .agg(max($"amount").alias("maxAmount"))

    val accountItemsDf = maxAmountDf.groupBy($"fromId")
      .agg(F.collect_list(F.array($"toId", $"maxAmount")).alias("items"))
      .select($"fromId".alias("account_id"), $"items")
      .sort($"account_id")

    val transformedAccountItemsDf = accountItemsDf.withColumn(
      "items",
      F.expr("transform(items, array -> concat('[', concat_ws(',', array), ']'))")
    ).withColumn(
      "items",
      F.concat_ws(",", $"items")
    ).withColumn(
      "items",
      F.concat(lit("["), $"items", lit("]"))
    )

    transformedAccountItemsDf
      .coalesce(1)
      .write
      .option("header", "true")
      .option("delimiter", "|")
      .format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
      .save("./out/new_factor_table/account_items")
  }
}
