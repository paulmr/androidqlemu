package smsqmulator.util

class Ticker(freq: Long = 20L, cb: () => Unit) extends Thread {
  private var running = false
  override def run = {
    running = true
    while(running) {
      Thread.sleep(freq)
      cb()
    }
  }

  def finish() = running = false
}

object Ticker {
  def fiftyHz(cb: () => Unit) = new Ticker(20, cb)
}
