package smsqmulator.util

class Ticker(freq: Long = 20L, cb: () => Unit) extends Thread {
  private var running = false
  private var paused  = true  // will continue to 'tick' but won't call callback; starts off paused
  override def run = {
    running = true
    while(running) {
      Thread.sleep(freq)
      if(!paused) cb()
    }
  }

  def finish = running = false
  def setPaused(paused: Boolean) = this.paused = paused
  def pause = setPaused(true)
  def unpause = setPaused(false)
}

object Ticker {
  def fiftyHz(cb: () => Unit) = new Ticker(20, cb)
}
