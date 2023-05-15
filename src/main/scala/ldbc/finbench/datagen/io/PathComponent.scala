package ldbc.finbench.datagen.io

import ldbc.finbench.datagen.model.{GraphDef, Mode}

trait PathComponent[A] {
  def path(a: A): String
}

object PathComponent {
  def apply[A: PathComponent]: PathComponent[A] = implicitly[PathComponent[A]]

  private def pure[A](f: A => String): PathComponent[A] = new PathComponent[A] {
    override def path(a: A): String = f(a)
  }

  implicit def pathComponentForGraphDef[M <: Mode]: PathComponent[GraphDef[M]] = pure((g: GraphDef[M]) => {
    val explodedPart = g match {
      case _ => "to_be_decided_fk"
    }

    val modePart = g.mode.modePath

    s"$modePart/$explodedPart"
  })
}