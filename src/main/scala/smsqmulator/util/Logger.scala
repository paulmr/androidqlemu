package smsqmulator.util

object Logger {
  type LoggerCB = String => Unit
  private var logCb: LoggerCB = (_) => ()

  def setCb(cb: LoggerCB) = logCb = cb

  def log(s: String) = logCb(s)
}
