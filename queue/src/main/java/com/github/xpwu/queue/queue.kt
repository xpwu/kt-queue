package com.github.xpwu.queue


import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement

private class queueCtx<T>(
  val underlying: T
) : AbstractCoroutineContextElement(queueCtx) {
  companion object Key : CoroutineContext.Key<queueCtx<*>>
}

class Queue<T>(name: String = "xpwu.queue", init: suspend ()->T){

  private val scope = CoroutineScope(CoroutineName(name))

  internal val queue: Channel<suspend (T) -> Unit> = Channel(UNLIMITED)

  init {
    // consumer
    scope.launch {
      val underlying = init()

      withContext(queueCtx(underlying)) {
        while (isActive) {
          val exe = queue.receive()
          exe(underlying)
        }
      }
    }
  }

  fun close() {
    scope.cancel()
  }
}

suspend operator fun <R, T> Queue<T>.invoke(block: suspend (T)->R): R {
  // process nest
  val ctx = currentCoroutineContext()[queueCtx.Key]
  if (ctx != null) {
    @Suppress("UNCHECKED_CAST")
    return block(ctx.underlying as T)
  }

  val ch = Channel<R>(1)
  queue.send {
    ch.send(block(it))
  }

  return ch.receive()
}

suspend fun <R, T> Queue<T>.en(block: suspend (T)->R): R {
  return this(block)
}


