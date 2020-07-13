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

package tecmo

import chisel3._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** This is the top-level module for the Tecmo arcade hardware. */
class Tecmo extends Module {
  val io = IO(new Bundle {
    /** Video signals */
    val video = Output(new Video)
    /** RGB output */
    val rgb = Output(new RGB)
  })

  val config = VideoTimingConfig(
    hDisplay = 320,
    hBackPorch = 48,
    hFrontPorch = 8,
    hRetrace = 8,
    vDisplay = 240,
    vBackPorch = 12,
    vFrontPorch = 4,
    vRetrace = 8
  )
  val videoTiming = Module(new VideoTiming(config))
  videoTiming.io.cen := true.B
  val video = videoTiming.io.video

  val rgb = RGB(
    Mux(video.pos.x(2, 0) === 0.U | video.pos.y(2, 0) === 0.U, 63.U, 0.U),
    Mux(video.pos.x(4), 63.U, 0.U),
    Mux(video.pos.y(4), 63.U, 0.U),
  )

  // Outputs
  io.video := video
  io.rgb := Mux(video.enable, rgb, RGB(0.U))
}

object Tecmo extends App {
  (new ChiselStage).execute(
    Array("-X", "verilog", "--target-dir", "quartus/rtl"),
    Seq(ChiselGeneratorAnnotation(() => new Tecmo()))
  )
}
