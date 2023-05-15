package ldbc.finbench.datagen.factors

import ldbc.finbench.datagen.io.graphs.GraphSource
import ldbc.finbench.datagen.util.{DatagenStage, Logging}
import ldbc.finbench.datagen.model
import ldbc.finbench.datagen.model.EntityType
import org.apache.spark.sql.DataFrame
import scopt.OptionParser
import shapeless.lens

import scala.util.matching.Regex

object FactorGenerationStage extends DatagenStage with Logging{

  case class Args(
      outputDir: String = "out",
      irFormat: String = "parquet",
      format: String = "parquet",
      only: Option[Regex] = None,
      force: Boolean = false
  )

  override type ArgsType = Args

  def main(args: Array[String]): Unit = {
    val parser = new OptionParser[Args](getClass.getName.dropRight(1)) {
      head(appName)

      val args = lens[Args]

      opt[String]('o', "output-dir")
        .action((x, c) => args.outputDir.set(c)(x))
        .text(
          "path on the cluster filesystem, where Datagen outputs. Can be a URI (e.g S3, ADLS, HDFS) or a " +
            "path in which case the default cluster file system is used."
        )

      opt[String]("ir-format")
        .action((x, c) => args.irFormat.set(c)(x))
        .text("Format of the raw input")

      opt[String]("format")
        .action((x, c) => args.format.set(c)(x))
        .text("Output format")

      opt[String]("only")
        .action((x, c) => args.only.set(c)(Some(x.r.anchored)))
        .text("Only generate factor tables whose name matches the supplied regex")

      opt[Unit]("force")
        .action((_, c) => args.force.set(c)(true))
        .text("Overwrites existing output")

      help('h', "help").text("prints this usage text")
    }
    val parsedArgs =
      parser.parse(args, Args()).getOrElse(throw new RuntimeException("Invalid arguments"))

    run(parsedArgs)
  }

  // execute the Factor generation process
  override def run(args: Args): Unit = {
    import ldbc.finbench.datagen.io.Reader.ops._
    import ldbc.finbench.datagen.io.instances._

    GraphSource(model.graphs.Raw.graphDef, args.outputDir, args.irFormat)

  }

  trait FactorTrait extends (Seq[DataFrame] => DataFrame) {
    def requiredEntities: Seq[EntityType]
  }

  case class Factor(override val requiredEntities: EntityType*)(f: Seq[DataFrame] => DataFrame) extends FactorTrait {
    override def apply(v1: Seq[DataFrame]) = f(v1).coalesce(1)
  }

  case class LargeFactor(override val requiredEntities: EntityType*)(f: Seq[DataFrame] => DataFrame) extends FactorTrait {
    override def apply(v1: Seq[DataFrame]) = f(v1)
  }

  import ldbc.finbench.datagen.model.raw._

//  private val rawFactors = Map(
//    "AccountTransferAccountOut1Hops" -> Factor(AccountType, TransferType) { case Seq(accounts, transfers) =>
//
//    }
//  )
}
