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
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** This is the top-level module for the test pattern circuit. */
class TestPattern extends Module {
  val io = IO(new Bundle {
    /** Video signals */
    val video = Output(new Video)
    /** RGB output */
    val rgb = Output(new RGB)
  })

  val config = VideoTimingConfig(
    clockFreq = 6600000,
    hFreq = 15750,
    vFreq = 57.55,
    hDisplay = 320,
    vDisplay = 240,
    hFrontPorch = 22,
    vFrontPorch = 12,
    hRetrace = 20,
    vRetrace = 2,
  )
  val videoTiming = Module(new VideoTiming(config))
  videoTiming.io.cen := true.B
  videoTiming.io.offset := Pos.zero
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

object TestPattern extends App {
  (new ChiselStage).execute(
    Array("-X", "verilog", "--target-dir", "quartus/rtl"),
    Seq(ChiselGeneratorAnnotation(() => new TestPattern()))
  )
}
