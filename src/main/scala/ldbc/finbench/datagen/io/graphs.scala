package ldbc.finbench.datagen.io

import ldbc.finbench.datagen.io.dataframes.DataFrameSource
import ldbc.finbench.datagen.model.Mode.Raw
import ldbc.finbench.datagen.model.{Graph, GraphDef, Mode}
import ldbc.finbench.datagen.syntax.{pathSyntaxOpsForPath, pathSyntaxOpsForString}
import ldbc.finbench.datagen.util.{Logging, fileExists}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object graphs {
  
  import Reader.ops._
  import dataframes.instances._

  case class GraphSink(
      path: String,
      format: String,
      formatOptions: Map[String, String] = Map.empty,
      saveMode: SaveMode = SaveMode.ErrorIfExists
  )

  case class GraphSource[M <: Mode](definition: GraphDef[M], path: String, format: String)

  private final class GraphReader[M <: Mode](implicit spark: SparkSession, ev: DataFrame =:= M#Layout) extends Reader[GraphSource[M]] with Logging{

    override type Ret = Graph[M]

    override def read(self: GraphSource[M]): Graph[M] = {
      val entities = for { (entity, schema) <- self.definition.entities } yield {
        val p = (self.path / "graphs" / self.format / PathComponent[GraphDef[M]].path(self.definition) / entity.entityPath).toString()
        log.info(s"Reading $entity")
        val opts = getFormatOptions(self.format, self.definition.mode)
        val dfs = DataFrameSource(p, self.format, opts, schema.map(StructType.fromDDL)).read
        entity -> ev(dfs)
      }
      Graph[M](self.definition, entities)
    }

    override def exists(self: GraphSource[M]): Boolean = fileExists(self.path)

    private def getFormatOptions(format: String, mode: Mode, customFormatOptions: Map[String, String] = Map.empty) = {
      val defaultCsvFormatOptions = Map(
        "header" -> "true",
        "sep" -> "|"
      )

      val forcedRawCsvFormatOptions = Map(
        "dateFormat" -> Raw.datePattern,
        "dateTimeFormat" -> Raw.dateTimePattern
      )

      val formatOptions = (format, mode) match {
        case ("csv", Raw) => defaultCsvFormatOptions ++ customFormatOptions ++ forcedRawCsvFormatOptions
        case ("csv", _) => defaultCsvFormatOptions ++ customFormatOptions
        case _ => customFormatOptions
      }

      formatOptions
    }
  }

  trait ReaderInstances {
    implicit def graphReader[M <: Mode](implicit spark: SparkSession, ev: DataFrame =:= M#Layout): Reader.Aux[GraphSource[M], Graph[M]] =
      new GraphReader[M]
  }

  trait Instances extends ReaderInstances

  object instances extends Instances
}
