package ldbc.finbench.datagen.io

import ldbc.finbench.datagen.util.fileExists
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object dataframes {

  case class DataFrameSource(
      path: String,
      format: String,
      formatOptions: Map[String, String] = Map.empty,
      schema: Option[StructType] = None
  )

  private class DataFrameReader(implicit spark: SparkSession) extends Reader[DataFrameSource] {
    override type Ret = DataFrame

    override def read(self: DataFrameSource): DataFrame = {
      spark.read
        .format(self.format)
        .options(self.formatOptions)
        .schema(self.schema.get)
        .load(self.path)
    }

    override def exists(self: DataFrameSource): Boolean = fileExists(self.path)
  }

  trait ReaderInstances {
    implicit def dataFrameReader(
        implicit spark: SparkSession): Reader.Aux[DataFrameSource, DataFrame] =
      new DataFrameReader
  }

  case class DataFrameSink(path: String,
                           format: String,
                           formatOptions: Map[String, String] = Map.empty,
                           mode: SaveMode = SaveMode.ErrorIfExists,
                           partitionBy: Seq[String] = Seq.empty)

  private object DataFrameWriter extends Writer[DataFrameSink] {
    override type Data = DataFrame
  }

  trait WriterInstances {
    implicit val dataFrameWriter: Writer.Aux[DataFrameSink, DataFrame] = DataFrameWriter
  }

  trait Instances extends WriterInstances with ReaderInstances

  object instances extends Instances
}
