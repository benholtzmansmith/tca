package lab.tca

sealed abstract class TCAError(msg: String) extends Exception(msg) {
  def message: String
}

case class InvalidInput(message: String) extends TCAError(message)
