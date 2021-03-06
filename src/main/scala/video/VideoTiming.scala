/*
 *    __   __     __  __     __         __
 *   /\ "-.\ \   /\ \/\ \   /\ \       /\ \
 *   \ \ \-.  \  \ \ \_\ \  \ \ \____  \ \ \____
 *    \ \_\\"\_\  \ \_____\  \ \_____\  \ \_____\
 *     \/_/ \/_/   \/_____/   \/_____/   \/_____/
 *    ______     ______       __     ______     ______     ______
 *   /\  __ \   /\  == \     /\ \   /\  ___\   /\  ___\   /\__  _\
 *   \ \ \/\ \  \ \  __<    _\_\ \  \ \  __\   \ \ \____  \/_/\ \/
 *    \ \_____\  \ \_____\ /\_____\  \ \_____\  \ \_____\    \ \_\
 *     \/_____/   \/_____/ \/_____/   \/_____/   \/_____/     \/_/
 *
 *  https://joshbassett.info
 *  https://twitter.com/nullobject
 *  https://github.com/nullobject
 *
 *  Copyright (c) 2020 Josh Bassett
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package video

import chisel3._
import chisel3.util._

/**
 * Represents the video timing signals.
 */
class Video extends Bundle {
  /** The enable signal is asserted when the beam is in the display region. */
  val enable = Bool()
  /** Position */
  val pos = new Vec2(9)
  /** The horizontal sync signal. */
  val hSync = Bool()
  /** The vertical sync signal. */
  val vSync = Bool()
  /** The horizontal blanking signal. */
  val hBlank = Bool()
  /** The vertical blanking signal. */
  val vBlank = Bool()
}

/**
 * Video timing configuration.
 *
 * @param clockFreq   The pixel clock frequency.
 * @param hFreq       The horizontal frequency.
 * @param hDisplay    The horizontal display width.
 * @param hFrontPorch The width of the horizontal front porch region.
 * @param hRetrace    The width of the horizontal retrace region.
 * @param vFreq       The vertical frequency.
 * @param vDisplay    The vertical display height.
 * @param vFrontPorch The width of the vertical front porch region.
 * @param vRetrace    The width of the vertical retrace region.
 */
case class VideoTimingConfig(clockFreq: Double,
                             hFreq: Double,
                             hDisplay: Int,
                             hFrontPorch: Int,
                             hRetrace: Int,
                             vFreq: Double,
                             vDisplay: Int,
                             vFrontPorch: Int,
                             vRetrace: Int) {
  /** Total width in pixels */
  val width = math.ceil(clockFreq / hFreq).toInt
  /** Total height in pixels */
  val height = math.ceil(hFreq / vFreq).toInt
}

/**
 * This module generates the video timing signals required for driving a
 * 15kHz CRT.
 *
 * The horizontal sync signal tells the CRT when to start a new scanline, and
 * the vertical sync signal tells it when to start a new field.
 *
 * The blanking signals indicate whether the beam is in either the horizontal or
 * vertical blanking regions. Video output is disabled while the beam is in
 * these regions.
 *
 * @param config The video timing configuration.
 */
class VideoTiming(config: VideoTimingConfig) extends Module {
  val io = IO(new Bundle {
    /** Video offset */
    val offset = Input(new SVec2(4))
    /** Video port */
    val video = Output(new Video)
  })

  // Wires
  val vSync = Wire(Bool())

  // Latching the CRT offset value during the display region can momentarily change the display
  // dimensions, which may in turn cause issues with other devices (e.g. video FIFO). To avoid this
  // problem the offset value must be latched during a vertical sync.
  val offsetReg = RegEnable(io.offset, vSync)

  // Counters
  val (x, xWrap) = Counter.static(config.width)
  val (y, yWrap) = Counter.static(config.height, enable = xWrap)

  // Horizontal regions
  val hBeginDisplay = (config.width.S - config.hDisplay.S - config.hFrontPorch.S - config.hRetrace.S + offsetReg.x).asUInt
  val hEndDisplay = (config.width.S - config.hFrontPorch.S - config.hRetrace.S + offsetReg.x).asUInt
  val hBeginSync = config.width.U - config.hRetrace.U
  val hEndSync = config.width.U

  // Vertical regions
  val vBeginDisplay = (config.height.S - config.vDisplay.S - config.vFrontPorch.S - config.vRetrace.S + io.offset.y).asUInt
  val vEndDisplay = (config.height.S - config.vFrontPorch.S - config.vRetrace.S + io.offset.y).asUInt
  val vBeginSync = config.height.U - config.vRetrace.U
  val vEndSync = config.height.U

  // Offset the position so the display region begins at the origin
  val pos = Vec2(x - hBeginDisplay, y - vBeginDisplay)

  // Sync signals
  val hSync = x >= hBeginSync && x < hEndSync
  vSync := y >= vBeginSync && y < vEndSync

  // Blanking signals
  val hBlank = x < hBeginDisplay || x >= hEndDisplay
  val vBlank = y < vBeginDisplay || y >= vEndDisplay

  // Outputs
  io.video.pos := pos
  io.video.hSync := hSync
  io.video.vSync := vSync
  io.video.hBlank := hBlank
  io.video.vBlank := vBlank
  io.video.enable := !(hBlank || vBlank)
}
