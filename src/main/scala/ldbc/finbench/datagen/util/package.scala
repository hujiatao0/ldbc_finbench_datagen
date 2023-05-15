package ldbc.finbench.datagen

import java.util.function.IntFunction
import com.google.common.base.CaseFormat
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession

import java.net.URI
import scala.reflect.ClassTag

package object util {
  def arrayOfSize[A: ClassTag] = new IntFunction[Array[A]] {
    override def apply(value: Int) = new Array[A](value)
  }

  def simpleNameOf[T: ClassTag] = implicitly[ClassTag[T]].runtimeClass.getSimpleName

  def pascalToCamel(str: String) = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, str)

  def camelToUpper(str: String) = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str)

  def lower(str: String) = str.toLowerCase

  def fileExists(path: String)(implicit spark: SparkSession) = {
    val hadoopPath = new Path(path)
    val fs = FileSystem.get(URI.create(path), spark.sparkContext.hadoopConfiguration)
    fs.exists(hadoopPath)
  }
}
